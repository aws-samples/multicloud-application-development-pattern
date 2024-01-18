namespace core.mapper;

public record GenericRequest
{
    public string HttpMethod { get; set; }

    public string Path { get; set; }

    public string? Body { get; set; }

    public IDictionary<string, IList<string>> QueryParameters { get; set; } = new Dictionary<string, IList<string>>();

    public GenericRequest(
        string httpMethod,
        string path,
        string? body,
        IDictionary<string, IList<string>>? queryParameters)
    {
        HttpMethod = httpMethod ?? throw new ArgumentNullException(nameof(httpMethod));
        Path = path ?? throw new ArgumentException(nameof(path));
        Body = body;

        if (queryParameters != null) {
            QueryParameters = queryParameters;
        }
    }
}