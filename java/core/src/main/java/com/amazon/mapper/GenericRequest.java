package com.amazon.mapper;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@NoArgsConstructor
public class GenericRequest {

    private String httpMethod;

    private String path;

    private JsonObject body = new JsonObject();

    private Map<String, List<String>> queryParameters = new HashMap<>();

    public GenericRequest(
            String httpMethod,
            String path,
            JsonObject body,
            Map<String, List<String>> queryParameters
    ) {
        Objects.requireNonNull(httpMethod);
        Objects.requireNonNull(path);
        this.httpMethod = httpMethod;
        this.path = path;

        if (body != null) {
            this.body = body;
        }

        if (queryParameters != null) {
            this.queryParameters = queryParameters;
        }
    }

}
