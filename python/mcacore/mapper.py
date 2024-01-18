from typing import TypeVar, Generic

from mcacore.model import GenericRequest

T = TypeVar('T')


class RequestMapper(Generic[T]):
    """Acts as an interface to be implemented by classes that should map an object to a GenericRequest"""

    def map(self, request: Generic[T]) -> GenericRequest:
        raise NotImplementedError()
