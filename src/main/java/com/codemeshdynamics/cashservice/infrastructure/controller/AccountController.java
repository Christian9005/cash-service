package com.codemeshdynamics.cashservice.infrastructure.controller;

import com.codemeshdynamics.cashservice.application.dto.request.AccountPatchRequest;
import com.codemeshdynamics.cashservice.application.dto.request.AccountRequest;
import com.codemeshdynamics.cashservice.application.dto.response.AccountResponse;
import com.codemeshdynamics.cashservice.application.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/accounts", "/api/v1/cuentas"})
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping
    public List<AccountResponse> findAll() {
        return accountService.findAll().stream()
                .map(AccountResponse::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public AccountResponse findById(@PathVariable Long id) {
        return AccountResponse.fromEntity(accountService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody AccountRequest request) {
        return AccountResponse.fromEntity(accountService.create(request));
    }

    @PutMapping("/{id}")
    public AccountResponse update(@PathVariable Long id, @Valid @RequestBody AccountRequest request) {
        return AccountResponse.fromEntity(accountService.update(id, request));
    }

    @PatchMapping("/{id}")
    public AccountResponse patch(@PathVariable Long id, @RequestBody AccountPatchRequest request) {
        return AccountResponse.fromEntity(accountService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        accountService.delete(id);
    }
}
