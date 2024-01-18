import {PersonDao} from "../../../../core/src/dao/person-dao";
import {Optional} from "typescript-optional";
import {Person} from "../../../../core/src/model/person";
import {Firestore} from "@google-cloud/firestore";
import {v4 as uuidv4} from "uuid";


export class FirestorePersonDaoImpl extends PersonDao {

    private readonly table: string = 'person';

    private readonly firestoreDatabase: Firestore;

    constructor() {
        super();
        this.firestoreDatabase = new Firestore();
    }

    public async getById(id: string): Promise<Optional<Person>> {
        console.log(`Getting person by id: ${id}`);

        const document: FirebaseFirestore.DocumentSnapshot<FirebaseFirestore.DocumentData> = await this.firestoreDatabase
            .collection(this.table)
            .doc(id)
            .get();

        console.log(document);

        if (document.exists) {
            return Optional.of(
                new Person(
                    document.id,
                    document.get('firstName'),
                    document.get('lastName'),
                    new Date(document.get('dateOfBirth'))
                )
            )
        }

        return Optional.empty();
    }

    public async save(person: Person): Promise<void> {
        console.log('Saving person')

        // Give the person an ID if it doesn't have one.
        if (! person.id) {
            person.id = uuidv4().toString()
        }

        await this.firestoreDatabase
            .collection(this.table)
            .doc(person.id) // Unique ID/primary key of document/object.
            .set({
                "dateOfBirth": person.dateOfBirth,
                "firstName": person.firstName,
                "lastName": person.lastName
            })
            .then(documentReference => {
                console.log(`Document write time: ${documentReference.writeTime}`);
            });
    }

}