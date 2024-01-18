import { Optional } from "typescript-optional";
import {v4 as uuidv4} from 'uuid';
import { Person } from "../model/person";

export abstract class PersonDao {

    // todo:  how to use uuid instead of string?
    abstract getById(id: string): Promise<Optional<Person>>;

    abstract save(person: Person): void;

}