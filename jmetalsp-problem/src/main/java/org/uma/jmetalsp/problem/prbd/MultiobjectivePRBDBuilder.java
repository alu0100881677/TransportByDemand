package org.uma.jmetalsp.problem.prbd;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;
import org.uma.jmetalsp.problem.tsp.TSPMatrixData;

import javafx.util.Pair;

public class MultiobjectivePRBDBuilder {
	
	private String distanceFileName;
	private String costFileName;
	private String busesLocationsFileName;
	private int numAlmacenes;
	
	private Observable<SingleObservedData<PRBDMatrixData>> observable;

	public MultiobjectivePRBDBuilder(String distanceFileName, String costFileName, String busesLocations){
		this(distanceFileName, costFileName, busesLocations, new DefaultObservable<>());
	}
	
	public MultiobjectivePRBDBuilder(String distanceFileName, String costFileName, String busesLocations,Observable<SingleObservedData<PRBDMatrixData>> observable) {
		this.distanceFileName = distanceFileName;
		this.costFileName = costFileName;
		this.busesLocationsFileName = busesLocations;
		this.observable = observable;
	}
	
	public DynamicMultiobjectivePRBD build()throws Exception{
		int numberOfStops;
		
		int numberOfBuses;
		double[][] distanceMatrix;
		double[][] costMatrix;
		
		distanceMatrix = readProblem(distanceFileName);
		costMatrix = readProblem(costFileName);
		ArrayList<Integer> busesLocations = (ArrayList<Integer>) readBusesLocationsFile(busesLocationsFileName).clone();
		numberOfBuses = busesLocations.size();
		numberOfStops = distanceMatrix.length - numAlmacenes;
		
		
		DynamicMultiobjectivePRBD problem = new DynamicMultiobjectivePRBD( numberOfBuses, numberOfStops, busesLocations, distanceMatrix, costMatrix, observable);
		
		return problem;
		
	}
	
	private double [][] readProblem(String file) throws IOException {
		double [][] matrix = null;

	    InputStream in = getClass().getResourceAsStream(file);
	    if (in == null) {
	    	in = new FileInputStream(file) ;
	    }
	    InputStreamReader isr = new InputStreamReader(in);
	    BufferedReader br = new BufferedReader(isr);

	    StreamTokenizer token = new StreamTokenizer(br);
	    try {
	    	boolean found ;
	    	found = false ;

	    	token.nextToken();
	    	while(!found) {
	    		if ((token.sval != null) && ((token.sval.compareTo("DIMENSION") == 0)))
	    			found = true ;
	    		else
	    			token.nextToken() ;
	    	}

	    	token.nextToken() ;
	    	token.nextToken() ;

	    	int numberOfCities =  (int)token.nval ;

	    	matrix = new double[numberOfCities][numberOfCities] ;

	    	// Find the string SECTION
	    	found = false ;
	    	token.nextToken();
	    	while(!found) {
	    		if ((token.sval != null) &&
	    				((token.sval.compareTo("SECTION") == 0)))
	    			found = true ;
	    		else
	    			token.nextToken() ;
	    	}

	    	double [] c = new double[2*numberOfCities] ;

	    	for (int i = 0; i < numberOfCities; i++) {
	    		token.nextToken() ;
	    		int j = (int)token.nval ;

	    		token.nextToken() ;
	    		c[2*(j-1)] = token.nval ;
	    		token.nextToken() ;
	    		c[2*(j-1)+1] = token.nval ;
	    	} // for

	    	double dist ;
	    	for (int k = 0; k < numberOfCities; k++) {
	    		matrix[k][k] = 0;
	    		for (int j = k + 1; j < numberOfCities; j++) {
	    			dist = Math.sqrt(Math.pow((c[k*2]-c[j*2]),2.0) +
	    					Math.pow((c[k*2+1]-c[j*2+1]), 2));
	    			dist = (int)(dist + .5);
	    			matrix[k][j] = dist;
	    			matrix[j][k] = dist;
	    		}
	    	}
	    } catch (Exception e) {
	    	new JMetalException("MultiobjectivePRBDINITIALIZER.readProblem(): error when reading data file " + e);
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
	
}
