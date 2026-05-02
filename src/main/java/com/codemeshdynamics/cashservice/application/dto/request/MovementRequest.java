package com.codemeshdynamics.cashservice.application.dto.request;

import com.codemeshdynamics.cashservice.domain.model.MovementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MovementRequest {
    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Type of movement is required")
    private MovementType type;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;
}
