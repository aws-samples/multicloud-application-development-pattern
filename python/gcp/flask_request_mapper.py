import flask
from mcacore.mapper import RequestMapper
from mcacore.model import GenericRequest


class FlaskRequestMapper(RequestMapper):

    def map(self, request: flask.Request) -> GenericRequest:
        body: dict = {}

        if request and request.is_json:
            body = request.get_json()

        return GenericRequest(
            http_method=request.method,
            path=request.path,
            body=body,
            query_parameters=request.args
        )
