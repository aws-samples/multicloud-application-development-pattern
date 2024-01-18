export class GenericRequest {

    private _httpMethod: string;

    private _path: string;

    private _body: any = {};

    private _queryParameters: Record<string, any> = {};

    constructor(
        httpMethod: string,
        path: string,
        body: any,
        queryParameters: Record<string, any>
    ) {
        if (! httpMethod) {
            throw Error('httpMethod cannot be null');
        }

        if (! path) {
            throw Error('path cannot be null');
        }

        this._httpMethod = httpMethod;
        this._path = path;

        this.body = body;
        this.queryParameters = queryParameters;
    }

    public get httpMethod() {
        return this._httpMethod;
    }

    public set httpMethod(httpMethod: string) {
        this._httpMethod = httpMethod;
    }

    public get path() {
        return this._path;
    }

    public set path(path: string) {
        this._path = path;
    }

    public get body() {
        return this._body;
    }

    public set body(body: any) {
        if (body) {
            this._body = body;
        }
    }

    public get queryParameters() {
        return this._queryParameters;
    }

    public set queryParameters(queryParameters: Record<string, any>) {
        if (queryParameters) {
            this._queryParameters = queryParameters;
        }
    }

}