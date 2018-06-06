package org.uma.jmetalsp.problem.prbd;
import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;
import org.uma.jmetalsp.problem.tsp.TSPMatrixData;

public class DynamicMultiobjectivePRBD 
				extends AbstractIntegerProblem
				implements ConstrainedProblem<IntegerSolution>,
				DynamicProblem<IntegerSolution,SingleObservedData<PRBDMatrixData>>{
	
	public static final double NON_CONNECTED = Double.POSITIVE_INFINITY ;
	private int 		numberOfBuses;
	private int         numberOfStations ;
	private double [][] distanceMatrix ;
	private double [][] costMatrix;
	private ArrayList<Integer> busesLocations;
	private Observable<SingleObservedData<PRBDMatrixData>> observable ;
	private ArrayList<PRBDMatrixData> peticiones;
	private int timeline = 0;
	
	private final int PENALIZACION = 10000;
	
	private boolean theProblemHasBeenModified;
	
	public OverallConstraintViolation<IntegerSolution> overallConstraintViolationDegree ;
	public NumberOfViolatedConstraints<IntegerSolution> numberOfViolatedConstraints;

	
	public DynamicMultiobjectivePRBD(int numBuses, int numStations, ArrayList<Integer> busLocations,
									double[][] distanceMatrix,
									double[][] costMatrix,
									Observable<SingleObservedData<PRBDMatrixData>> observable) throws InterruptedException {
		
		this.numberOfBuses = numBuses;
		this.numberOfStations = numStations;
		this.busesLocations = new ArrayList<Integer>(busLocations);
		peticiones = new ArrayList<PRBDMatrixData>();
		this.costMatrix = costMatrix;
		this.distanceMatrix = distanceMatrix;
		this.observable = observable;
		this.numberOfBuses = busesLocations.size();
		
		theProblemHasBeenModified = false;
		
		setName("DMoPRBD");
		setNumberOfVariables(numberOfBuses * numberOfStations + (numberOfBuses - 1));
		setNumberOfObjectives(2);
		setNumberOfConstraints(1);
		
		//Estableciendo el rango de la representación de la solución
		List<Integer> upperLimit = new ArrayList<Integer>();
		List<Integer> lowerLimit = new ArrayList<Integer>();
		final int lower = -1;
		final int upper = numberOfStations - 1;
		for(int i = 0; i < getNumberOfVariables(); i++) {
			upperLimit.add(upper);
			lowerLimit.add(lower);
		}
		setLowerLimit(lowerLimit);
		setUpperLimit(upperLimit);
		
		overallConstraintViolationDegree = new OverallConstraintViolation<IntegerSolution>() ;
		numberOfViolatedConstraints = new NumberOfViolatedConstraints<IntegerSolution>();
	}
	
	
	public DynamicMultiobjectivePRBD(int numStations, int numBuses, ArrayList<Integer> busesLocations,
									double[][] distanceMatrix, double[][] costMatrix) throws InterruptedException {
		this(numBuses, numStations, busesLocations, distanceMatrix, costMatrix, new DefaultObservable<>());
	}
	
	
	
	public synchronized void updateCostValue(int row, int col, double newValue) {
		    costMatrix[row][col] = newValue ;
		    theProblemHasBeenModified = true ;
	}

	public synchronized void updateDistanceValue(int row, int col, double newValue) {
		distanceMatrix[row][col] = newValue ;
		theProblemHasBeenModified = true ;
	}
	
	public synchronized void addPetition(PRBDMatrixData data) {
		peticiones.add(data);
		theProblemHasBeenModified = true;
	}

	public synchronized double[][] getDistanceMatrix() {
		return distanceMatrix;
	}

	public synchronized double[][] getCostMatrix() {
		return costMatrix;
	}
	  
	public synchronized boolean hasTheProblemBeenModified() {
		return theProblemHasBeenModified;
	}

	@Override
	public synchronized void reset() {
		theProblemHasBeenModified = false ;
	}


	public int getNumberOfBuses() {
		return numberOfBuses;
	}


	public void setNumberOfBuses(int numberOfBuses) {
		this.numberOfBuses = numberOfBuses;
	}

	public int getNumberOfStations() {
		return numberOfStations;
	}


	public void setNumberOfStations(int numberOfStations) {
		this.numberOfStations = numberOfStations;
	}


	public ArrayList<Integer> getBusesLocations() {
		return busesLocations;
	}


	public void setBusesLocations(ArrayList<Integer> busesLocations) {
		this.busesLocations = busesLocations;
	}
	
	public String toString() {
	    String result = "" ;
	    for (int i = 0; i < numberOfStations; i++) {
	    	for (int j = 0; j < numberOfStations; j++) {
	    		result += "" +  distanceMatrix[i][j] + " " ;
	    	}
	    	result += "\n" ;
	    }
	    return result ;
	  }
	
	@Override
	public void update(Observable<SingleObservedData<PRBDMatrixData>> observable, SingleObservedData<PRBDMatrixData> data) {
		if (data!=null && data.getData().getMatrixIdentifier() == "PETITION") {
			addPetition(data.getData());
	    } 
		//else if(data!=null && data.getData().getMatrixIdentifier() == "VALUE"){
		//	updateDistanceValue(data.getData().getX(),data.getData().getY(),data.getData().getInstant());
	    //}
	}

	
	@Override
	public synchronized void evaluate(IntegerSolution solution) {
		//Evaluación del primer objetivo
		String traza = "";
		int busId = 0;
		double fitness1 = 0;
		double fitness2 = 0;
		int x = busesLocations.get(busId);
		x = x + numberOfStations - 1;
		int i = 0;
		int y = 0;
		/*System.out.println("PETICIONES DE USUARIOS");
		for(int ii  = 0; ii < peticiones.size(); ii++) {
			System.out.println(peticiones.get(ii));
		}
		System.out.println("SOLUCION ACTUAL");
		for(int p = 0; p < getNumberOfVariables(); p++) {
			System.out.print(solution.getVariableValue(p) + " ");
		}
		System.out.println();*/
		while((y = solution.getVariableValue(i)) == -1) {
			busId++;
			if(busId >= numberOfBuses) {
				busId = busId % numberOfBuses;
			}
			i++;
		}
		x = busesLocations.get(busId) + numberOfStations - 1;
		timeline += costMatrix[x][y];
		traza += "[" + x + "][" + y + "]" + distanceMatrix[x][y] + "\n";
		fitness1 += distanceMatrix[x][y];
		fitness2 += evaluarPasajeros(y, busId);
		for(int j = i; j < getNumberOfVariables() - 1; j++) {		
			x = solution.getVariableValue(j);
			y = solution.getVariableValue(j + 1);
		    //System.out.println("Evaluando x = " + x + ", y = " + y + " TIEMPO : " + timeline);
			if(x == -1) {
				fitness2 += penalizarPasajerosNoServidos();
		        busId++;
		        if(busId >= numberOfBuses) {
		        	busId = busId % numberOfBuses;
		        }
		        //System.out.println("AHORA ESTA LA GUAGUA --> " + busId);
				if(y != -1) {
					fitness1 += distanceMatrix[busesLocations.get(busId) + numberOfStations - 1][y];
					traza += "[" + (busesLocations.get(busId) + numberOfStations - 1) + "][" + y + "]" + distanceMatrix[busesLocations.get(busId) + numberOfStations - 1][y] + "caso 1\n";
		            timeline += costMatrix[busesLocations.get(busId) + numberOfStations - 1][y];
		        }
			}
			else if(y == -1) {
				fitness1 += distanceMatrix[x][busesLocations.get(busId) + numberOfStations - 1];
				traza += "[" + x + "][" + (busesLocations.get(busId) + numberOfStations - 1) + "]" + distanceMatrix[x][busesLocations.get(busId) + numberOfStations -1] + "caso 2\n";
		        fitness2 += evaluarPasajeros(x, busId);
		        timeline += costMatrix[x][busesLocations.get(busId) + numberOfStations - 1];
		    }
			else {
				fitness1 += distanceMatrix[x][y];
				traza += "[" + x + "][" + y + "]" + distanceMatrix[x][y] + " caso 3\n";
		        fitness2 += evaluarPasajeros(x, busId);
		        timeline += costMatrix[x][y];
			}
		}
		if(y != -1) {
			fitness2 += evaluarPasajeros(y, busId);
			if(x != -1) {
				timeline += costMatrix[x][y];
			}
			else {
				timeline += costMatrix[busId][y];
			}
			fitness1 += distanceMatrix[y][busesLocations.get(busId) + numberOfStations - 1];
			traza += "[" + (busesLocations.get(busId) + numberOfStations - 1) + "][" + y + "] fuera del for\n";
		}
		//System.out.println(traza);
		//System.out.println("Pimer objetivo: " + fitness1);
		//System.out.println("Fitness dos vale " + fitness2 + "\n\n");
		fitness2 +=  penalizarPasajerosOmitidos();
		solution.setObjective(0, fitness1);
		solution.setObjective(1, fitness2);
		resetPasajeros();
		timeline = 0;
		//try {
		//	Thread.sleep(2000);
		//} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
	}
	
	public int evaluarPasajeros(int parada, int idBus) {
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
				//System.out.println("Se BAJA en --> " + parada + "de la guauga --> " + idBus);
			}
		}
		return fitness;
	}
	
	public int penalizarPasajerosNoServidos() {
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
	
	public int penalizarPasajerosOmitidos() {
		int fitness = 0;
		for(int i  = 0; i < peticiones.size(); i++) {
			PRBDMatrixData peticion = peticiones.get(i);
			if((peticion.getServed() ==  false) && (peticion.getRide() ==  false) && (peticion.getInstant() < timeline)) {
				peticion.setServed(true);
				peticion.setRide(false);
				fitness += PENALIZACION;
				//System.out.println("Penalizando --> " + peticion + "no llego a subir a la gugua");
			}
		}
		return fitness;
				
	}
	

	
	public synchronized void evaluateConstraints(IntegerSolution solution) {
		int nonExistingBuses = 0;
		for(int i = 0; i < getNumberOfVariables(); i++) {
			if(solution.getVariableValue(i) == -1) {
				nonExistingBuses++;
			}
		}
		int ref = Math.abs(nonExistingBuses - (numberOfBuses - 1));
		overallConstraintViolationDegree.setAttribute(solution, -1.0 * ref);
		numberOfViolatedConstraints.setAttribute(solution, ref);
	}
	
	public void resetPasajeros() {
		for (int i = 0; i < peticiones.size(); i++) {
			peticiones.get(i).reset();
		}
	}
	  
	
	public void printMatrix(double[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}
	}
	  
}
