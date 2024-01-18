import json
import os
import traceback

import flask
import functions_framework
from mcacore import person_controller
from mcacore.mapper import RequestMapper
from mcacore.model import GenericResponse, GenericRequest
from mcacore.person_dao import PersonDao

from firestore_dao import FirestoreDao
from flask_request_mapper import FlaskRequestMapper

request_mapper: RequestMapper[flask.Request] = FlaskRequestMapper()
project_id: str = os.environ['PROJECT_ID']
print(f'project_id is {project_id}')
person_dao: PersonDao = FirestoreDao(project_id=project_id)


def map_response(generic_response: GenericResponse) -> tuple:
    """Maps a GenericResponse to tuple that flask will use to create a flask.Response using make_response"""
    body_str: str = ''

    if generic_response.body:
        body_str = json.dumps(generic_response.body)

    return body_str, generic_response.status, generic_response.headers


@functions_framework.http
def hello_http(request):
    """HTTP Cloud Function.
    Args:
        request (flask.Request): The request object.
        <https://flask.palletsprojects.com/en/1.1.x/api/#incoming-request-data>
    Returns:
        The response text, or any set of values that can be turned into a
        Response object using `make_response`
        <https://flask.palletsprojects.com/en/1.1.x/api/#flask.make_response>.
    """
    print('Inside hello_http')

    print(f'request before mapping: {request}')

    generic_request: GenericRequest = request_mapper.map(request)
    print(f'generic_request: {generic_request}')

    try:
        print('Calling person controller')
        generic_response: GenericResponse = person_controller.handle_request(generic_request, person_dao)

        print(f'Got generic response: {generic_response}')
        return map_response(generic_response)
    except Exception as e:
        print('There was an error')
        traceback.print_exception(e)
        return {
            'body': f'Resource not found: {request.url}',
            'status': 404
        }
