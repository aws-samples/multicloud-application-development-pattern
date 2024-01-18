from .model import GenericRequest, GenericResponse, Person
from .person_dao import PersonDao


def handle_request(request: GenericRequest, person_dao: PersonDao) -> GenericResponse:
    if request.http_method == 'GET' and request.path == '/person':
        return _get_person(request, person_dao)
    elif request.http_method == 'POST' and request.path == '/person':
        return _create_person(request, person_dao)
    else:
        return build_error_response(request.http_method, 404, 'Not implemented')


def build_error_response(method: str, status: int, msg: str = None) -> GenericResponse:
    return GenericResponse(
        http_method=method,
        status=status,
        body={
            'msg': msg
        }
    )


def _get_person(request: GenericRequest, person_dao: PersonDao) -> GenericResponse:
    try:
        id: str = request.query_parameters.get('id')
        # Get the record from the DAO
        person: Person = person_dao.get_person(id)
        print(f'person is: {person}')
        if not person:
            return build_error_response(request.http_method, 404)

        return GenericResponse(
            http_method=request.http_method,
            status=200,
            body=person.to_json()
        )
    except Exception as exc:
        return build_error_response(request.http_method, 404, f"Not found - {exc}")


def _create_person(request: GenericRequest, person_dao: PersonDao) -> GenericResponse:
    try:
        # Create the person
        person = Person.init_from(json_object=request.body)
        persisted_person = person_dao.create_person(person)

        return GenericResponse(
            http_method=request.http_method,
            status=201,
            body={
                'id': persisted_person.id
            }
        )
    except Exception as exc:
        return build_error_response(request.http_method, 500, f"Internal server error - {exc}")

