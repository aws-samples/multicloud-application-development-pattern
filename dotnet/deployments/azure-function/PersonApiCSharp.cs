using System;
using System.Threading.Tasks;
using azure_function.dao;
using azure_function.mapper;
using core.controller;
using core.dao;
using core.mapper;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;

namespace azure_function
{
    public static class PersonApiCSharp
    {
        private static readonly GenericRequestMapper<HttpRequest> RequestMapper = new HttpRequestMapper(); 

        private static readonly PersonDao PersonDao;

        private static readonly PersonController PersonController;

        static PersonApiCSharp()
        {
            PersonDao = new CosmosDbPersonDao();
            PersonController = new PersonController(PersonDao);
        }
        
        [FunctionName("PersonApiCSharp")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.User, "get", "post", Route = "person")] HttpRequest req,
            ILogger log
        )
        {
            log.LogInformation("C# HTTP trigger function processed a request.");

            var response = new GenericResponse();
            try
            {
                Console.WriteLine("Mapping HttpRequest");
                var request = await RequestMapper.Map(req);

                Console.WriteLine("Calling person controller");
                Console.WriteLine(JsonConvert.SerializeObject(request));
                response = await PersonController.HandleRequest(request);

                return MapGenericResponseToObjectResult(response);
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
                
                response.Status = 500;
                return MapGenericResponseToObjectResult(response);
            }
        }
        
        private static ObjectResult MapGenericResponseToObjectResult(GenericResponse response)
        {
            var objectResult = new ObjectResult(response)
            {
                StatusCode = response.Status,
                Value = response.Body
            };

            return objectResult;
        }
    }
}
