package com.example.spring.swagger.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PersonWithRoleId {

    private long id;

    private String name;
    private String city;
    private String email;
    private String cep;
    private String address;
    private String country;
    private String user;
    private String phone;
    private String birthday;
    
    private long roleId;

}
