import {PersonDao} from "../../../../../core/src/dao/person-dao";
import {Optional} from "typescript-optional";
import {Person} from "../../../../../core/src/model/person";
import { CosmosClient, Database } from "@azure/cosmos";
import { v4 as uuidv4 } from 'uuid';

export class CosmosDbPersonDaoImpl implements PersonDao {

    private static readonly primaryKey: string = "/id";

    private static readonly cosmosDatabaseName: string = "MyCosmosDatabase";

    private static readonly containerName: string = "person";

    private cosmosDatabase: Database;

    private cosmosContainer;

    private cosmosClient: CosmosClient;

    public constructor() {
        const endpoint: string = process.env.COSMOS_ENDPOINT;
        console.log(`endpoint is: ${endpoint}`);

        const key: string = process.env.COSMOS_KEY;
        console.log(`key found? ${key !== null}`);

        this.cosmosClient = new CosmosClient({ endpoint, key });
    }

    // public static async init(): Promise<CosmosDbPersonDaoImpl> {
    //     return await new CosmosDbPersonDaoImpl().setUpDatabase();
    // }

    public async getById(id: string): Promise<Optional<Person>> {
        // todo: move this to a static initializer.  This is just a temporary workaround until I figure out top-level awaits.
        await this.setUpDatabase();

        console.log(`Getting person from cosmos by id: ${id}`);

        const itemResponse = await this.cosmosContainer.item(id, id).read();
        console.log(`itemResponse: ${itemResponse}`);
        const personResource = itemResponse.resource;
        if (personResource) {
            console.log(`Found person: ${JSON.stringify(personResource)}`);

            const person: Person = new Person(
                personResource.id,
                personResource.firstName,
                personResource.lastName,
                personResource.dateOfBirth
            )
            return Optional.of(person);
        }
        else {
            console.error(`Did not find person with id: ${id}`);

            return Optional.empty();
        }
    }

    public async save(person: Person): Promise<void> {
        // todo: move this to a static initializer.  This is just a temporary workaround until I figure out top-level awaits.
        await this.setUpDatabase();

        // Give the person an ID if it doesn't have one.
        if (! person.id) {
            person.id = uuidv4().toString()
        }

        console.log(`Saving person to cosmos database: ${JSON.stringify(person)}`);

        await this.cosmosContainer.items.create(person);
    }

    private async setUpDatabase(): Promise<CosmosDbPersonDaoImpl> {
        if (! this.cosmosDatabase) {
            console.log('Creating Cosmos database');
            const databaseResponse = await this.cosmosClient.databases.createIfNotExists({
                id: CosmosDbPersonDaoImpl.cosmosDatabaseName
            });
            this.cosmosDatabase = databaseResponse.database;
        }

        if (! this.cosmosContainer) {
            console.log('Creating Cosmos container');
            const containerResponse= await this.cosmosDatabase.containers.createIfNotExists({
                id: CosmosDbPersonDaoImpl.containerName
            });
            this.cosmosContainer = containerResponse.container;
        }

        return this;
    }

}