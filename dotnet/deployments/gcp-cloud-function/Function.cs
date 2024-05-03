using core.controller;
using core.dao;
using core.mapper;
using gcp_cloud_function.mapper;
using Google.Cloud.Functions.Framework;
using Microsoft.AspNetCore.Http;

namespace gcp_cloud_function;

public class Function : IHttpFunction
{
    private static readonly GenericRequestMapper<HttpRequest> _requestMapper = new HttpRequestMapper(); 

    private static readonly PersonDao _personDao;

    private static readonly PersonController _personController;

    static Function()
    {
        _personDao = new FirestorePersonDaoImpl();
        _personController = new PersonController(_personDao);
    }
    public async Task HandleAsync(HttpContext context)
    {
        Console.WriteLine("Inside HandleAsync");

        try
        {
            var request = await _requestMapper.Map(context.Request);
            Console.WriteLine("{0}", request);
            
            Console.WriteLine("Calling personController#HandleRequest");
            var response = await _personController.HandleRequest(request);
            Console.WriteLine("{0}", response);
            
            // Map GenericResponse to context#Response.
            Console.WriteLine("Mapping GenericResponse to context object");
            context.Response.StatusCode = response.Status;
            Console.WriteLine("Successfully set status code");
            foreach (var keyValuePair in response.Headers)
            {
                context.Response.Headers.Append(keyValuePair.Key, keyValuePair.Value);
            }
            Console.WriteLine("Successfully set headers");
            await context.Response.WriteAsync(response.Body);
            Console.WriteLine("Successfully set body");
        }
        catch (Exception e)
        {
            Console.WriteLine(e);
            context.Response.StatusCode = 500;
            
            await context.Response.WriteAsync("");
        }
    }

}