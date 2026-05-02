package com.codemeshdynamics.cashservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String gender;

    @PositiveOrZero(message = "Age must be zero or positive")
    private Integer age;

    @NotBlank(message = "Identification is required")
    private String identification;

    private String address;

    @Size(max = 20, message = "Phone must be at most 20 characters")
    private String phone;

    @NotBlank(message = "Password is required")
    private String password;

    private Boolean active = true;
}
