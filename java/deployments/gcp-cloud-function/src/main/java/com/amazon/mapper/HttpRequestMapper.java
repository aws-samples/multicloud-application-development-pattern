package com.amazon.mapper;

import com.google.cloud.functions.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

public class HttpRequestMapper implements GenericRequestMapper<HttpRequest> {

    private final Gson gson = new Gson();

    @Override
    public GenericRequest map(HttpRequest httpRequest) throws IOException {
        return new GenericRequest(
                httpRequest.getMethod(),
                httpRequest.getPath(),
                this.gson.fromJson(httpRequest.getReader(), JsonObject.class),
                httpRequest.getQueryParameters()
        );
    }

}
