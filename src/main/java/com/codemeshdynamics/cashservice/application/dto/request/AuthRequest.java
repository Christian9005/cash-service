package com.codemeshdynamics.cashservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank(message = "Identification is required")
    private String identification;

    @NotBlank(message = "Password is required")
    private String password;
}
