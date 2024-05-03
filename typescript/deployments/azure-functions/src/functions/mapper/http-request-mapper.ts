import {GenericRequestMapper} from "../../../../../core/src/mapper/generic-request-mapper";
import { HttpRequest } from "@azure/functions";
import {GenericRequest} from "../../../../../core/src/mapper/generic-request";

export class HttpRequestMapper implements GenericRequestMapper<HttpRequest> {

    public async map(httpRequest): Promise<GenericRequest> {
        // todo:  Consider checking the headers for "Content-Type: application/json" instead of using this try-catch block.
        let body: object = {}
        try {
            body = await httpRequest.json();
        }
        catch (e: any) {
            console.error('Could not deserialize HTTP request body');
        }

        const queryParameters: Record<string, string> = {};
        httpRequest.query.forEach((value, key) => queryParameters[key] = value);

        // pathname will return '/api/person', so replace '/api' with ''.
        const path: string = new URL(httpRequest.url).pathname.replace('/api', '');

        return new GenericRequest(
            httpRequest.method,
            path,
            body,
            queryParameters
        );
    }

}