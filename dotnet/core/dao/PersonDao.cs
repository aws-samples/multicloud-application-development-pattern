using core.model;

namespace core.dao;

public interface PersonDao
{
    Task<Person?> GetById(Guid id);

    Task<bool> Save(Person person);
}