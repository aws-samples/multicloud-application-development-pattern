package com.amazon.controller;

import com.amazon.dao.PersonDao;
import com.amazon.mapper.GenericRequest;
import com.amazon.mapper.GenericResponse;
import com.amazon.model.Person;
import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class PersonController {

    private static final String GET = "GET";

    private static final String POST = "POST";

    private static final String PERSON_ROUTE = "/person";

    private static final String ID = "id";

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(
                    OffsetDateTime.class,
                    (JsonDeserializer<OffsetDateTime>) (json, type, context) -> OffsetDateTime.parse(json.getAsString())
            )
            .registerTypeAdapter(
                    OffsetDateTime.class,
                    (JsonSerializer<OffsetDateTime>) (object, type, context) -> new JsonPrimitive(object.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
            )
            .create();

    private final PersonDao personDao;

    public PersonController(PersonDao personDao) {
        this.personDao = personDao;
    }

    /**
     * Contains the API routes/handlers - usually based on the request's HTTP method and the URI/path.
     *
     * @param request {@link GenericRequest}
     * @param response {@link GenericResponse}
     */
    public void handleRequest(GenericRequest request, GenericResponse response) {
        // GET /person?id={id}
        if (GET.equals(request.getHttpMethod()) && PERSON_ROUTE.equals(request.getPath())) {
            String id = request.getQueryParameters().get(ID).get(0);  // todo: check if it's okay to get the 0th element.
            UUID uuid = UUID.fromString(id);

            this.personDao.getById(uuid)
                    .ifPresentOrElse(
                            person -> {
                                log.debug("{}", person);

                                response.setStatus(200);

                                String personJson = gson.toJson(person);
                                log.debug(personJson);

                                response.setBody(personJson);
                            },
                            () -> response.setStatus(404)
                    );
        }
        // POST /person
        else if (POST.equals(request.getHttpMethod()) && PERSON_ROUTE.equals(request.getPath())) {
            JsonObject body = request.getBody();

            Person person = gson.fromJson(body, Person.class);
            this.personDao.save(person);

            response.setStatus(201);
            response.setBody(
                    gson.toJson(
                            Map.of(
                                    ID, person.getId().toString()
                            )
                    )
            );
        }
        // If the HTTP method and path cannot be found, then return a 404.
        else {
            log.debug("No endpoint matches {} {}", request.getHttpMethod(), request.getPath());
            response.setStatus(404);
        }

        beforeResponse(request, response);
    }

    /**
     * This method contains logic that should run AFTER the request has been handled, but BEFORE the response is sent back
     * to the client.
     *
     * @param request {@link GenericRequest}
     * @param response {@link GenericResponse}
     */
    private static void beforeResponse(GenericRequest request, GenericResponse response) {
        response.addHeader("Content-Type", "application/json");
    }

}

