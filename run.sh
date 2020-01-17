#!/bin/bash
rm -rf ./**/*.class &&
  rm -rf ./cache/* &&
  javac src/Main.java &&
  echo "==== COMPILED ====" &&
  java src/Main
