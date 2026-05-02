package com.codemeshdynamics.cashservice.application.dto.response;

import com.codemeshdynamics.cashservice.domain.model.Account;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountResponse {
    private Long id;
    private String number;
    private String type;
    private BigDecimal initialBalance;
    private Boolean active;
    private String customerName;

    public static AccountResponse fromEntity(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .number(account.getNumber())
                .type(account.getType().name())
                .initialBalance(account.getInitialBalance())
                .active(account.getActive())
                .customerName(account.getCustomer().getName())
                .build();
    }
}
