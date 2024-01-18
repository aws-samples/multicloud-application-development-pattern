import json

from mcacore.model import GenericRequest

from mcacore.mapper import RequestMapper


class AwsApiGatewayProxyRequestMapper(RequestMapper):

    def map(self, aws_api_gateway_proxy_request: dict) -> GenericRequest:
        return GenericRequest(
            http_method=aws_api_gateway_proxy_request['httpMethod'],
            path=aws_api_gateway_proxy_request['path'],
            body=json.loads(aws_api_gateway_proxy_request['body']) if aws_api_gateway_proxy_request['body'] else {},
            query_parameters=aws_api_gateway_proxy_request['queryStringParameters']
        )
