package com.codemeshdynamics.cashservice.infrastructure.controller;

import com.codemeshdynamics.cashservice.application.dto.response.StatementDocumentResponse;
import com.codemeshdynamics.cashservice.application.dto.response.StatementResponse;
import com.codemeshdynamics.cashservice.application.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping({"/api/v1/reports", "/api/v1/reportes"})
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/{clientId}")
    public StatementResponse getAccountStatement(
            @PathVariable Long clientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return reportService.generateStatementByClient(clientId, startDate, endDate);
    }

    @GetMapping("/{clientId}/pdf")
    public StatementDocumentResponse getAccountStatementPdf(
            @PathVariable Long clientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return reportService.generateStatementPdfBase64(clientId, startDate, endDate);
    }
}
