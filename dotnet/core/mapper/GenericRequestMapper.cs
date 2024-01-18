namespace core.mapper;

public interface GenericRequestMapper<T> 
{ 
    Task<GenericRequest> Map(T httpRequest);
}