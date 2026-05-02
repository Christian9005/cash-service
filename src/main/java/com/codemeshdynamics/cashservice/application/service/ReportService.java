package com.codemeshdynamics.cashservice.application.service;

import com.codemeshdynamics.cashservice.application.dto.response.StatementDocumentResponse;
import com.codemeshdynamics.cashservice.application.dto.response.StatementResponse;
import com.codemeshdynamics.cashservice.domain.model.Account;
import com.codemeshdynamics.cashservice.domain.model.Customer;
import com.codemeshdynamics.cashservice.domain.model.Movement;
import com.codemeshdynamics.cashservice.domain.model.MovementType;
import com.codemeshdynamics.cashservice.domain.repository.AccountRepository;
import com.codemeshdynamics.cashservice.domain.repository.CustomerRepository;
import com.codemeshdynamics.cashservice.domain.repository.MovementRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;

    @Transactional(readOnly = true)
    public StatementResponse generateStatementByClient(Long clientId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating statement for client ID: {} from {} to {}", clientId, startDate, endDate);

        Customer customer = customerRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + clientId));

        List<Account> accounts = accountRepository.findByCustomerId(clientId);

        if (accounts.isEmpty()) {
            throw new EntityNotFoundException("No accounts found for customer ID: " + clientId);
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<StatementResponse.AccountDetail> accountDetails = new ArrayList<>();
        BigDecimal totalDebits = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;

        for (Account account : accounts) {
            List<Movement> movements = movementRepository.findByAccountIdAndDateBetween(
                    account.getId(), start, end);

            for (Movement movement : movements) {
                if (movement.getType() == MovementType.DEBIT) {
                    totalDebits = totalDebits.add(movement.getAmount().abs());
                } else {
                    totalCredits = totalCredits.add(movement.getAmount());
                }
            }

            List<StatementResponse.MovementDetail> movementDetails = movements.stream()
                    .map(m -> StatementResponse.MovementDetail.builder()
                            .date(m.getDate())
                            .type(m.getType().name())
                            .amount(m.getAmount())
                            .balance(m.getBalance())
                            .build())
                    .toList();

            accountDetails.add(StatementResponse.AccountDetail.builder()
                    .accountNumber(account.getNumber())
                    .accountType(account.getType().name())
                    .initialBalance(account.getInitialBalance())
                    .active(account.getActive())
                    .movements(movementDetails)
                    .build());
        }

        return StatementResponse.builder()
                .customerName(customer.getName())
                .customerId(customer.getId())
                .totalDebits(totalDebits)
                .totalCredits(totalCredits)
                .accounts(accountDetails)
                .build();
    }

    @Transactional(readOnly = true)
    public StatementDocumentResponse generateStatementPdfBase64(Long clientId, LocalDate startDate, LocalDate endDate) {
        StatementResponse statement = generateStatementByClient(clientId, startDate, endDate);
        String pdfContent = buildMinimalPdf(statement, startDate, endDate);

        return StatementDocumentResponse.builder()
                .fileName("estado-cuenta-" + clientId + ".pdf")
                .contentType("application/pdf")
                .base64(Base64.getEncoder().encodeToString(pdfContent.getBytes(StandardCharsets.ISO_8859_1)))
                .build();
    }

    private String buildMinimalPdf(StatementResponse statement, LocalDate startDate, LocalDate endDate) {
        List<String> lines = new ArrayList<>();
        lines.add("Estado de cuenta");
        lines.add("Cliente: " + statement.getCustomerName() + " (ID " + statement.getCustomerId() + ")");
        lines.add("Periodo: " + startDate + " a " + endDate);
        lines.add("Total debitos: " + statement.getTotalDebits());
        lines.add("Total creditos: " + statement.getTotalCredits());
        lines.add("");

        for (StatementResponse.AccountDetail account : statement.getAccounts()) {
            lines.add("Cuenta " + account.getAccountNumber() + " - " + account.getAccountType()
                    + " - Saldo: " + account.getInitialBalance());
            for (StatementResponse.MovementDetail movement : account.getMovements()) {
                lines.add("  " + movement.getDate().toLocalDate()
                        + " | " + movement.getType()
                        + " | " + movement.getAmount()
                        + " | Disponible: " + movement.getBalance());
            }
            lines.add("");
        }

        StringBuilder stream = new StringBuilder();
        stream.append("BT\n/F1 18 Tf\n72 740 Td\n(")
                .append(escapePdf(lines.getFirst()))
                .append(") Tj\n/F1 11 Tf\n0 -24 Td\n");
        for (int i = 1; i < lines.size(); i++) {
            stream.append("(").append(escapePdf(lines.get(i))).append(") Tj\n0 -16 Td\n");
        }
        stream.append("ET");

        String contents = stream.toString();
        List<String> objects = List.of(
                "<< /Type /Catalog /Pages 2 0 R >>",
                "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
                "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] "
                        + "/Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>",
                "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>",
                "<< /Length " + contents.getBytes(StandardCharsets.ISO_8859_1).length + " >> stream\n"
                        + contents + "\nendstream"
        );

        return buildPdfDocument(objects);
    }

    private String buildPdfDocument(List<String> objects) {
        StringBuilder pdf = new StringBuilder("%PDF-1.4\n");
        List<Integer> offsets = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            offsets.add(pdf.toString().getBytes(StandardCharsets.ISO_8859_1).length);
            pdf.append(i + 1).append(" 0 obj\n")
                    .append(objects.get(i))
                    .append("\nendobj\n");
        }

        int xrefOffset = pdf.toString().getBytes(StandardCharsets.ISO_8859_1).length;
        pdf.append("xref\n0 ").append(objects.size() + 1).append("\n")
                .append("0000000000 65535 f \n");
        for (Integer offset : offsets) {
            pdf.append(String.format("%010d 00000 n \n", offset));
        }
        pdf.append("trailer << /Size ").append(objects.size() + 1)
                .append(" /Root 1 0 R >>\n")
                .append("startxref\n")
                .append(xrefOffset)
                .append("\n%%EOF");

        return pdf.toString();
    }

    private String escapePdf(String text) {
        return text.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }
}
