package com.codemeshdynamics.cashservice.application.dto.request;

import com.codemeshdynamics.cashservice.domain.model.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequest {
    @NotBlank(message = "Account number is required")
    private String number;

    @NotNull(message = "Account type is required")
    private AccountType type;

    @NotNull(message = "Initial balance  is required")
    @PositiveOrZero(message = "Initial balance must be zero or positive")
    private BigDecimal initialBalance;

    @NotNull(message = "ClientId is required")
    private Long customerId;

    private Boolean active = true;
}
