#!/bin/bash
rm -rf build
javac -d ./build src/**/*.java
jar cvf Proxy.jar build/*
