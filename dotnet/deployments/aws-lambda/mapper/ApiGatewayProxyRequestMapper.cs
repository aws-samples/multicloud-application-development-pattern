using Amazon.Lambda.APIGatewayEvents;
using core.mapper;

namespace aws_lambda.mapper;

public class ApiGatewayProxyRequestMapper : GenericRequestMapper<APIGatewayProxyRequest>
{
    public async Task<GenericRequest> Map(APIGatewayProxyRequest httpRequest)
    {
        var splitQueryParameters =
            httpRequest.QueryStringParameters?.ToDictionary(
                pair => pair.Key, 
                pair => (IList<string>) pair.Value.Split().ToList());
        
        return new GenericRequest(
                httpRequest.HttpMethod, 
                httpRequest.Path, 
                httpRequest.Body,
                splitQueryParameters
        );
    }
}