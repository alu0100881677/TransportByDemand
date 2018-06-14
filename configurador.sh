#!/bin/sh
cd prbdGenerator
mvn install
cd ..
chmod 777 visorPareto.sh
chmod 777 generadorPeticiones.sh
chmod 777 solucionadorProblema.sh
