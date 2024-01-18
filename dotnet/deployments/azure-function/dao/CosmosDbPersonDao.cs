using System;
using System.Net;
using System.Threading.Tasks;
using core.dao;
using core.model;
using Microsoft.Azure.Cosmos;

namespace azure_function.dao;


public class CosmosDbPersonDao : PersonDao
{
    private static readonly string PrimaryKey = $"/{nameof(Person.id)}";

    private static readonly string CosmosDatabaseName = "MyCosmosDatabase";

    private static readonly string ContainerName = "person";

    private static readonly Database CosmosDatabase;

    private static readonly Container CosmosContainer;

    private static readonly CosmosClient CosmosClient;

    static CosmosDbPersonDao()
    {
        Console.WriteLine("Creating CosmosClient");
        
        var endpoint = Environment.GetEnvironmentVariable("COSMOS_ENDPOINT")!;
        Console.WriteLine("Endpoint: {0}", endpoint);
        
        var key = Environment.GetEnvironmentVariable("COSMOS_KEY")!;
        Console.WriteLine("Key: {0}", key);
        
        CosmosClient = new(
            accountEndpoint: endpoint,
            authKeyOrResourceToken: key,
            new CosmosClientOptions()
            {
                ApplicationRegion = Regions.EastUS,
                ConnectionMode = ConnectionMode.Gateway
            }
        );
        
        Console.WriteLine("Creating CosmosDatabase");
        CosmosDatabase = CosmosClient.CreateDatabaseIfNotExistsAsync(CosmosDatabaseName)
            .Result
            .Database;

        Console.WriteLine("Creating CosmosContainer");
        CosmosContainer = CosmosDatabase.CreateContainerIfNotExistsAsync(new ContainerProperties(ContainerName, PrimaryKey))
            .Result
            .Container;
    }
    
    public async Task<Person?> GetById(Guid id)
    {
        Console.Write("Getting person by id: {0}", id);
        
        var response = await CosmosContainer.ReadItemAsync<Person>(id.ToString(), new PartitionKey(id.ToString()));
        return response.Resource;
    }

    public async Task<bool> Save(Person person)
    {
        Console.WriteLine("Saving person: {0}", person);
        Console.WriteLine("person id is: {0}", person.id.ToString());

        var response = await CosmosContainer
            .UpsertItemAsync(person, new PartitionKey(person.id.ToString()));

        return HttpStatusCode.Created.Equals(response.StatusCode);
    }
}