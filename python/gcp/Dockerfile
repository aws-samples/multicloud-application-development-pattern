FROM python:3.11.6

RUN mkdir ./gcp-build
WORKDIR ./gcp-build
COPY ./gcp/*.py .
COPY ./gcp/requirements.txt .
COPY ./dist/*.whl .

RUN pip3 install -t . -r ./requirements.txt