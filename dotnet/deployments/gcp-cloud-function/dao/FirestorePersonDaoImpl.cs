using core.dao;
using core.model;
using Google.Cloud.Firestore;
using Newtonsoft.Json;

namespace gcp_cloud_function;

public class FirestorePersonDaoImpl : PersonDao
{
    private readonly FirestoreDb _firestoreDb = FirestoreDb.Create();

    private static readonly string PERSON_TABLE_NAME = "person";

    private static readonly string ID_FIELD_NAME = "id";

    public async Task<Person?> GetById(Guid id)
    {
        Console.WriteLine("Getting person by id {0}", id);
        
        var snapshot = await _firestoreDb.Collection(PERSON_TABLE_NAME)
            .Document(id.ToString())
            .GetSnapshotAsync();

        Console.WriteLine("snapshot: {0}", snapshot.Exists);
        if (snapshot.Exists)
        {
            return new Person(
                Guid.Parse(snapshot.Id),
                snapshot.GetValue<string>("firstName"),
                snapshot.GetValue<string>("lastName"),
                 DateTimeOffset.FromUnixTimeSeconds(snapshot.GetValue<int>("dateOfBirth"))
            );
        }

        return null;
    }

    public async Task<bool> Save(Person person)
    {
        Console.WriteLine("Saving person {0}", person);

        var personObj = new
        {
            firstName = person.FirstName,
            lastName = person.LastName,
            dateOfBirth = person.DateOfBirth?.ToUnixTimeSeconds()
        };
        
        Console.WriteLine("personObj is {0}", personObj);
        
        await _firestoreDb.Collection(PERSON_TABLE_NAME)
            .Document(person.id.ToString())
            .SetAsync(personObj);
        
        return true;
    }
}