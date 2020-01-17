#!/bin/bash
rm ./**/*.class

className="src/Main"

javac $className.java
echo "==== COMPILED ===="
# java -Xss2g $className
java $className
