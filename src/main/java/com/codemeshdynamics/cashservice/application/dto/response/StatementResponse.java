package com.codemeshdynamics.cashservice.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class StatementResponse {
    private String customerName;
    private Long customerId;
    private BigDecimal totalDebits;
    private BigDecimal totalCredits;
    private List<AccountDetail> accounts;

    @Data
    @Builder
    public static class AccountDetail {
        private String accountNumber;
        private String accountType;
        private BigDecimal initialBalance;
        private Boolean active;
        private List<MovementDetail> movements;
    }

    @Data
    @Builder
    public static class MovementDetail {
        private LocalDateTime date;
        private String type;
        private BigDecimal amount;
        private BigDecimal balance;
    }
}
