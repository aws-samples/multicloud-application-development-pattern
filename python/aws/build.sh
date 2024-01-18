#!/bin/bash

# Build core wheel
python3 setup.py bdist_wheel --universal

# Build AWS zip file
rm ./aws-lambda.zip
rm -rf ./aws-build
mkdir aws-build || return
pip3 install -t ./aws-build -r ./aws/requirements.txt
cp ./aws/*.py ./aws-build/
cd aws-build/ || return
zip -r ../aws-lambda.zip .
