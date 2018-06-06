#!/bin/sh
cd Translator
java -jar Translator.jar $1 $2  &
firefox VisorPareto.html
