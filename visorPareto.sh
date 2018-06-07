#!/bin/sh
cd prbdGenerator
java -jar Translator.jar $1 $2  &
firefox VisorPareto.html
