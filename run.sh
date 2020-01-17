#!/bin/bash
rm ./**/*.class
rm -rf ./cache/*

className="src/Main"

javac $className.java
echo "==== COMPILED ===="
# java -Xss2g $className
java $className
