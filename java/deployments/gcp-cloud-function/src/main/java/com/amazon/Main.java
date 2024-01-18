package com.amazon;

import com.amazon.controller.PersonController;
import com.amazon.dao.FirestorePersonDaoImpl;
import com.amazon.dao.PersonDao;
import com.amazon.mapper.GenericRequest;
import com.amazon.mapper.GenericRequestMapper;
import com.amazon.mapper.GenericResponse;
import com.amazon.mapper.HttpRequestMapper;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main implements HttpFunction {

    private final static Gson gson = new Gson();

    private final static PersonDao personDao;

    private final static PersonController personController;

    private final static GenericRequestMapper<HttpRequest> requestMapper = new HttpRequestMapper();

    static {
        personDao = new FirestorePersonDaoImpl();
        personController = new PersonController(personDao);
    }

    /**
     * Public default constructor that is required by GCP Cloud Functions.
     */
    public Main() {}

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        log.debug("Inside service method");

        try {
            GenericRequest request = requestMapper.map(httpRequest);
            GenericResponse response = new GenericResponse();

            personController.handleRequest(request, response);

            // Map generic response back to GCP HttpResponse.
            if (response.getBody() != null) {
                httpResponse.getWriter().write(response.getBody());
            }

            httpResponse.setStatusCode(response.getStatus());

            response.getHeaders().forEach(httpResponse::appendHeader);

            if (response.getHeaders() != null && response.getHeaders().get("Content-Type") != null) {
                httpResponse.setContentType(response.getHeaders().get("Content-Type"));
            }
        }
        catch (Exception e) {
            log.error("", e);
            httpResponse.setStatusCode(500);
        }

    }

}
