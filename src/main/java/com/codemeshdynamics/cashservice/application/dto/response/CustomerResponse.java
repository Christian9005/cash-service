package com.codemeshdynamics.cashservice.application.dto.response;

import com.codemeshdynamics.cashservice.domain.model.Customer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerResponse {
    private Long id;
    private String name;
    private String gender;
    private Integer age;
    private String identification;
    private String address;
    private String phone;
    private Boolean active;

    public static CustomerResponse fromEntity(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .gender(customer.getGender())
                .age(customer.getAge())
                .identification(customer.getIdentification())
                .address(customer.getAddress())
                .phone(customer.getPhone())
                .active(customer.getActive())
                .build();
    }
}
