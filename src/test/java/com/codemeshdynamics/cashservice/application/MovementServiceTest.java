package com.codemeshdynamics.cashservice.application;

import com.codemeshdynamics.cashservice.application.exception.DailyLimitExceededException;
import com.codemeshdynamics.cashservice.application.exception.InsufficientBalanceException;
import com.codemeshdynamics.cashservice.application.service.MovementService;
import com.codemeshdynamics.cashservice.domain.model.*;
import com.codemeshdynamics.cashservice.domain.repository.AccountRepository;
import com.codemeshdynamics.cashservice.domain.repository.MovementRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovementServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private MovementRepository movementRepository;

    @InjectMocks
    private MovementService movementService;

    private Account testAccount;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("John Doe");
        testCustomer.setIdentification("1234567890");
        testCustomer.setActive(true);

        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setNumber("478758");
        testAccount.setType(AccountType.valueOf("SAVINGS"));
        testAccount.setInitialBalance(new BigDecimal("2000"));
        testAccount.setActive(true);
        testAccount.setCustomer(testCustomer);

        ReflectionTestUtils.setField(movementService, "dailyWithdrawalLimit", new BigDecimal("1000"));
    }

    @Test
    @DisplayName("Should register credit movement successfully")
    void registerMovement_Credit_Success() {
        // Arrange
        BigDecimal amount = new BigDecimal("500");
        when(accountRepository.findWithLockByNumber("478758")).thenReturn(Optional.of(testAccount));
        when(movementRepository.save(any(Movement.class))).thenAnswer(invocation -> {
            Movement m = invocation.getArgument(0);
            m.setId(1L);
            return m;
        });

        // Act
        Movement result = movementService.registerMovement("478758", MovementType.valueOf("CREDIT"), amount);

        // Assert
        assertNotNull(result);
        assertEquals(MovementType.CREDIT, result.getType());
        assertEquals(amount, result.getAmount());
        assertEquals(new BigDecimal("2500"), result.getBalance());
        verify(accountRepository).save(testAccount);
        verify(movementRepository).save(any(Movement.class));
    }

    @Test
    @DisplayName("Should register debit movement successfully")
    void registerMovement_Debit_Success() {
        // Arrange
        BigDecimal amount = new BigDecimal("500");
        when(accountRepository.findWithLockByNumber("478758")).thenReturn(Optional.of(testAccount));
        when(movementRepository.sumAmountByCustomerAndTypeBetween(any(), eq(MovementType.DEBIT), any(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(movementRepository.save(any(Movement.class))).thenAnswer(invocation -> {
            Movement m = invocation.getArgument(0);
            m.setId(1L);
            return m;
        });

        // Act
        Movement result = movementService.registerMovement("478758", MovementType.valueOf("DEBIT"), amount);

        // Assert
        assertNotNull(result);
        assertEquals(MovementType.DEBIT, result.getType());
        assertEquals(amount.negate(), result.getAmount());
        assertEquals(new BigDecimal("1500"), result.getBalance());
        verify(accountRepository).save(testAccount);
    }

    @Test
    @DisplayName("Should throw InsufficientBalanceException when debit exceeds balance")
    void registerMovement_Debit_InsufficientBalance() {
        // Arrange
        BigDecimal amount = new BigDecimal("3000");
        when(accountRepository.findWithLockByNumber("478758")).thenReturn(Optional.of(testAccount));

        // Act & Assert
        assertThrows(InsufficientBalanceException.class, () ->
                movementService.registerMovement("478758", MovementType.valueOf("DEBIT"), amount)
        );
        verify(movementRepository, never()).save(any(Movement.class));
    }

    @Test
    @DisplayName("Should throw DailyLimitExceededException when daily debit limit is exceeded")
    void registerMovement_Debit_DailyLimitExceeded() {
        // Arrange
        BigDecimal amount = new BigDecimal("200");
        when(accountRepository.findWithLockByNumber("478758")).thenReturn(Optional.of(testAccount));
        when(movementRepository.sumAmountByCustomerAndTypeBetween(any(), eq(MovementType.DEBIT), any(), any()))
                .thenReturn(new BigDecimal("900"));

        // Act & Assert
        assertThrows(DailyLimitExceededException.class, () ->
                movementService.registerMovement("478758", MovementType.valueOf("DEBIT"), amount)
        );
        verify(accountRepository, never()).save(any(Account.class));
        verify(movementRepository, never()).save(any(Movement.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when account not found")
    void registerMovement_AccountNotFound() {
        // Arrange
        when(accountRepository.findWithLockByNumber("999999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                movementService.registerMovement("999999", MovementType.valueOf("CREDIT"), new BigDecimal("100"))
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when amount is zero or negative")
    void registerMovement_InvalidAmount() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                movementService.registerMovement("478758", MovementType.valueOf("CREDIT"), BigDecimal.ZERO)
        );

        assertThrows(IllegalArgumentException.class, () ->
                movementService.registerMovement("478758", MovementType.valueOf("CREDIT"), new BigDecimal("-100"))
        );
    }
}
