package com.codemeshdynamics.cashservice.application.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatementDocumentResponse {
    private String fileName;
    private String contentType;
    private String base64;
}
