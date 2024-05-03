import traceback
import azure.functions as func
import json
import logging

from mcacore import person_controller
from mcacore.model import GenericResponse, GenericRequest
from mcacore.person_dao import PersonDao

from cosmos_person_dao import CosmosPersonDao
from http_request_mapper import HttpRequestMapper
from mcacore.mapper import RequestMapper

app = func.FunctionApp()
person_dao: PersonDao = CosmosPersonDao()
request_mapper: RequestMapper[func.HttpRequest] = HttpRequestMapper()

logging.info('Done with one-time instantiation')


def map_response(generic_response: GenericResponse) -> func.HttpResponse:
    """Maps a GenericResponse to an Azure Functions HttpResponse that the Azure Functions service expects"""
    if generic_response.body:
        body_str: str = json.dumps(generic_response.body)
    else:
        body_str: str = ''

    return func.HttpResponse(
        body=body_str,
        status_code=generic_response.status,
        headers=generic_response.headers
    )


@app.route(route="person", auth_level=func.AuthLevel.ANONYMOUS)
def PersonApiPython(req: func.HttpRequest) -> func.HttpResponse:
    logging.info('Inside the azure function handler')

    request: GenericRequest = request_mapper.map(req)
    logging.info(f'request is {request}')

    try:
        logging.info('Calling person controller')
        generic_response: GenericResponse = person_controller.handle_request(request, person_dao)

        logging.info(f'Got generic response: {generic_response}')

        return map_response(generic_response)
    except Exception as e:
        logging.error('There was an error')

        traceback.print_exception(e)

        return func.HttpResponse(
            status_code=404,
            body=f'Resource not found: {req.url}'
        )
