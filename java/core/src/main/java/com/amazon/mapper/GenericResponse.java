package com.amazon.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericResponse {

    private String httpMethod;

    private int status;

    private Map<String, String> headers = new HashMap<>();

    private String body;

    public void addHeader(String headerKey, String headerValue) {
        this.getHeaders().put(headerKey, headerValue);
    }

}
