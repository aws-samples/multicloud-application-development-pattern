using Amazon.Lambda.APIGatewayEvents;
using Amazon.Lambda.Core;
using aws_lambda.dao;
using aws_lambda.mapper;
using core.controller;
using core.mapper;

namespace aws_lambda;

using core.dao;
public class LambdaHandler
{
    private static readonly GenericRequestMapper<APIGatewayProxyRequest> _requestMapper = new ApiGatewayProxyRequestMapper(); 

    private static readonly PersonDao _personDao;

    private static readonly PersonController _personController;

    static LambdaHandler()
    {
        _personDao = new DynamoDbPersonDao();
        _personController = new PersonController(_personDao);
    }

    public static void Main(string[] args)
    {
    }

    // todo:  Add logging statements.
    [LambdaSerializer(typeof(Amazon.Lambda.Serialization.SystemTextJson.DefaultLambdaJsonSerializer))]
    public static async Task<APIGatewayProxyResponse> Handler(APIGatewayProxyRequest apiGatewayProxyRequest, ILambdaContext context)
    {
        Console.WriteLine("In Handler");

        var response = new GenericResponse();
        try
        {
            Console.WriteLine("Mapping api gateway proxy request");
            var request = await _requestMapper.Map(apiGatewayProxyRequest);
            
            Console.WriteLine("Calling person controller");
            response = await _personController.HandleRequest(request);

            Console.WriteLine("Mapping generic response to api gateway proxy response");
            return MapGenericResponseToApiGatewayProxyResponse(response);
        }
        catch (Exception e)
        {
            Console.WriteLine(e);
            
            response.Status = 500;
            return MapGenericResponseToApiGatewayProxyResponse(response);
        }
    }

    private static APIGatewayProxyResponse MapGenericResponseToApiGatewayProxyResponse(GenericResponse genericResponse)
    {
        Console.WriteLine("genericResponse Body is: {0}", genericResponse.Body);

        var proxyResponse = new APIGatewayProxyResponse();
        proxyResponse.Body = genericResponse.Body;
        proxyResponse.Headers = genericResponse.Headers;
        proxyResponse.StatusCode = genericResponse.Status;

        Console.WriteLine("proxyResponse Body is: {0}", proxyResponse.Body);
        
        return proxyResponse;
    }

}