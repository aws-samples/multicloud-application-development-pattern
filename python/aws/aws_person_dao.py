import boto3
import logging
import os
from typing import Union, Any
from uuid import uuid4

from mcacore.model import Person
from mcacore.person_dao import PersonDao


class AwsPersonDAO(PersonDao):

    TABLE_NAME = 'person'

    def __init__(
            self,
            dynamodb_resource: Any = None
    ):
        if dynamodb_resource:
            self.dynamodb_table = dynamodb_resource.Table(self.TABLE_NAME)
        else:
            self.dynamodb_table = boto3.resource('dynamodb').Table(self.TABLE_NAME)

        self.logger = logging.getLogger('AwsPersonDAO')
        self.logger.setLevel(os.environ.get('LOG_LEVEL', 'WARNING'))

    def get_person(self, person_id: str) -> Union[Person, None]:
        try:
            response = self.dynamodb_table.get_item(
                Key={
                    'id': person_id
                }
            )

            if response and response['Item']:
                return Person.init_from(response['Item'])
        except Exception as e:
            self.logger.error(f'Exception getting a person: {e}')
            return None

    def create_person(self, person: Person) -> Union[Person, None]:
        try:
            person.id = str(uuid4())

            response = self.dynamodb_table.put_item(
                Item=person.to_json()
            )
            if response:
                return person
        except Exception as e:
            self.logger.error(f'Exception getting a person: {e}')
            return None
