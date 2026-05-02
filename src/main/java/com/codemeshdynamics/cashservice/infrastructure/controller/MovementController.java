package com.codemeshdynamics.cashservice.infrastructure.controller;

import com.codemeshdynamics.cashservice.application.dto.request.MovementRequest;
import com.codemeshdynamics.cashservice.application.dto.response.MovementResponse;
import com.codemeshdynamics.cashservice.application.service.MovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/movements", "/api/v1/movimientos"})
@RequiredArgsConstructor
public class MovementController {
    private final MovementService movementService;

    @GetMapping
    public List<MovementResponse> findAll() {
        return movementService.findAll().stream()
                .map(MovementResponse::fromEntity)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovementResponse register(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody MovementRequest request
    ) {
        return MovementResponse.fromEntity(movementService.registerMovement(
                request.getAccountNumber(),
                request.getType(),
                request.getAmount(),
                idempotencyKey
        ));
    }
}
