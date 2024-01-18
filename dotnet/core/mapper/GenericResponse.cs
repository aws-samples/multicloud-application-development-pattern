namespace core.mapper;

public class GenericResponse
{
    public string HttpMethod { get; set; } = "GET";

    public int Status { get; set; } = 200;

    public IDictionary<string, string> Headers { get; set; } = new Dictionary<String, String>();

    public string Body { get; set; } = "";

    public GenericResponse() {}

    public GenericResponse(
        string httpMethod,
        int status,
        IDictionary<string, string> headers,
        string body
    )
    {
        this.HttpMethod = httpMethod;
        this.Status = status;
        this.Headers = headers;
        this.Body = body;
    }

    public void AddHeader(string headerKey, string headerValue)
    {
        Headers.Add(headerKey, headerValue);
    }
}