import { app, HttpRequest, HttpResponseInit, InvocationContext, input } from "@azure/functions";
// @ts-ignore
import { PersonDao } from "../../../../core/src/dao/person-dao";
import { CosmosDbPersonDaoImpl } from "./dao/cosmosdb-person-dao-impl";
import {PersonController} from "../../../../core/src/controller/person-controller";
import {GenericResponse} from "../../../../core/src/mapper/generic-response";
import * as http from "http";
import {GenericRequest} from "../../../../core/src/mapper/generic-request";
import {HttpRequestMapper} from "./mapper/http-request-mapper";

const personDao: PersonDao = new CosmosDbPersonDaoImpl();
const personController: PersonController = new PersonController(personDao);

const requestMapper: HttpRequestMapper = new HttpRequestMapper();

export async function personApiNode(request: HttpRequest, context: InvocationContext): Promise<HttpResponseInit> {
    context.log('Inside personApiNode function handler');

    const genericResponse: GenericResponse = new GenericResponse();
    try {
        context.log(`request: ${JSON.stringify(request, null, 2)}`);
        context.log(`context: ${JSON.stringify(request, null, 2)}`);

        request.query.forEach((key, value) => context.log(`key: ${key}, value: ${value}`));

        const genericRequest: GenericRequest = await requestMapper.map(request);

        context.log(`genericRequest: ${JSON.stringify(genericRequest)}`);

        await personController.handleRequest(genericRequest, genericResponse);

        return mapGenericResponseToHttpResponseInit(genericResponse);
    }
    catch (e: any) {
        context.error(e);

        genericResponse.statusCode = 500;
        return mapGenericResponseToHttpResponseInit(genericResponse);
    }
};

function mapGenericResponseToHttpResponseInit(genericResponse: GenericResponse): HttpResponseInit {
    console.log('Inside mapGenericResponseToHttpResponseInit');

    // Map headers
    const httpResponseHeaders: Record<string, string> = {};
    genericResponse.headers.forEach((value, key) => httpResponseHeaders[key] = value);
    console.log(`httpResponseHeaders: ${JSON.stringify(httpResponseHeaders)}`);

    return {
        status: genericResponse.statusCode,
        jsonBody: genericResponse.body,
        headers: httpResponseHeaders
    }
}

app.http('personApiNode', {
    methods: ['GET', 'POST'], // todo:  Is this needed because methods is listed in the trigger, too?
    authLevel: 'anonymous', // todo:  Is this needed because authLevel is listed in the trigger, too?
    handler: personApiNode,
    trigger: {
        type: 'httpTrigger',
        name: 'personApi',
        authLevel: 'anonymous',
        methods: ['GET', 'POST'],
        route: 'person'
    }
});
