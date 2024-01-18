import {GenericRequestMapper} from "../../../../core/src/mapper/generic-request-mapper";
import {GenericRequest} from "../../../../core/src/mapper/generic-request";
import {Request} from "@google-cloud/functions-framework";

export class HttpRequestMapper implements GenericRequestMapper<Request> {

    public async map(httpRequest: Request): Promise<GenericRequest> {
        return new GenericRequest(
            httpRequest.method.toUpperCase(),
            httpRequest.path,
            httpRequest.body,
            httpRequest.query
        );
    }

}