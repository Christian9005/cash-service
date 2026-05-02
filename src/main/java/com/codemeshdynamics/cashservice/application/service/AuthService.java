package com.codemeshdynamics.cashservice.application.service;

import com.codemeshdynamics.cashservice.application.dto.request.AuthRequest;
import com.codemeshdynamics.cashservice.application.dto.response.AuthResponse;
import com.codemeshdynamics.cashservice.domain.model.Customer;
import com.codemeshdynamics.cashservice.domain.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse login(AuthRequest request) {
        Customer customer = customerRepository.findByIdentification(request.getIdentification())
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        if (!Boolean.TRUE.equals(customer.getActive())) {
            throw new BadCredentialsException("Inactive client");
        }
        if (!passwordMatches(request.getPassword(), customer)) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return AuthResponse.builder()
                .tokenType("Bearer")
                .accessToken(jwtService.generateToken(customer))
                .expiresIn(jwtService.expiresInSeconds())
                .build();
    }

    private boolean passwordMatches(String rawPassword, Customer customer) {
        String storedPassword = customer.getPassword();
        if (storedPassword != null && storedPassword.startsWith("$2")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        if (rawPassword.equals(storedPassword)) {
            customer.setPassword(passwordEncoder.encode(rawPassword));
            customerRepository.save(customer);
            return true;
        }
        return false;
    }
}
