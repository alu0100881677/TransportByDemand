#!/bin/sh
cd Translator
firefox VisorPareto.html &
java -jar Translator.jar $1 $2  

