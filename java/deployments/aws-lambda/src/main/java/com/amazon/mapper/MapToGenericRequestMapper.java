package com.amazon.mapper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapToGenericRequestMapper implements GenericRequestMapper<Map<String, Object>> {

    private final static Gson gson = new Gson();

    @Override
    public GenericRequest map(Map<String, Object> event) {
        JsonObject bodyJson = gson.fromJson(gson.toJson(event), JsonObject.class);

        // We need to split the query string parameters after deserializing them.
        JsonObject unsplitQueryParametersJson = bodyJson.getAsJsonObject("queryStringParameters");
        Map<String, String> unsplitQueryParameters = gson.fromJson(
                unsplitQueryParametersJson,
                TypeToken.getParameterized(Map.class, String.class, String.class).getType()
        );
        Map<String, List<String>> splitQueryParameters = unsplitQueryParameters.entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                value -> List.of(value.getValue().split(","))
                        )
                );

        return new GenericRequest(
                bodyJson.get("httpMethod").getAsString(),
                bodyJson.get("path").getAsString(),
                gson.fromJson(bodyJson.getAsJsonPrimitive("body").getAsString(), JsonObject.class),
                splitQueryParameters
        );
    }

}
