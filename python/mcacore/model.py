import dataclasses
import json
import uuid
from dataclasses import dataclass


class JsonRepresentation:
    """Acts as an interface to be implemented by any class that should be able to be converted to a JSON dictionary."""
    def to_json(self) -> dict:
        """Returns a JSON dictionary of the object."""
        raise NotImplementedError()


@dataclass
class Person(JsonRepresentation):

    def __init__(self) -> None:
        self.id: str = uuid.uuid4().__str__()
        self.firstName: str = ''
        self.lastName: str = ''
        self.dateOfBirth: str = '01/01/0000'

    def to_json(self) -> dict:
        return {
            'id': self.id,
            'firstName': self.firstName,
            'lastName': self.lastName,
            'dateOfBirth': self.dateOfBirth
        }

    @staticmethod
    def init_from(json_object: dict) -> 'Person':
        person: Person = Person()
        if json_object:
            id = json_object.get('id')
            if id:
                person.id = id

            person.firstName = json_object.get('firstName')
            person.lastName = json_object.get('lastName')
            person.dateOfBirth = json_object.get('dateOfBirth')
        return person


@dataclass
class GenericRequest:

    http_method: str = 'GET'
    path: str = ''
    body: dict = dataclasses.field(default_factory=dict)
    query_parameters: dict = dataclasses.field(default_factory=dict)


@dataclass
class GenericResponse(JsonRepresentation):

    http_method: str = 'NONE'
    status: int = 500
    headers: dict = dataclasses.field(default_factory=dict)
    body: dict = dataclasses.field(default_factory=dict)

    def to_json(self) -> dict:
        return {
            'httpMethod': self.http_method,
            'status': self.status,
            'headers': self.headers,
            'body': json.dumps(self.body)
        }