import json
import logging
import os

from aws_api_gateway_proxy_request_mapper import AwsApiGatewayProxyRequestMapper
from mcacore import person_controller

from aws_person_dao import AwsPersonDAO
from mcacore.mapper import RequestMapper
from mcacore.model import GenericRequest, GenericResponse
from mcacore.person_dao import PersonDao

# One-time initialization code goes here
logging.basicConfig(level=logging.DEBUG)
person_dao: PersonDao = AwsPersonDAO()
request_mapper: RequestMapper[dict] = AwsApiGatewayProxyRequestMapper()
logger = logging.getLogger('lambda_function')
logger.setLevel(os.environ.get('LOG_LEVEL', 'WARNING'))


def map_response(generic_response: GenericResponse) -> dict:
    """Maps a GenericResponse to the dictionary that the Lambda service expects"""
    if generic_response.body:
        body_str: str = json.dumps(generic_response.body)
    else:
        body_str: str = ''

    return {
        'httpMethod': generic_response.http_method,
        'statusCode': generic_response.status,
        'body': body_str,
        'headers': generic_response.headers
    }


def lambda_handler(event, context):
    """The Lambda entry point"""
    logger.info('Inside the lambda handler function')

    # Map a Lambda-specific event to a general-purpose, cross-cloud request
    request: GenericRequest = request_mapper.map(event)

    # Dispatch the event to the appropriate controller, and return the results to the requestor
    try:
        generic_response: GenericResponse = person_controller.handle_request(request, person_dao)
        return map_response(generic_response)
    except Exception as e:
        logger.error(e)
        return {
            'status': 404,
            'message': f"Resource not found: {request.path}"
        }


# The following is only needed for a stand-alone function
if __name__ == "__main__":
    logger.info("Hello World")
    # Note: The 'id' value in evt1 is the identifier for a person that already exists in the table.
    # For testing, replace the id value with an existing value from the DynamoDB table
    evt1 = {
        'httpMethod': 'GET',
        'path': '/person',
        'body': None,
        'queryParameters': {
            'id': '8a88da63-8f4a-491a-a1fe-5ae6e68c31ee'
        },
        'headers': {
        }
    }

    evt2 = {
        'httpMethod': 'POST',
        'path': '/person',
        'body': {
            'firstName': 'Jeff',
            'lastName': 'Jones',
            'dateOfBirth': '02/03/2004'
        },
        'queryParameters': {
        },
        'headers': {
        }
    }

    response = lambda_handler(evt1, None)
    logger.info(f"The response: {response}")
    logger.info("Done")
