package com.codemeshdynamics.cashservice.application.service;

import com.codemeshdynamics.cashservice.application.dto.request.CustomerRequest;
import com.codemeshdynamics.cashservice.application.dto.request.CustomerPatchRequest;
import com.codemeshdynamics.cashservice.domain.model.Customer;
import com.codemeshdynamics.cashservice.domain.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found with ID: " + id));
    }

    @Transactional
    public Customer create(CustomerRequest request) {
        log.info("Creating client: {}", request.getName());

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setGender(request.getGender());
        customer.setAge(request.getAge());
        customer.setIdentification(request.getIdentification());
        customer.setAddress(request.getAddress());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setPhone(request.getPhone());
        customer.setActive(request.getActive());

        return customerRepository.save(customer);
    }

    @Transactional
    public Customer update(Long id, CustomerRequest request) {
        log.info("Updating client ID: {}", id);

        Customer customer = findById(id);
        customer.setName(request.getName());
        customer.setGender(request.getGender());
        customer.setAge(request.getAge());
        customer.setIdentification(request.getIdentification());
        customer.setAddress(request.getAddress());
        customer.setPhone(request.getPhone());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            customer.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        customer.setActive(request.getActive());

        return customerRepository.save(customer);
    }

    @Transactional
    public Customer patch(Long id, CustomerPatchRequest request) {
        log.info("Partially updating client ID: {}", id);

        Customer customer = findById(id);
        if (request.getName() != null) {
            customer.setName(request.getName());
        }
        if (request.getGender() != null) {
            customer.setGender(request.getGender());
        }
        if (request.getAge() != null) {
            customer.setAge(request.getAge());
        }
        if (request.getAddress() != null) {
            customer.setAddress(request.getAddress());
        }
        if (request.getPhone() != null) {
            customer.setPhone(request.getPhone());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            customer.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getActive() != null) {
            customer.setActive(request.getActive());
        }

        return customerRepository.save(customer);
    }

    @Transactional
    public void delete(Long id){
        log.info("Deleting client ID: {}", id);
        Customer customer = findById(id);
        customerRepository.delete(customer);
    }
}
