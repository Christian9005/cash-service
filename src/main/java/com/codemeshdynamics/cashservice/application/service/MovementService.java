package com.codemeshdynamics.cashservice.application.service;

import com.codemeshdynamics.cashservice.application.exception.DailyLimitExceededException;
import com.codemeshdynamics.cashservice.application.exception.InsufficientBalanceException;
import com.codemeshdynamics.cashservice.domain.model.Account;
import com.codemeshdynamics.cashservice.domain.model.Movement;
import com.codemeshdynamics.cashservice.domain.model.MovementType;
import com.codemeshdynamics.cashservice.domain.repository.AccountRepository;
import com.codemeshdynamics.cashservice.domain.repository.MovementRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovementService {
    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;

    @Value("${banking.daily-withdrawal-limit:1000}")
    private BigDecimal dailyWithdrawalLimit;

    @Transactional
    public Movement registerMovement(String accountId, MovementType type, BigDecimal amount, String idempotencyKey)
    {
        log.info("Registering movement {} for account {}", type, accountId);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<Movement> existingMovement = movementRepository.findByIdempotencyKey(idempotencyKey);
            if (existingMovement.isPresent()) {
                return existingMovement.get();
            }
        }

        Account account = accountRepository.findWithLockByNumber(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        BigDecimal currentBalance = account.getInitialBalance();
        BigDecimal newBalance;

        if (type == MovementType.DEBIT) {
            if (currentBalance.compareTo(amount) < 0) {
                throw new InsufficientBalanceException("Saldo no disponible");
            }
            validateDailyWithdrawalLimit(account, amount);
            newBalance = currentBalance.subtract(amount);
        } else {
            newBalance = currentBalance.add(amount);
        }

        account.setInitialBalance(newBalance);
        accountRepository.save(account);

        Movement movement = new Movement();
        movement.setDate(LocalDateTime.now());
        movement.setType(type);
        movement.setAmount(type == MovementType.DEBIT ? amount.negate() : amount);
        movement.setBalance(newBalance);
        movement.setAccount(account);
        movement.setIdempotencyKey(idempotencyKey);

        return movementRepository.save(movement);
    }

    @Transactional
    public Movement registerMovement(String accountId, MovementType type, BigDecimal amount) {
        return registerMovement(accountId, type, amount, null);
    }

    @Transactional(readOnly = true)
    public List<Movement> findAll() {
        return movementRepository.findAll();
    }

    private void validateDailyWithdrawalLimit(Account account, BigDecimal amount) {
        LocalDate today = LocalDate.now();
        BigDecimal withdrawnToday = movementRepository.sumAmountByCustomerAndTypeBetween(
                account.getCustomer().getId(),
                MovementType.DEBIT,
                today.atStartOfDay(),
                today.atTime(23, 59, 59)
        ).abs();

        if (withdrawnToday.add(amount).compareTo(dailyWithdrawalLimit) > 0) {
            throw new DailyLimitExceededException("Cupo diario Excedido");
        }
    }
}
