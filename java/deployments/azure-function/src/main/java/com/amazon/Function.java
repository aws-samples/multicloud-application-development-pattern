package com.amazon;

import com.amazon.controller.PersonController;
import com.amazon.dao.CosmosDbPersonDaoImpl;
import com.amazon.dao.PersonDao;
import com.amazon.mapper.GenericResponse;
import com.amazon.mapper.HttpRequestMessageMapper;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

    private static final HttpRequestMessageMapper httpRequestMessageMapper = new HttpRequestMessageMapper();

    private static final CosmosDbPersonDaoImpl personDao;

    private static final PersonController personController;

    static {
        personDao = new CosmosDbPersonDaoImpl();
        personController = new PersonController(personDao);
    }

    @FunctionName("personApiJava")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS, route = "person") HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context
    ) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        personDao.setLogger(context.getLogger());

        var genericResponse = new GenericResponse();
        try {
            context.getLogger().info("Mapping request");
            var genericRequest = httpRequestMessageMapper.map(request);

            context.getLogger().info(genericRequest.toString());
            context.getLogger().info("Calling controller");
            personController.handleRequest(genericRequest, genericResponse);

            context.getLogger().info("Building response");
            return this.buildHttpResponseMessage(
                    request.createResponseBuilder(HttpStatusType.custom(genericResponse.getStatus())),
                    genericResponse
            );


        }
        catch (Exception e) {
            context.getLogger().log(Level.SEVERE, e.getMessage());

            var httpStatusCode = genericResponse.getStatus() != 0 ? genericResponse.getStatus() : 500;

            return this.buildHttpResponseMessage(
                    request.createResponseBuilder(HttpStatusType.custom(httpStatusCode)),
                    genericResponse
            );
        }
    }

    private HttpResponseMessage buildHttpResponseMessage(HttpResponseMessage.Builder httpResponseMessageBuilder, GenericResponse genericResponse) {
        var responseBuilder = httpResponseMessageBuilder.body(genericResponse.getBody());

        genericResponse.getHeaders().forEach(responseBuilder::header);

        return responseBuilder.build();
    }
}
