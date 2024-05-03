import json
import logging

from azure.functions import HttpRequest

from mcacore.mapper import RequestMapper
from mcacore.model import GenericRequest


class HttpRequestMapper(RequestMapper):

    def map(self, request: HttpRequest) -> GenericRequest:
        # Get path
        path: str = request.url.split("/api")[1] # Removing the domain and base "/api" path.
        path_without_query_params: str = path.split('?')[0] # Removing the query params, which can be present in the url.
        logging.info(f'path is {path_without_query_params}')

        # Get body
        body: dict = {}
        body_bytes: bytes = request.get_body()
        if body_bytes:
            body_string: str = body_bytes.decode('utf-8')
            body = json.loads(body_string)
            logging.info(f'body is {json.dumps(body)}')

        return GenericRequest(
            http_method=request.method,
            path=path_without_query_params,
            body=body,
            query_parameters=request.params
        )
