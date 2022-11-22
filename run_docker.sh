#!/bin/sh
./gradlew clean build && docker build -t devsong/spring-test-demo:1.0.0-SNAPSHOT .
