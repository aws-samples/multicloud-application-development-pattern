import { GenericRequest } from "./generic-request";

export abstract class GenericRequestMapper<T> {

    abstract map(httpRequest: T): Promise<GenericRequest>;

}