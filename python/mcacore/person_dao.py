from .model import Person


class PersonDao:
    """Acts as an interface to be implemented by classes that will persist and retrieve Person objects from external
    data stores and systems."""

    def get_person(self, person_id: str) -> Person:
        raise NotImplementedError()

    def create_person(self, person: Person) -> Person:
        raise NotImplementedError()

