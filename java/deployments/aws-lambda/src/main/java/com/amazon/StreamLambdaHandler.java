package com.amazon;

import com.amazon.controller.PersonController;
import com.amazon.dao.DynamoDbPersonDaoImpl;
import com.amazon.dao.PersonDao;
import com.amazon.mapper.GenericRequest;
import com.amazon.mapper.GenericResponse;
import com.amazon.mapper.MapToGenericRequestMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class StreamLambdaHandler implements RequestHandler<Map<String, Object>, GenericResponse> {

    private final static MapToGenericRequestMapper mapToGenericRequestMapper = new MapToGenericRequestMapper();

    private final static PersonDao personDao;

    private final static PersonController personController;

    static {
            log.debug("Inside static block.  Starting to initialize dependency tree");

            personDao = new DynamoDbPersonDaoImpl();
            personController = new PersonController(personDao);
    }

    @Override
    public GenericResponse handleRequest(Map<String, Object> event, Context context) {
        log.debug("Inside handleRequest method");

        final GenericResponse response = new GenericResponse();
        try {
            final GenericRequest request = mapToGenericRequestMapper.map(event);

            personController.handleRequest(request, response);

            return response;
        }
        catch (Exception e) {
            log.error("", e);

            response.setStatus(500);
            return response;
        }
    }
}