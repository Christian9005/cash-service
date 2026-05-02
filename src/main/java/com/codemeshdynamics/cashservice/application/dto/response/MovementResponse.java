package com.codemeshdynamics.cashservice.application.dto.response;

import com.codemeshdynamics.cashservice.domain.model.Movement;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MovementResponse {
    private Long id;
    private LocalDateTime date;
    private String type;
    private BigDecimal amount;
    private BigDecimal balance;
    private String accountNumber;
    private String idempotencyKey;

    public static MovementResponse fromEntity(Movement movement) {
        return MovementResponse.builder()
                .id(movement.getId())
                .date(movement.getDate())
                .type(movement.getType().name())
                .amount(movement.getAmount())
                .balance(movement.getBalance())
                .accountNumber(movement.getAccount().getNumber())
                .idempotencyKey(movement.getIdempotencyKey())
                .build();
    }
}
