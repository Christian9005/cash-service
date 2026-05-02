package com.codemeshdynamics.cashservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public class Person {
    @Column(nullable = false)
    private String name;

    private String gender;

    private Integer age;

    @Column(nullable = false, unique = true)
    private String identification;

    private String address;
    private String phone;
}
