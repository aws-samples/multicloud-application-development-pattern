package com.amazon.mapper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.azure.functions.HttpRequestMessage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpRequestMessageMapper implements GenericRequestMapper<HttpRequestMessage<Optional<String>>> {

    @Override
    public GenericRequest map(HttpRequestMessage<Optional<String>> httpRequest) throws IOException {
        // Instantiate body.
        var jsonBody = new JsonObject();
        if (httpRequest.getBody().isPresent() && ! httpRequest.getBody().get().isEmpty()) {
            jsonBody = JsonParser.parseString(httpRequest.getBody().get()).getAsJsonObject();
        }

        // Instantiate headers.
        Map<String, List<String>> queryParameters = httpRequest.getQueryParameters().entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                value -> List.of(value.getValue().split(","))
                        )
                );

        return new GenericRequest(
                httpRequest.getHttpMethod().toString(),
                httpRequest.getUri().getPath().replace("/api", ""),
                jsonBody,
                queryParameters
        );
    }

}
