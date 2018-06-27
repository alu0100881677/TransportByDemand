import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class Analizador {
	private int 		numberOfBuses;
	private int         numberOfStations ;
	private double [][] distanceMatrix ;
	private double [][] costMatrix;
	private ArrayList<Integer> busesLocations;
	private ArrayList<PRBDMatrixData> peticiones;
	private int numAlmacenes;
	private int timeline = 0;
	private final int PENALIZACION = 0;
	private int pasajerosServidos = 0;

	
	public static void main(String[] args) throws Exception {
		/*
		 * Experimentos/small/1/peticionesContempladas.txt
		 *  Datos/ficheroDistancias20.prbd 
		 *  Datos/ficheroCostes20.prbd 
		 *  Datos/busesLocations20.prbd 
		 *  Experimentos/small/1/VAR12.tsv
		 */
		String experimento = "";
		for(int i = 0 ; i < 3; i++) {
			if(i == 0) {
				experimento = "Experimentos/small/";
			}
			else if(i == 1) {
				experimento = "Experimentos/medium/";
			}
			else {
				experimento = "Experimentos/large/";
			}
			for(int j = 1 ; j <= 10; j++) {
				String aux = experimento;
				aux += j + "/peticionesContempladas.txt";
				String aux2 = experimento;
				aux2 += j + "/VAR.tsv";
				String dist = "Datos/ficheroDistancias";
				String cost = "Datos/ficheroCostes";
				String buses = "Datos/busesLocations";
				if(i == 0) {
					dist += "20.prbd";
					cost += "20.prbd";
					buses += "20.prbd";
				}
				else if(i == 1) {
					dist += "40.prbd";
					cost += "40.prbd";
					buses += "40.prbd";				
				}else {
					dist += "60.prbd";
					cost += "60.prbd";
					buses += "60.prbd";				
				}
				Analizador analisis = new Analizador(recogerPeticiones(aux), dist, cost, buses);
				String contenido = analisis.comenzarAnalisis(aux2);
				
				FileWriter fichero = null;
		        PrintWriter pw = null;
		        try {
					fichero = new FileWriter(experimento + j + "/analisis.csv", false);
					pw = new PrintWriter(fichero);
					pw.write("Objetivo 1; Objetivo 2; Porcentaje de peticiones servidas; Solucion factible\n");
					pw.write(contenido);
					pw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		

	}
	
	
	public Analizador(ArrayList<PRBDMatrixData> petitions, String distanceFileName, String costFileName, String busesLocationsFileName) throws Exception {
		peticiones = new ArrayList<PRBDMatrixData>(petitions);
		distanceMatrix = readMatrixProblem(distanceFileName);
		costMatrix = readMatrixProblem(costFileName);
		busesLocations = (ArrayList<Integer>) readBusesLocationsFile(busesLocationsFileName).clone();
		numberOfBuses = busesLocations.size();
		numberOfStations = distanceMatrix.length - numAlmacenes;
	}
	
	public static ArrayList<PRBDMatrixData> recogerPeticiones(String fileName) throws IOException{
		ArrayList<PRBDMatrixData> peticiones = new ArrayList<PRBDMatrixData>();
		FileReader fich = new FileReader(fileName);
		BufferedReader buf = new BufferedReader(fich);
		String cadena = "";
		while((cadena = buf.readLine()) != null){
			//System.out.println(cadena);
			StringTokenizer st = new StringTokenizer(cadena, " ,]", false);
			for(int i = 0; i < 7; i++) {
				st.nextToken();
			}
			int origen = Integer.parseInt(st.nextToken());
			for(int i = 0; i < 4; i++) {
				st.nextToken();
			}
			int destino = Integer.parseInt(st.nextToken());
			for(int i = 0; i < 4; i++) {
				st.nextToken();
			}
			int instante = Integer.parseInt(st.nextToken());
			peticiones.add(new PRBDMatrixData("PETITION", origen, destino, instante));
			cadena = buf.readLine();
		}
		return peticiones;
	}
	

	private double [][] readMatrixProblem(String file){
		double [][] matrix = null;
		FileReader lector  = null;
	    BufferedReader br = null;
	    try {
	    	File arch = new File(file);
	        if(!arch.exists()){
	           System.out.println("No existe el archivo");
	        }else{
	           lector = new FileReader(file);
	           br = new BufferedReader(lector);
	        }
	        String linea = br.readLine();
	        while (linea.startsWith("#")) {
				linea = br.readLine();			
			}
	        int numberOfPoints = Integer.parseInt(linea);
	        matrix =  new double[numberOfPoints][numberOfPoints];
	        for(int i = 0; i < numberOfPoints; i++) {
	        	linea = br.readLine();
	        	StringTokenizer st = new StringTokenizer(linea, " ", false);
	        	for(int j = 0; j < numberOfPoints; j++) {
	        		matrix[i][j] = Double.parseDouble(st.nextToken());
	        	}
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return matrix;
	    
	}
	
	private ArrayList<Integer> readBusesLocationsFile(String file) throws Exception{
		ArrayList<Integer> busesPosition = new ArrayList<Integer>();
		
		
		FileReader fich = new FileReader(file);
		BufferedReader buf = new BufferedReader(fich);
		String cadena = "";
		
		
		while(((cadena = buf.readLine()) != null) && (cadena.startsWith("#"))){
			;
		}
		
		numAlmacenes = Integer.parseInt(cadena);
		
		while((cadena = buf.readLine()) != null){
			if(cadena.startsWith("#")) {
				;
			}
			else {
				int busPosition = Integer.parseInt(cadena);
				if(busPosition <= numAlmacenes) {
					busesPosition.add(busPosition);
				}
				else {
					throw new Exception("La posición de la guagua es invalida");
				}
				
			}
		}
		return busesPosition;
	}
	
	public String evaluate(ArrayList<Integer> solution) {
		int numeroDeMenosUnos = 0;
		int busId = 0;
		double fitness1 = 0;
		double fitness2 = 0;
		int x = busesLocations.get(busId);
		x = x + numberOfStations - 1;
		pasajerosServidos = 0;
		int i = 0;
		int y = 0;
		while((y = solution.get(i)) == -1) {
			busId++;
			numeroDeMenosUnos++;
			if(busId >= numberOfBuses) {
				busId = busId % numberOfBuses;
			}
			i++;
		}
		x = busesLocations.get(busId) + numberOfStations - 1;
		timeline += costMatrix[x][y];
		//traza += "[" + x + "][" + y + "]" + distanceMatrix[x][y] + "\n";
		fitness1 += distanceMatrix[x][y];
		fitness2 += evaluarPasajeros(y, busId);
		for(int j = i; j < solution.size() - 1; j++) {		
			x = solution.get(j);
			y = solution.get(j + 1);
		    //System.out.println("Evaluando x = " + x + ", y = " + y + " TIEMPO : " + timeline);
			if(x == -1) {
				fitness2 += penalizarPasajerosNoServidos();
		        busId++;
		        numeroDeMenosUnos++;
		        if(busId >= numberOfBuses) {
		        	busId = busId % numberOfBuses;
		        }
		        if(timeline > (8* 60 * 60)) {
		        	//penalización por durar más de las horas de una jornada
		        	//System.out.println("Penalización por durar más de las horas de una jornada, horas de más --> " + ((timeline - 28800) / (60 * 60)) );
		        	fitness1 += PENALIZACION;
		        }
		        //ruta de nueva guagua tiempo cero
		        timeline = 0;
		        //System.out.println("AHORA ESTA LA GUAGUA --> " + busId);
				if(y != -1) {
					fitness1 += distanceMatrix[busesLocations.get(busId) + numberOfStations - 1][y];
					//traza += "[" + (busesLocations.get(busId) + numberOfStations - 1) + "][" + y + "]" + distanceMatrix[busesLocations.get(busId) + numberOfStations - 1][y] + "caso 1\n";
		            timeline += costMatrix[busesLocations.get(busId) + numberOfStations - 1][y];
		        }
			}
			else if(y == -1) {
				fitness1 += distanceMatrix[x][busesLocations.get(busId) + numberOfStations - 1];
				//traza += "[" + x + "][" + (busesLocations.get(busId) + numberOfStations - 1) + "]" + distanceMatrix[x][busesLocations.get(busId) + numberOfStations -1] + "caso 2\n";
		        fitness2 += evaluarPasajeros(x, busId);
		        timeline += costMatrix[x][busesLocations.get(busId) + numberOfStations - 1];
		    }
			else {
				fitness1 += distanceMatrix[x][y];
				//traza += "[" + x + "][" + y + "]" + distanceMatrix[x][y] + " caso 3\n";
		        fitness2 += evaluarPasajeros(x, busId);
		        timeline += costMatrix[x][y];
			}
		}
		if(y != -1) {
			fitness1 += distanceMatrix[y][busId];
			fitness2 += evaluarPasajeros(y, busId);
		}
		fitness2 +=  penalizarPasajerosOmitidos();
		resetPasajeros();
		timeline = 0;
		double xxx = (pasajerosServidos * 100.0f) / (peticiones.size() * 100.0f);
		xxx = xxx * 100;
		xxx = Math.round(xxx*1000.0)/1000.0;
		String cadenaRetorno =  fitness1 + ";" + fitness2 + ";" + xxx + ";" ;
		if(numeroDeMenosUnos == numberOfBuses - 1) {
			cadenaRetorno += "SI";
		}else {
			cadenaRetorno += "NO";
		}
		return cadenaRetorno;
	}
	
	public synchronized int evaluarPasajeros(int parada, int idBus) {
		int fitness = 0;
		for(int i  = 0; i < peticiones.size(); i++) {
			PRBDMatrixData peticion = peticiones.get(i);
			//System.out.println(peticiones.get(i));
			if((peticion.getOrigin() == parada) && (peticion.getInstant() <= timeline) && (peticion.getServed() == false) && (peticion.getRide() == false)){
				peticion.setRide(true);
				peticion.setBus(idBus);
				//System.out.println("Se sube un pasajero en --> " + parada + " guagua --> " + idBus);
			}
			if((peticion.getDestiny() == parada) && (peticion.getRide()) && (!peticion.getServed()) && (peticion.getBusId() == idBus) ){
				peticion.setServed(true);
				peticion.setRide(false);
				fitness += timeline - peticion.getInstant();
				pasajerosServidos++;
				//System.out.println("Se BAJA en --> " + parada + "de la guauga --> " + idBus);
			}
		}
		return fitness;
	}
	
	public synchronized int penalizarPasajerosNoServidos() {
		int fitness = 0; 
		for(int i = 0; i < peticiones.size(); i++) {
			if((peticiones.get(i).getRide()) && (!peticiones.get(i).getServed())) {
				peticiones.get(i).setServed(true);
				//System.out.println("Penalizando la peticiones" + peticiones.get(i));
				fitness += PENALIZACION;
			}
		}
		return fitness;
	}
	
	public synchronized int penalizarPasajerosOmitidos() {
		int fitness = 0;
		for(int i  = 0; i < peticiones.size(); i++) {
			PRBDMatrixData peticion = peticiones.get(i);
			if((peticion.getServed() ==  false) && (peticion.getRide() ==  false)) {
				peticion.setServed(true);
				peticion.setRide(false);
				fitness += PENALIZACION;
			}
		}
		return fitness;
				
	}
	
	public void resetPasajeros() {
		for (int i = 0; i < peticiones.size(); i++) {
			peticiones.get(i).reset();
		}
	}
	
	
	public String comenzarAnalisis(String varFileName) throws NumberFormatException, IOException {
		FileReader fich = new FileReader(varFileName);
		BufferedReader buf = new BufferedReader(fich);
		String cadena = "";
		String contenidoFichero = "";
		
		while((cadena = buf.readLine()) != null){
			StringTokenizer st = new StringTokenizer(cadena, " ");
			ArrayList<Integer> solucion = new ArrayList<Integer>();
			while(st.hasMoreTokens()) {
				solucion.add(Integer.parseInt(st.nextToken()));
			}
			String analisisSolucion = evaluate(solucion);
			contenidoFichero += analisisSolucion + "\n";
		}
		return contenidoFichero;
	}
	
	
	

}
