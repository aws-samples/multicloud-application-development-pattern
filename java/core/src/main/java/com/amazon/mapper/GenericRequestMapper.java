package com.amazon.mapper;

import java.io.IOException;

public interface GenericRequestMapper<T> {

    GenericRequest map(T httpRequest) throws IOException;

}
