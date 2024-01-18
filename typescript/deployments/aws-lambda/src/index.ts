import { Context, APIGatewayEvent } from 'aws-lambda';
import {PersonDao} from "../../../core/src/dao/person-dao";
import {PersonController} from "../../../core/src/controller/person-controller";
import {DynamodbPersonDaoImpl} from "./dao/dynamodb-person-dao-impl";
import {MapToGenericRequestMapper} from "./mapper/map-to-generic-request-mapper";
import {GenericRequest} from "../../../core/src/mapper/generic-request";
import {GenericResponse} from "../../../core/src/mapper/generic-response";

const personDao: PersonDao = new DynamodbPersonDaoImpl();

const personController: PersonController = new PersonController(personDao);

const mapToGenericRequestMapper: MapToGenericRequestMapper = new MapToGenericRequestMapper();

export const lambdaHandler = async (event: APIGatewayEvent, context: Context): Promise<GenericResponse> => {
    console.log(`Event: ${JSON.stringify(event, null, 2)}`);
    console.log(`Context: ${JSON.stringify(context, null, 2)}`);

    const response: GenericResponse = new GenericResponse();
    try {
        const request: GenericRequest = await mapToGenericRequestMapper.map(event)

        await personController.handleRequest(request, response);

        return response;
    }
    catch (e: any) {
        console.error(e);

        response.statusCode = 500;
        return response;
    }
};