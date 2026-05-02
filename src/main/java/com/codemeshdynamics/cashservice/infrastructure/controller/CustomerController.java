package com.codemeshdynamics.cashservice.infrastructure.controller;

import com.codemeshdynamics.cashservice.application.dto.request.CustomerPatchRequest;
import com.codemeshdynamics.cashservice.application.dto.request.CustomerRequest;
import com.codemeshdynamics.cashservice.application.dto.response.CustomerResponse;
import com.codemeshdynamics.cashservice.application.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/customers", "/api/v1/clientes"})
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    public List<CustomerResponse> findAll() {
        return customerService.findAll().stream()
                .map(CustomerResponse::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public CustomerResponse findById(@PathVariable Long id) {
        return CustomerResponse.fromEntity(customerService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@Valid @RequestBody CustomerRequest request) {
        return CustomerResponse.fromEntity(customerService.create(request));
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        return CustomerResponse.fromEntity(customerService.update(id, request));
    }

    @PatchMapping("/{id}")
    public CustomerResponse patch(@PathVariable Long id, @RequestBody CustomerPatchRequest request) {
        return CustomerResponse.fromEntity(customerService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        customerService.delete(id);
    }
}
