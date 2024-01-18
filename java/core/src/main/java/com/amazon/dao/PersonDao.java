package com.amazon.dao;

import com.amazon.model.Person;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public interface PersonDao {

    Optional<Person> getById(UUID id);

    void save(Person person);

}
