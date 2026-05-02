package com.codemeshdynamics.cashservice.application.service;

import com.codemeshdynamics.cashservice.application.dto.request.AccountRequest;
import com.codemeshdynamics.cashservice.application.dto.request.AccountPatchRequest;
import com.codemeshdynamics.cashservice.domain.model.Account;
import com.codemeshdynamics.cashservice.domain.model.Customer;
import com.codemeshdynamics.cashservice.domain.repository.AccountRepository;
import com.codemeshdynamics.cashservice.domain.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public Account findByNumber(String number) {
        return accountRepository.findByNumber(number)
                .orElseThrow(() -> new EntityNotFoundException("Account not found: " + number));
    }

    @Transactional
    public Account create(AccountRequest request) {
        log.info("Creating account: {}", request.getNumber());

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        Account account = new Account();
        account.setNumber(request.getNumber());
        account.setType(request.getType());
        account.setInitialBalance(request.getInitialBalance());
        account.setActive(request.getActive());
        account.setCustomer(customer);

        return accountRepository.save(account);
    }

    @Transactional
    public Account update(Long id, AccountRequest request) {
        log.info("Updating account ID: {}", id);

        Account account = findById(id);

        if (request.getCustomerId() != null) {
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new EntityNotFoundException("Client not found"));
            account.setCustomer(customer);
        }

        account.setNumber(request.getNumber());
        account.setType(request.getType());
        account.setInitialBalance(request.getInitialBalance());
        account.setActive(request.getActive());

        return accountRepository.save(account);
    }

    @Transactional
    public Account patch(Long id, AccountPatchRequest request) {
        log.info("Partially updating account ID: {}", id);

        Account account = findById(id);

        if (request.getCustomerId() != null) {
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new EntityNotFoundException("Client not found"));
            account.setCustomer(customer);
        }
        if (request.getNumber() != null) {
            account.setNumber(request.getNumber());
        }
        if (request.getType() != null) {
            account.setType(request.getType());
        }
        if (request.getInitialBalance() != null) {
            account.setInitialBalance(request.getInitialBalance());
        }
        if (request.getActive() != null) {
            account.setActive(request.getActive());
        }

        return accountRepository.save(account);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting account ID: {}", id);
        Account account = findById(id);
        accountRepository.delete(account);
    }
}
