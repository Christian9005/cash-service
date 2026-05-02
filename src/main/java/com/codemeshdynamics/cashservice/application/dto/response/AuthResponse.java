package com.codemeshdynamics.cashservice.application.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String tokenType;
    private String accessToken;
    private Long expiresIn;
}
