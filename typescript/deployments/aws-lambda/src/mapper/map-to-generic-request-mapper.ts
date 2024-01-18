import {GenericRequestMapper} from "../../../../core/src/mapper/generic-request-mapper";
import {GenericRequest} from "../../../../core/src/mapper/generic-request";
import {Optional} from "typescript-optional";
import {APIGatewayProxyEvent} from "aws-lambda";

export class MapToGenericRequestMapper implements GenericRequestMapper<APIGatewayProxyEvent>{

    public map(event: APIGatewayProxyEvent): Promise<GenericRequest> {
        const queryParameters: Record<string, any> = Optional.ofNullable(event.queryStringParameters)
            .orElse({});

        const httpMethod: string = this.toStringOrEmptyString(event.httpMethod);
        const path: string = this.toStringOrEmptyString(event.path);
        let body: Object = '';
        if (event.body) {
            body = JSON.parse(event.body) as Object;
        }

        return Promise.resolve(new GenericRequest(httpMethod, path, body, queryParameters))
    }

    private toStringOrEmptyString(object: Object | undefined): string {
        return Optional.ofNullable(object)
            .map(obj => obj.toString())
            .orElse('');
    }

}