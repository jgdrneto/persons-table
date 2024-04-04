package com.frontend.models;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Person {

    private long id;
    private String name;
    private String city;
    private String email;
    private String cep;
    private String address;
    private String country;
    private String user;
    private String phone;
    
    @JsonFormat(pattern="MM/dd/yyyy")
    private LocalDate birthday;

    private long roleId;

    @JsonIgnore
    private Role objRole;

    public Person(){
        this.objRole = new Role();
    }
}
