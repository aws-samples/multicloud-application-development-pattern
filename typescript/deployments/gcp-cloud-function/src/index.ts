import {HttpFunction, Response} from "@google-cloud/functions-framework";
import {PersonDao} from "../../../core/src/dao/person-dao";
import {PersonController} from "../../../core/src/controller/person-controller";
import {FirestorePersonDaoImpl} from "./dao/firestore-person-dao-impl";
import {HttpRequestMapper} from "./mapper/http-request-mapper";
import {GenericRequest} from "../../../core/src/mapper/generic-request";
import {GenericResponse} from "../../../core/src/mapper/generic-response";

const personDao: PersonDao = new FirestorePersonDaoImpl();

const personController: PersonController = new PersonController(personDao);

const httpRequestMapper: HttpRequestMapper = new HttpRequestMapper();

export const main: HttpFunction = async (req, res) => {
    console.log('Inside main');

    const response: GenericResponse = new GenericResponse();
    try {
        const request: GenericRequest = await httpRequestMapper.map(req);

        await personController.handleRequest(request, response);

        // Map the GenericResponse to the res object and send it back to the client.
        mapResponse(response, res).send(response.body);
    }
    catch (e: any) {
        console.error(e);

        response.statusCode = 500;
        mapResponse(response, res).send(response.body);

    }
}

function mapResponse(genericResponse: GenericResponse, response: Response): Response {
    response.statusCode = genericResponse.statusCode;
    genericResponse.headers.forEach((value, key) => response.setHeader(key, value));

    return response;
}