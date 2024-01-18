import {v4 as uuidv4} from 'uuid';

export class Person {

    // private _id: typeof uuidv4;

    private _id: string;

    private _firstName: string;

    private _lastName: string;

    private _dateOfBirth: Date

    constructor(
        // id: typeof uuidv4,
        id: string,
        firstName: string,
        lastName: string,
        dateOfBirth: Date
    ) {
        this._id = id;
        this._firstName = firstName;
        this._lastName = lastName;
        this._dateOfBirth = dateOfBirth;
    }

    public get id() {
        return this._id;
    }

    public set id(id: string) {
        this._id = id;
    }

    public get firstName() {
        return this._firstName;
    }

    public set firstName(firstName: string) {
        this._firstName = firstName;
    }

    public get lastName() {
        return this._lastName;
    }

    public set lastName(lastName: string) {
        this._lastName = lastName;
    }

    public get dateOfBirth() {
        return this._dateOfBirth;
    }

    public set dateOfBirth(dateOfBirth: Date) {
        this._dateOfBirth = dateOfBirth;
    }

}