package com.amazon.dao;

import com.amazon.model.Person;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class DynamoDbPersonDaoImpl implements PersonDao {

    private static final String PRIMARY_KEY = "id";

    private final Table table;

    public DynamoDbPersonDaoImpl() {
        String dynamoTableName = Optional.ofNullable(System.getenv("DYNAMO_TABLE_NAME"))
                .orElseThrow(() -> new IllegalArgumentException("DYNAMO_TABLE_NAME should not be null"));

        final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        final DynamoDB dynamoDB = new DynamoDB(client);
        this.table = dynamoDB.getTable(dynamoTableName);
    }

    @Override
    public Optional<Person> getById(UUID id) {
        log.debug("Getting person by id: {}", id);

        return Optional.ofNullable(this.table.getItem(PRIMARY_KEY, id.toString()))
                .map(item -> new Person(
                        UUID.fromString(item.getString(PRIMARY_KEY)),
                        item.getString("firstName"),
                        item.getString("lastName"),
                        OffsetDateTime.parse(item.getString("dateOfBirth"))
                ));
    }

    @Override
    public void save(Person person) {
        log.debug("Saving person: {}", person);

        Item item = new Item()
                .withPrimaryKey("id", person.getId().toString())
                .withString("firstName", person.getFirstName())
                .withString("lastName", person.getLastName())
                .withString("dateOfBirth", person.getDateOfBirth().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        this.table.putItem(item);
    }

}
