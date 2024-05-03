#!/bin/bash

# Build core wheel
#python3 setup.py bdist_wheel --universal

# Build GCP Cloud Function.
rm -rf ./gcp-build
mkdir gcp-build || return

# Use docker or finch to install linux-based Python dependencies.
finch image build --no-cache -t gcp-build-tmp:local -f ./gcp/Dockerfile .
finch create --name gcp-build-tmp-container gcp-build-tmp:local
finch cp gcp-build-tmp-container:/gcp-build .
finch rm -f gcp-build-tmp-container

cd gcp-build/ || return
