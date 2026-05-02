package com.codemeshdynamics.cashservice.infrastructure;

import com.codemeshdynamics.cashservice.application.dto.request.MovementRequest;
import com.codemeshdynamics.cashservice.application.exception.InsufficientBalanceException;
import com.codemeshdynamics.cashservice.application.service.MovementService;
import com.codemeshdynamics.cashservice.application.service.JwtService;
import com.codemeshdynamics.cashservice.domain.model.*;
import com.codemeshdynamics.cashservice.domain.repository.CustomerRepository;
import com.codemeshdynamics.cashservice.infrastructure.controller.MovementController;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovementController.class)
@AutoConfigureMockMvc(addFilters = false)
class MovementControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MovementService movementService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomerRepository customerRepository;

    @Test
    void registerMovement_ReturnsCreatedMovement() throws Exception {
        MovementRequest request = new MovementRequest();
        request.setAccountNumber("478758");
        request.setType(MovementType.DEBIT);
        request.setAmount(new BigDecimal("575"));

        when(movementService.registerMovement(eq("478758"), eq(MovementType.DEBIT), eq(new BigDecimal("575")), eq("move-1")))
                .thenReturn(movement("move-1"));

        mockMvc.perform(post("/api/v1/movements")
                        .header("Idempotency-Key", "move-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("DEBIT"))
                .andExpect(jsonPath("$.amount").value(-575))
                .andExpect(jsonPath("$.balance").value(1425))
                .andExpect(jsonPath("$.idempotencyKey").value("move-1"));
    }

    @Test
    void registerMovement_WhenBalanceIsUnavailable_ReturnsBadRequest() throws Exception {
        MovementRequest request = new MovementRequest();
        request.setAccountNumber("495878");
        request.setType(MovementType.DEBIT);
        request.setAmount(new BigDecimal("10"));

        when(movementService.registerMovement(anyString(), any(), any(), any()))
                .thenThrow(new InsufficientBalanceException("Saldo no disponible"));

        mockMvc.perform(post("/api/v1/movements")
                        .header("Idempotency-Key", "move-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Saldo no disponible"));
    }

    private Movement movement(String idempotencyKey) {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Jose Lema");

        Account account = new Account();
        account.setId(1L);
        account.setNumber("478758");
        account.setType(AccountType.SAVINGS);
        account.setCustomer(customer);

        Movement movement = new Movement();
        movement.setId(1L);
        movement.setDate(LocalDateTime.now());
        movement.setType(MovementType.DEBIT);
        movement.setAmount(new BigDecimal("-575"));
        movement.setBalance(new BigDecimal("1425"));
        movement.setAccount(account);
        movement.setIdempotencyKey(idempotencyKey);
        return movement;
    }
}
