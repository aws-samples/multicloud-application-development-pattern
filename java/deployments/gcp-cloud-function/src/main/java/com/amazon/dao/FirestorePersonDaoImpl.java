package com.amazon.dao;

import com.amazon.model.Person;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.WriteResult;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
public class FirestorePersonDaoImpl implements PersonDao {

    private final Firestore firestoreDatabase;

    private final static String PERSON_TABLE_NAME = "person";

    private final static String ID_FIELD_NAME = "id";

    public FirestorePersonDaoImpl() {
        this.firestoreDatabase = FirestoreOptions.getDefaultInstance().getService();
    }

    @Override
    public Optional<Person> getById(UUID id) {
        try {
            ApiFuture<DocumentSnapshot> documentSnapshotFuture = this.firestoreDatabase.collection(PERSON_TABLE_NAME)
                    .document(id.toString())
                    .get();

            DocumentSnapshot documentSnapshot = documentSnapshotFuture.get();
            if (documentSnapshot.exists()) {
                Long dateOfBirthEpochSeconds = documentSnapshot.getLong("dateOfBirth");
                Objects.requireNonNull(dateOfBirthEpochSeconds);

                Person person = new Person(
                        UUID.fromString(documentSnapshot.getId()),
                        documentSnapshot.getString("firstName"),
                        documentSnapshot.getString("lastName"),
                        OffsetDateTime.of(
                                LocalDateTime.ofEpochSecond(dateOfBirthEpochSeconds, 0, ZoneOffset.UTC),
                                ZoneOffset.UTC
                        )
                );

                return Optional.of(person);
            }

            return Optional.empty();
        }
        catch (ExecutionException | InterruptedException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Person person) {
        try {
            ApiFuture<WriteResult> writeResultFuture = this.firestoreDatabase.collection(PERSON_TABLE_NAME)
                    .document(person.getId().toString())
                    .set(
                            Map.of(
                                    "firstName", person.getFirstName(),
                                    "lastName", person.getLastName(),
                                    "dateOfBirth", person.getDateOfBirth().toEpochSecond()
                            )
                    );

            WriteResult writeResult = writeResultFuture.get();
            log.info("Successfully wrote person to database at {}: {}", writeResult.getUpdateTime(), person);
        }
        catch (ExecutionException | InterruptedException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }

}
