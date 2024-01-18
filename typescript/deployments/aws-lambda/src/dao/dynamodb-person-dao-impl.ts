import {DynamoDB} from '@aws-sdk/client-dynamodb';
import {DynamoDBDocument, GetCommand, GetCommandOutput, PutCommand, PutCommandOutput} from "@aws-sdk/lib-dynamodb";
import {Optional} from "typescript-optional";
import {PersonDao} from "../../../../core/src/dao/person-dao";
import {Person} from "../../../../core/src/model/person";
import { v4 as uuidv4 } from 'uuid';

export class DynamodbPersonDaoImpl extends PersonDao {

    private static readonly PRIMARY_KEY: string = 'id';

    private readonly table: string = 'person'

    private readonly dynamoDbDocument: DynamoDBDocument;

    constructor() {
        super();
        const dynamoDbClient: any = new DynamoDB({});
        const marshallOptions = {
            // Whether to automatically convert empty strings, blobs, and sets to `null`.
            convertEmptyValues: false, // false, by default.
            // Whether to remove undefined values while marshalling.
            removeUndefinedValues: true, // false, by default.
            // Whether to convert typeof object to map attribute.
            convertClassInstanceToMap: true, // false, by default.
        };

        const unmarshallOptions = {
            // Whether to return numbers as a string instead of converting them to native JavaScript numbers.
            wrapNumbers: false, // false, by default.
        };
        this.dynamoDbDocument = DynamoDBDocument.from(
            dynamoDbClient,
            {
                marshallOptions,
                unmarshallOptions
            }
        );
    }

    public async getById(id: string): Promise<Optional<Person>> {
        console.log(`Getting person by id: ${id}`);

        const result: GetCommandOutput = await this.dynamoDbDocument.send(
            new GetCommand({
                TableName: this.table,
                Key: {
                    id: id,
                }
            })
        )

        console.log(result);
        if (result.Item) {
            return Optional.of(
                new Person(
                    result.Item['id'],
                    result.Item['firstName'],
                    result.Item['lastName'],
                    new Date(result.Item['dateOfBirth'])
                )
            )
        }

        return Optional.empty();
    }

    public async save(person: Person): Promise<void> {
        console.log(`Saving person: ${JSON.stringify(person)}`);

        // Give the person an ID if it doesn't have one.
        if (! person.id) {
            person.id = uuidv4().toString()
        }

        const input = {
            "TableName": this.table,
            "Item": {
                "id": (person.id) ,
                "dateOfBirth": person.dateOfBirth,
                "firstName": person.firstName,
                "lastName": person.lastName
            }
        };

        const putCommand: PutCommand = new PutCommand(input);

        const result: PutCommandOutput = await this.dynamoDbDocument.send(putCommand);

        console.log(`PutCommandOutput: ${JSON.stringify(result)}`);
    }

}