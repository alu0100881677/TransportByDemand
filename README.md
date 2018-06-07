# Problema de Rutas Bajo Demanda

Este repositorio contiene el código necesario para resolver una variante de los problemas de rutas. El problema que se resuelve es un problema de rutas bajo demandas el cual trata de asignar las rutas de un conjunto de vehículos en función de las necesidades de un conjunto de usuarios.

Para conocer bien este problema es necesario conocer los datos que van a ser usados, así mismo como las características que presenta el problema y los distintos objetivos que se pretenden alcanzar a resolverlo.

## Instalación
 * En primer lugar debe de clonar este repositorio
 * Una vez clonado, situese en en la base del directorio y ejecute el script bash [`configurador.sh`]. Este script se encargará de instalar las dependencias necesarias del proyecto.

## Uso
El repositorio contiene tres modulos distintos los cuales pueden compenetrar su funcionamiento. Para arrancar cada uno de los modulos necesitará una terminal para cada uno de ellos.
 * El primer modulo, encargado de la generación de peticiones de movilidad es ejecutado desde el directorio raiz del repositorio mediante el comando *./generadorPeticiones.sh*. A este modulo debemos de pasarle una serie de argumentos que establezcan su funcionamiento:
   * nombre del fichero donde se generan las peticiones. Este fichero estará en la carpeta jmetalsp-examples/Datos.
   * número de paradas que contiene el problema para el que simulamos peticiones.
   * parametro númerico que representa el paso del tiempo entre petición y petición, se recomienda usar 150.
   * número de milisegundos entre la generación de cada petición (5 segundos = 5000)

 * El segundo modulo, es el encargado de resolver el problema, para ello hace uso del algoritmo nsga-ii. Para configurar este modulo debemos de pasar una serie de parametros a *./solucionadorProblema.sh*:
  * fichero de distancias
  * fichero de costes
  * fichero con la localización de los buses
  * nombre del fichero con las peticiones
  * nombre del directorio que almacena los frentes de pareto
  * periodo de lectura de nuevas peticiones en milisegundos(2000)

Todos los ficheros que se pasen como parámetro deben de estar en  jmetal-examples/Datos y solo debe indicar su nombre.
 * El tercer modulo representa las soluciones obtenidas por el segundo modulo mediante un servicio web. Para ponerlo en funcionamiento se debe de ejecutar el script *./visorPareto.sh* al que se le deben de pasar una serie de argumentos:
  * nombre del directorio donde que almacena los frentes de pareto
  * periodo en milisegundos que pasa entre la traducción de cada fichero, se recomienda usar 2000.

### Ejemplo de ejecución
Tal y como se ha mencionado previamente cada modulo debe de ser ejecutado en una terminal distinta. Además para que el funcionamiento del programa sea el adecuado, se deben de ejecutar los modulos en orden.
 * Primer modulo --> ./generadorPeticiones.sh generacion.txt 10 150 5000
 * Segundo modulo --> ./solucionadorProblema.sh distanceFile.prbd costFile.prbd busesLocations.prbd generacion.txt DirectorioSalida 2000
 * Tercer modulo --> ./visorPareto.sh DirectorioSalida 2000
## Datos
* **n :** este parametro del problema indica el número de paradas donde un usuario puede solicitar un desplazamiento a otro parada.
* **m:** al igual que existen un número determinado de paradas, sucede lo mismo con los almacenes de guagua. Otro parametro del problema es el número de almacenes que existen.

* **k:** este parametor indica el número de guaguas que realizan rutas visitando las diferentes paradas, estas rutas serán planteadas en función de las distintas peticiones de movilidad que vayan surgiendo a lo largo de la ejecución del programa, tal y como se menciona previamente estas guaguas deben estar situadas inicialmente en un almacén.

* **u:** dado que puede haber más de un almacén en nuestro problema debemos de conocer en cuál de ellos esta estacionado cada vehículo, por lo que esto será otro parametro de entrada al problema.

* **t_ij:** tiempo de desplazamiento entre los puntos *i* y *j* (paradas o almacenes).

* **d_ij:** Distancia entre los puntos *i* y *j* (paradas o almacenes).


## Características

* Una petición de desplazamiento, *p*, es una t-upla de la forma (*i*, *j*, *t_p*), que indica que en el instante *t_p* una persona ha solicitado el desplazamiento desde la parada *i* hasta la parada *j*. Es relevante conocer el instante en el que se ha generado la petición para posteriormente conocer el tiempo de servicio de cada usuario.

* Tal y como se menciono previamente, inicialmente cada vehículo está en una estación determinada, una vez finalice su jordana dicho vehículo debe volver a la misma estación de la cual partió.

* Los vehículos están disponibles al comienzo de cada jornada laboral y no sufren interrupción a lo largo del día.

* Al comienzo del día hay un conjunto de peticiones de desplazamiento conocidas. Sin embargo, a lo largo del día van apareciendo nuevas peticiones de desplazamiento entre paradas que requieren una reorganización de las rutas iniciales. Es por eso que la ruta de cada vehículo no es conocida de antemano, serán las nuevas peticiones de movilidad generadas por los usuarios en paradas las que reorganicen la ruta de la guagua.

* Cada vehículo tarda una cierta cantidad de tiempo de recoger a los pasajeros en una parada. Dicha cantidad de tiempo es igual para cada parada y para cada vehículo sin importar el momento del día en el que se recoja al pasajero.

* Todas las peticiones de desplazamiento deben ser atendidas.

* La jornada laboral de los vehículos es conocida. Por ejemplo de 6:00 a 23:00.

## Objetivos

* Minimizar la suma de las distancias de las rutas de los vehículos

* Minimizar la suma de tiempo de servicio de todos los pasajeros (tiempo de espera + tiempo de desplazamiento hasta destino)
