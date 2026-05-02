package com.codemeshdynamics.cashservice.application.dto.request;

import lombok.Data;

@Data
public class CustomerPatchRequest {
    private String name;
    private String gender;
    private Integer age;
    private String address;
    private String phone;
    private String password;
    private Boolean active;
}
