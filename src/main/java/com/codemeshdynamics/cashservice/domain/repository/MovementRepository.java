package com.codemeshdynamics.cashservice.domain.repository;

import com.codemeshdynamics.cashservice.domain.model.Movement;
import com.codemeshdynamics.cashservice.domain.model.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovementRepository extends JpaRepository<Movement, Long> {
    List<Movement> findByAccountIdAndDateBetween(
            Long accountId,
            LocalDateTime start,
            LocalDateTime end
    );

    Optional<Movement> findByIdempotencyKey(String idempotencyKey);

    @Query("""
            select coalesce(sum(m.amount), 0)
            from Movement m
            where m.account.customer.id = :customerId
              and m.type = :type
              and m.date between :start and :end
            """)
    BigDecimal sumAmountByCustomerAndTypeBetween(
            @Param("customerId") Long customerId,
            @Param("type") MovementType type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
