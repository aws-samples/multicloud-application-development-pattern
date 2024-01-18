using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;
using core.dao;
using core.model;

namespace aws_lambda.dao;

public class DynamoDbPersonDao : PersonDao
{
    private static readonly string PRIMARY_KEY = "id";

    private static readonly string TABLE_NAME = "person";

    private readonly AmazonDynamoDBClient _dynamoDbClient = new();

    public async Task<Person?> GetById(Guid id)
    {
        var request = new GetItemRequest
        {
            TableName = TABLE_NAME,
            Key = new Dictionary<string, AttributeValue>()
            {
                { "id", new AttributeValue() { S = id.ToString() } }
            }
        };

        var response = await _dynamoDbClient.GetItemAsync(request);

        if (response.Item == null)
        {
            return null;
        }
        
        var item = response.Item;
        return new Person(
            Guid.Parse(item["id"].S),
            item["firstName"].S,
            item["lastName"].S,
            DateTimeOffset.Parse(item["dateOfBirth"].S)
        );
    }

    public async Task<bool> Save(Person person)
    {
        Console.WriteLine("person uuid before saving is: {0}", person.id);
        Console.WriteLine("person uuid before savings is null: {0}", person.id == null);
        var request = new PutItemRequest
        {
            TableName = TABLE_NAME,
            Item = new Dictionary<string, AttributeValue>
            {
                { "id", new AttributeValue { S = person.id.ToString() } },
                { "firstName", new AttributeValue { S = person.FirstName } },
                { "lastName", new AttributeValue { S = person.LastName } },
                { "dateOfBirth", new AttributeValue { S = person.DateOfBirth.ToString() } }
            }
        };

        var response = await _dynamoDbClient.PutItemAsync(request);
        return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
    }
}