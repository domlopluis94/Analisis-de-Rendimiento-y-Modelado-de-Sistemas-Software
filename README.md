# [Análisis de Rendimiento y Modelado de Sistemas Software](https://zistvan.github.io/teaching/pams19.html)

## Partes a Desarrollar

* Cleaning of document from special words: Our input will be (fragments of) HTML documents that

 - **Nos falta el algoritmo de limpieza que se dedicara a ir caracter a carcater buscando si hay < y > de modo que cuando encuentre < descartara todos los valores hasta su cierre >** 

* Add fine-grained statistics gathering inside the server and print out the aggregate statistics once all
clients have disconnected (that is, the experiment is finished). The instrumentation of the code should
measure:

 	- time spent until the entire document has been received
 	-	time spent cleaning the document (removing tags)
 	-	time spent performing the word count
 	-	time spent serializing the results
In terms of final aggregate printout, the average and the percentiles (0-100 in 1% steps) are needed. It
is not necessary to separate statistics per client, but you might consider using data structures dedicated to each client before merging the results to avoid synchronization overheads.

 - **Nos falta comprobar los tiempos dentro de la limpieza del documento y guardarlos de modo que sea optimo realizar las pruebas** 

* Extend the server into a multi-threaded implementation where there are W worker threads
performing the word count (inter-client parallelism).

 - **Entendemos que sobre el .sh se especifican el numero de hilos, en caso de que eso cree problemas de concurrencia tendriamos que crear threads para cada clientes, probablemente seria añadir esos hilos al main justo en el momento en el que se realiza la llamda al servidor, Pero en la presentación dice que expliquemos como hemos implementado esto**

* **Subir a AWS**
 - **Crear dos maquinas una cliente y otra servidor**
 - **Subir el código asignando correctamente la IP de cada maquina**
 - **Ejecutar el código de forma que podamos ver los resultados de todo el proceso**

* **Con los resultados en un excel podemos crear las graficas y los calculos para conocer el rendimiento del sistema**



## Algoritmo de limpieza 

Tendremos que ir caracter a caracter evaluando la situación, Tendremos que reconocer los caracteres < >. Pero hay un problema, como vemos acotinuación puede encontrarse estos simbolos dentro del texto normal.  

			<p> hola 2 < 3 </p> 
<p> hola 2 < 3 </p> 

Entonces podemos hacer lo siguiente ir carcater a caracter mirando: 

Vamos a usar 3 variables LINE (**nos la dan**) , TAGLINE , CLEAN (**Donde almacenamos el resultado**)

* Cuando encontramos < Guardamos los siguientes caracteres en TAGLINE hasta:
 	- Si encotramos > ponemos TAGLINE a 0 y seguimos.
 	- Si no encontramos o encontramos < , significa que esa etiqueta no se cierra y guardamos TAGLINE en CLEAN.
* Cuando encontramos un caracter distinto de > < lo que hacemos es mirar que TAGLINE esta vacio **Osea no estamos en el estado comprobando que es un TAG** y si esta vacio metemos el caracter en CLEAN.

Osea tenemos 2 estados :

* Evaluando si es TAG
 - Empieza cuando hay un <
 - Termina cuando hay un > o otro <
* Capturando TEXTO
 - En este estado TAGLINE tiene que estar a null/0 


## Medición de Tiempos

* El cliente nos da su tiempo de preparación del documento al ejecutarse, como mucho podriamos hacer que se guarden los resultado en un excel 

* El servidor, tenemos puesto medicion durante la limpieza del documento, el tema de la serialización hay que revisarlo pero parece que falta apañar de forma correcta el documento que se envia al cliente.

* En AWS si lo hemos realizado correctamente en local simplemente tendremos que correr varias veces el programa y quedarnos con los resultados y evaluarlos. 


## Evaluación de resultados

Pagina de Evaluación [M/M/1](https://www.supositorio.com/rcalc/rcalclite.htm) 

Necesitamos conseguir

[Docs](https://zistvan.github.io/teaching/pams18/L3-QTheory.pdf)

* Mean arrival rate: λ 
* Mean service rate μ 

Con Esto ya podemos aplicar las funciones, ademas podemos hacer graficos con los tiempos de respuesta y los tiempos dependiendo del numero de clientes...


## Presentación y Documento


### Puntos para la Presentación

* Describe shortly how you implemented the HTML cleaning
* Describe shortly how you implemented multi-threading
* State the default experiment length and replication factor
* Show an example graph with throughput over time (at least 1 minute) that motivates how you chose the warm-up  and cool-down times
* Show throughput with increasing number of clients for the following three variants of the system (on a single graph):
 - 1) default skeleton
 - 2) single-threaded server with HTML tag cleaning
 - 3) multi-threaded server with tag cleaning. 
* Highlight the place where system becomes saturated in each case
* Show graph of internal costs of the server given different document types (plot time per document, include at least two different costs)
* State which operation is your bottleneck in reaching higher performance and give a short argument why
* What did you learn about the system through experiments?
* What new skill did you learn while working on the project?
* What was the most difficult aspect of the project?

### Para el documento 
* **Implementation overview** 
: In this section you should explain how you implemented the three features listed in the project description. Make sure to elaborate on how multi-threading works in your system and what data structures are accessed by multiple threads, if any. 
Feel free to include a drawing of the internal architecture if it helps to illustrate at what points of the server logic you have added instrumentation for performance measurement. 
Explain shortly how you automated the experiments, how you collected the log files from both the clients and the server and how did you process them. 
* **Baselines**
: The first section of the report establishes the baselines of performance for the word count server and explores what effect the additional functionality has when compared to the plain skeleton.
 * **2.1 Test Environment and Experimental Design**
You will run the experiments using Amazon EC2, with two t2.2xlarge machines (one for the client and one for the server).
Explain the default setup for the experiments in terms of length, repetitions, amount of warm-up and cool-down time. Explain why you chose these specific values. We recommend that you maintain the same setup for all experiments in the paper to ensure that numbers can be correctly compared.
 * **2.2 Throughput and Response Time**
Using 1 to 16 clients  on a single client machine and the default 16KB document size, plot the throughput and response times as measured by the clients for the following server variants: 1) skeleton, 2) single-threaded server with HTML tag cleaning, 3) multi-threaded server with tag cleaning. Repeat the experiment with input document type 1 and input document type 2.
You need to provide four plots in total, two per input type: one for throughput and one for response time, each of them with multiple lines for the different server variants. Please follow the best practices presented in class when plotting.
 * **2.3 Discussion**
Discuss the behavior of the system and the overheads (if any) that your implementation introduces. Identify and quantify the cost of performing HTML cleaning and comment on how well the system parallelizes with increasing number of clients.
Please make sure that in the explanations you don’t just describe what is in the graphs, but instead put the results in the context of your design decisions and implementation details.
* **Effect of Document Size**
 * **3.1 Time Spent in Server**
Based on the server version with all features included (that is, variant 3) , pick a number of clients C just below the saturation point of Section 2.2 and run experiments with different document sizes between 4KB and 256KB (at least 7 different sizes) both on input type 1 and 2. Plot how the response time changes at the clients, as well as inside the server (for this, show at least 2 different internal costs).
 * **3.2 Discussion**
Describe how the relative cost of different operations inside the server change and how this relates to your implementation decisions. Identify the operation that limits performance the most. 
Please make sure that in the explanations you don’t just describe what is in the graphs, but instead put the results in the context of your design decisions and implementation details.

* **Modeling**
 * **4.1 M/M/m**
Using the experimental results in Section 2.2 variant 3) and the insights you gathered in Section 3, build a model of your system using an M/M/m queue. 
Plot the predicted response time and throughput as a function of load   and compare this to the real-world results.
 * **4.2 Discussion** 
Elaborate on how well the models match the real-world behavior and how these results relate to your design decisions.
Please make sure that in the explanations you don’t just describe what is in the graphs, but instead put the results in the context of your design decisions and implementation details.


