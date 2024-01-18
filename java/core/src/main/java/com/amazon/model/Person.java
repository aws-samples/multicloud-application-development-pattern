package com.amazon.model;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class Person {

    private UUID id;

    private String firstName;

    private String lastName;

    private OffsetDateTime dateOfBirth;

    public Person() {
        this.id = UUID.randomUUID();
    }

    public Person(
            String firstName,
            String lastName,
            OffsetDateTime dateOfBirth
    ) {
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
    }

    public Person(
            UUID id,
            String firstName,
            String lastName,
            OffsetDateTime offsetDateTime
    ) {
        this(firstName, lastName, offsetDateTime);
        this.id = id;
    }

}
