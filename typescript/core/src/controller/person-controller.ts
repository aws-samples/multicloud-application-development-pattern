import {PersonDao} from "../dao/person-dao";
import {GenericRequest} from "../mapper/generic-request";
import {Optional} from "typescript-optional";
import {Person} from "../model/person";
import {GenericResponse} from "../mapper/generic-response";

export class PersonController {

    private static readonly GET: string = 'GET';

    private static readonly POST: string = 'POST';

    // todo:  do we need something like Gson in Java to handle Date serialization and deserialization?

    private readonly personDao: PersonDao;

    constructor(personDao: PersonDao) {
        this.personDao = personDao;
    }

    public async handleRequest(request: GenericRequest, response: GenericResponse) {
        if (PersonController.GET === request.httpMethod && '/person' === request.path) {
            console.log(`query parameters: ${JSON.stringify(request.queryParameters)}`);

            const id: string = Optional.ofNullable(request.queryParameters['id'])
                .orElseThrow(() => new Error('id parameter is required'));

            const personOptional: Optional<Person> = await this.personDao.getById(id);
            personOptional.ifPresentOrElse(
                person => {
                    console.log(person);

                    response.statusCode = 200;

                    const personJson: string = JSON.stringify(person);
                    console.log(personJson);

                    response.body = personJson;
                },
                () => {
                    response.statusCode = 404;
                }
            )
        }
        else if (PersonController.POST === request.httpMethod && '/person' === request.path) {
            const person: Person = request.body as Person;

            await this.personDao.save(person);

            response.statusCode = 201;
            response.body = JSON.stringify({
                'id': person.id
            })
        }
        else {
            console.log(`No endpoint matches ${request.httpMethod} ${request.path}`)
        }

        PersonController.beforeResponse(request, response);
    }

    /**
     * This method contains logic that should run AFTER the request has been handled, but BEFORE the response is sent back
     * to the client.
     *
     * @param request {@link GenericRequest}
     * @param response {@link GenericRequest}
     * @private
     */
    private static beforeResponse(request: GenericRequest, response: GenericResponse): void {
        response.addHeader('Content-Type', 'application/json');
    }

}