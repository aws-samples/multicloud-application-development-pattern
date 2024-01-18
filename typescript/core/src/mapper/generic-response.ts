export class GenericResponse {

    private _httpMethod: string = '';

    private _statusCode: number = 200;

    private _headers: Map<string, string> = new Map<string, string>();

    private _body: string = '';

    constructor();

    constructor(
        httpMethod?: string,
        statusCode?: number,
        headers?: Map<string, string>,
        body?: string
    ) {
        this.httpMethod = httpMethod;
        this.statusCode = statusCode!;

        if (headers) {
            this.headers = headers;
        }

        if (body) {
            this.body = body;
        }
    }
    public get httpMethod() {
        return this._httpMethod;
    }

    public set httpMethod(httpMethod: string | undefined) {
        if (httpMethod) {
            this._httpMethod = httpMethod;
        }
    }

    public get statusCode() {
        return this._statusCode;
    }

    public set statusCode(statusCode: number) {
        if (statusCode) {
            this._statusCode = statusCode;
        }
    }

    public get headers() {
        return this._headers;
    }

    public set headers(headers: Map<string, string>) {
        this._headers = headers;
    }

    public get body() {
        return this._body;
    }

    public set body(body: string) {
        this._body = body;
    }

    public addHeader(headerKey: string, headerValue: string): void {
        this.headers.set(headerKey, headerValue);
    }

}