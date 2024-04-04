package com.example.spring.swagger.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class APIError{
    private String message;
    private String httpStatusName;
    private int httpStatusCode;
}
