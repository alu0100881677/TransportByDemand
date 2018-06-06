#!/bin/sh
cd prbdGenerator
java -jar Launcher.jar $1 $2 $3 $4 &
java -Dspring.config.location=src/main/resources/application.properties -jar target/prbdGenerator-0.0.1-SNAPSHOT.jar 
