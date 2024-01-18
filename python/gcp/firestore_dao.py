from typing import Optional

from google.cloud import firestore
from mcacore.model import Person
from mcacore.person_dao import PersonDao


class FirestoreDao(PersonDao):

    PERSON_TABLE_NAME: str = 'person'

    ID_FIELD_NAME: str = 'id'

    def __init__(self, project_id: str):
        self.db_client = firestore.Client(project=project_id)

    def get_person(self, person_id: str) -> Optional[Person]:
        print('Inside FirestoreDao#get_person')

        document = self.db_client.collection(self.PERSON_TABLE_NAME).document(person_id).get()
        print(f'document is {document}')
        if document.exists:
            print(f'Document exists for person_id {person_id}.  Mapping to Person and returning.')
            document_dict = document.to_dict()
            return Person.init_from(document_dict)
        else:
            print(f'Document does not exist for person_id {person_id}')
            return None

    def create_person(self, person: Person) -> Optional[Person]:
        print('Inside FirestoreDao#create_person')

        print(f'Saving {person.to_json()} to firestore')
        self.db_client.collection(self.PERSON_TABLE_NAME).document(person.id).set(person.to_json())
        print('Saving successful')

        return person
