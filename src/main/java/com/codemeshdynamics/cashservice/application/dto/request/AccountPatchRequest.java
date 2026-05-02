package com.codemeshdynamics.cashservice.application.dto.request;

import com.codemeshdynamics.cashservice.domain.model.AccountType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountPatchRequest {
    private String number;
    private AccountType type;
    private BigDecimal initialBalance;
    private Long customerId;
    private Boolean active;
}
