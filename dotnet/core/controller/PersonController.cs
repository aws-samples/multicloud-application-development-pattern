using Newtonsoft.Json;
using core.dao;
using core.model;
using core.mapper;

namespace core.controller;

public class PersonController
{
    private static readonly string GET = "GET";

    private static readonly string POST = "POST";

    private readonly PersonDao _personDao;

    public PersonController(PersonDao personDao)
    {
        _personDao = personDao;
    }

    public async Task<GenericResponse> HandleRequest(GenericRequest request)
    {
        var response = new GenericResponse();
        
        if (GET.Equals(request.HttpMethod) && "/person".Equals(request.Path))  
		{
			var idQueryParameters = request.QueryParameters["id"];
            if (idQueryParameters.Count == 0) 
            {
                throw new NullReferenceException("id is a required parameter");
            }

            var id = idQueryParameters[0];
            var uuid = Guid.Parse(id);

            var person = await _personDao.GetById(uuid);
            Console.WriteLine("retrieved person is: {0}", person);
            if (person != null) 
            {
                response.Status = 200;

                var personJson = JsonConvert.SerializeObject(person);
                Console.WriteLine(personJson);
                
                response.Body = personJson;
            }
            else 
            {
                response.Status = 404;
                return BeforeResponse(request, response);
            }
		}
        else if (POST.Equals(request.HttpMethod) && "/person".Equals(request.Path))
        {
            if (request.Body == null) {
                response.Status = 400;
                return BeforeResponse(request, response);
            }
            
            Console.WriteLine("request body before deserialization is: {0}", request.Body);
            try
            {
                var person = JsonConvert.DeserializeObject<Person>(request.Body);
                Console.WriteLine("person uuid after deserialization is: {0}", person.id);
                
                var result = await _personDao.Save(person);
                Console.WriteLine("result is: {0}", result);
                Console.WriteLine("person uuid after saving is: {0}", person.id);

                response.Status = 201;
                response.Body = JsonConvert.SerializeObject(
                    new Dictionary<string, string>{
                        { "id", person.id.ToString() }
                    }
                );
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
                throw e;
            }
        }
        else 
        {
            Console.WriteLine("No endpoint matches {0} and {1}", request.HttpMethod, request.Path);
            response.Status = 404;
        }

        return BeforeResponse(request, response);
    }

    private static GenericResponse BeforeResponse(GenericRequest request, GenericResponse response)
    {
        response.AddHeader("Content-Type", "application/json");
        return response;
    }
}