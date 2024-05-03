using System.Web;
using core.mapper;
using Microsoft.AspNetCore.Http;
using System.Threading.Tasks;

namespace gcp_cloud_function.mapper;

public class HttpRequestMapper : GenericRequestMapper<HttpRequest>
{
    public async Task<GenericRequest> Map(HttpRequest httpRequest)
    {
        Console.WriteLine("Inside HttpRequestMapper#Map");
        
        // convert httpRequest QueryString to Dictionary<string, List<string>>
        var nameValueCollection = HttpUtility.ParseQueryString(httpRequest.QueryString.ToString());
        IDictionary<string, IList<string>> queryParameters = nameValueCollection.AllKeys.ToDictionary(
                key => key, 
                key => (IList<string>) nameValueCollection[key].Split(','));

        Console.WriteLine("Finished transforming query parameters");
        
        return await Task.FromResult(
            new GenericRequest(
                httpRequest.Method,
                httpRequest.Path,
                await new StreamReader(httpRequest.Body).ReadToEndAsync(),
                queryParameters
            )
        );
    }
}