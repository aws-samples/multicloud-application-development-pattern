namespace core.model;

public record Person
{
    public Guid id { get; set; } = Guid.NewGuid();

    public string? FirstName { get; set; }

    public string? LastName { get; set; }

    public DateTimeOffset? DateOfBirth { get; set; }
    
    public Person() {}

    public Person(
        string firstName,
        string lastName,
        DateTimeOffset dateOfBirth
    ) 
    {
        FirstName = firstName;
        LastName = lastName;
        DateOfBirth = dateOfBirth;
    }

    public Person(
        Guid id,
        string firstName,
        string lastName,
        DateTimeOffset dateOfBirth
    )
    {
        this.id = id;
        FirstName = firstName;
        LastName = lastName;
        DateOfBirth = dateOfBirth;
    }
}