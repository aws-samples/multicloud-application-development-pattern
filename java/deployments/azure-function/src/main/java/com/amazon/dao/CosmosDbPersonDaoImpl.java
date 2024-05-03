package com.amazon.dao;

import com.amazon.model.Person;
import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.PartitionKey;
import com.microsoft.azure.functions.HttpStatus;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CosmosDbPersonDaoImpl implements PersonDao {

    private final String PRIMARY_KEY = "/id";

    private static final String DATABASE_NAME = "MyCosmosDatabase";

    private static final String CONTAINER_NAME = "person";

    private final CosmosContainer cosmosContainer;

    private Logger logger;

    public CosmosDbPersonDaoImpl() {
        final String endpoint = System.getenv("COSMOS_ENDPOINT");
        final String key = System.getenv("COSMOS_KEY");

        // Instantiate client.
        CosmosClient cosmosClient = new CosmosClientBuilder()
                .endpoint(endpoint)
                .key(key)
                .consistencyLevel(ConsistencyLevel.EVENTUAL)
                .contentResponseOnWriteEnabled(true)
                .buildClient();

        // Instantiate database.
        var cosmosDatabaseResponse = cosmosClient.createDatabaseIfNotExists(DATABASE_NAME);
        CosmosDatabase cosmosDatabase = cosmosClient.getDatabase(cosmosDatabaseResponse.getProperties().getId());

        // Instantiate container.
        var cosmosContainerResponse = cosmosDatabase.createContainerIfNotExists(
                new CosmosContainerProperties(CONTAINER_NAME, PRIMARY_KEY)
        );
        this.cosmosContainer = cosmosDatabase.getContainer(cosmosContainerResponse.getProperties().getId());
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Optional<Person> getById(UUID id) {
        this.logger.info(String.format("Getting person with id: %s", id));

        CosmosItemResponse<Person> person = this.cosmosContainer.readItem(
                id.toString(),
                new PartitionKey(id.toString()),
                Person.class
        );

        this.logger.info(String.format("person is: %s", person));
        if (person == null || person.getItem() == null) {
            return Optional.empty();
        }

        return Optional.of(person.getItem());
    }

    @Override
    public void save(Person person) {
        if (person.getId() == null) {
            person.setId(UUID.randomUUID());
        }

        this.logger.info(String.format("Saving person: %s", person));
        CosmosItemResponse<Person> itemResponse = this.cosmosContainer.createItem(
                person,
                new PartitionKey(person.getId().toString()),
                new CosmosItemRequestOptions()
        );

        if (HttpStatus.CREATED.value() != itemResponse.getStatusCode()) {
            final String errorMessage = String.format(
                    "Received a %d status code when trying to persist person %s",
                    itemResponse.getStatusCode(),
                    person
            );

            this.logger.log(Level.SEVERE, errorMessage);
            throw new RuntimeException(errorMessage);
        }

    }

}
