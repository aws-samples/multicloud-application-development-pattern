using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using core.mapper;
using Microsoft.AspNetCore.Http;

namespace azure_function.mapper;

public class HttpRequestMapper : GenericRequestMapper<HttpRequest>
{
    public async Task<GenericRequest> Map(HttpRequest httpRequest)
    {
        var splitQueryParameters = httpRequest.Query.ToDictionary(
            pair => pair.Key,
            pair => (IList<string>)pair.Value.ToList()
        );
        
        var requestBody = await new StreamReader(httpRequest.Body).ReadToEndAsync();

        return new GenericRequest(
            httpRequest.Method,
            httpRequest.Path.ToString().Remove(0, 4), // Removing "/api" from path, such as "/api/person".
            requestBody,
            splitQueryParameters
        );
    }
}