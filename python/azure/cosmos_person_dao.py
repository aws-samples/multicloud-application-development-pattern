import logging
import os
import uuid

from azure.cosmos import CosmosClient, PartitionKey, DatabaseProxy, ContainerProxy

from mcacore.model import Person
from mcacore.person_dao import PersonDao


class CosmosPersonDao(PersonDao):

    database_name: str = 'MyCosmosDatabase'

    container_name: str = 'person'

    primary_key: str = '/id'

    def __init__(self):
        endpoint: str = os.environ["COSMOS_ENDPOINT"]
        key: str = os.environ["COSMOS_KEY"]
        self.client = CosmosClient(url=endpoint, credential=key)

        self.cosmos_database: DatabaseProxy = self.client.create_database_if_not_exists(id=self.database_name)
        logging.info(f"Database with id '{self.database_name}' created")

        partition_key = PartitionKey(path=self.primary_key, kind='Hash')
        logging.info("\n2.1 Create Container - Basic")

        self.cosmos_container: ContainerProxy = self.cosmos_database.create_container_if_not_exists(id=self.container_name, partition_key=partition_key)
        logging.info('Container with id \'{0}\' created'.format(self.container_name))

        logging.info("\n2.2 Create Container - With custom index policy")

    def get_person(self, person_id: str) -> Person:
        logging.info('Inside CosmosPersonDao#get_person')

        response: dict = self.cosmos_container.read_item(item=person_id, partition_key=person_id)

        logging.info(response)

        person: Person = Person()
        person.id = response['id']
        person.firstName = response['firstName']
        person.lastName = response['lastName']
        person.dateOfBirth = response['dateOfBirth']

        return person

    def create_person(self, person: Person) -> Person:
        logging.info('Inside CosmosPersonDao#create_person')

        if not person.id:
            person.id = uuid.uuid4().__str__()

        self.cosmos_container.upsert_item(body=person.to_json())

        return person
