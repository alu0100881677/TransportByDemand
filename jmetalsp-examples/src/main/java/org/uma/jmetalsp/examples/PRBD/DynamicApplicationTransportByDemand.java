package org.uma.jmetalsp.examples.PRBD;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.IntegerSBXCrossover;
import org.uma.jmetal.operator.impl.crossover.PMXCrossover;
import org.uma.jmetal.operator.impl.mutation.IntegerPolynomialMutation;
import org.uma.jmetal.operator.impl.mutation.PermutationSwapMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetalsp.DataConsumer;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.JMetalSPApplication;
import org.uma.jmetalsp.algorithm.nsgaii.DynamicNSGAIIBuilder;
import org.uma.jmetalsp.consumer.ChartConsumer;
import org.uma.jmetalsp.consumer.LocalDirectoryOutputConsumer;
import org.uma.jmetalsp.impl.DefaultRuntime;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.observer.impl.DefaultObservable;
import org.uma.jmetalsp.problem.prbd.MultiobjectivePRBDBuilder;
import org.uma.jmetalsp.problem.prbd.PRBDMatrixData;
import org.uma.jmetalsp.problem.tsp.MultiobjectiveTSPBuilderFromNYData;
import org.uma.jmetalsp.problem.tsp.MultiobjectiveTSPBuilderFromTSPLIBFiles;
import org.uma.jmetalsp.problem.tsp.TSPMatrixData;


public class DynamicApplicationTransportByDemand {
	public static void main(String args[])throws Exception {
		/*
		 * args 0 --> fichero de distancias
		 * args 1 --> fichero de costes
		 * args 2 --> fichero con la localización de los buses
		 * args 3 --> nombre del fichero con las peticiones
		 * args 4 --> nombre del directorio que almacena los frentes de pareto
		 * args 5 --> periodo de lectura de nuevas peticiones en milisegundos
		 */
		
		switch (args.length) {
		case 1:
			if(args[0].equals("-h")) {
				System.out.println("* arg 0 --> fichero de distancias\n" + 
						"		 * arg 1 --> fichero de costes\n" + 
						"		 * arg 2 --> fichero con la localización de los buses\n" + 
						"		 * arg 3 --> nombre del fichero con las peticiones\n" + 
						"		 * arg 4 --> nombre del directorio que almacena los frentes de pareto\n" + 
						"		 * arg 5 --> periodo de lectura de nuevas peticiones en milisegundos(2000)\n"
						+ "TODOS LOS FICHEROS DEBEN DE ESTAR EN LA CARPETA jmetal-examples/Datos y solo debe indicar su nombre ");
			}
			else {
				System.out.println("Ejecute la opción -h para ver como se ejecuta el programa");
			}
			System.exit(-1);
			break;
		case 6:
			;
			break;
		default:
			System.out.println("Ejecute la opción -h para ver como se ejecuta el programa");
			System.exit(-1);
			break;
		}
		//Step 1. Create the problem
		DynamicProblem<IntegerSolution, SingleObservedData<PRBDMatrixData>> problem;
		problem = new MultiobjectivePRBDBuilder("Datos/" + args[0],"Datos/" + args[1], "Datos/" + args[2])
				.build();
		
		//Step 2. Create the algorithm
		CrossoverOperator<IntegerSolution> crossover;
		MutationOperator<IntegerSolution> mutation;
		SelectionOperator<List<IntegerSolution>, IntegerSolution> selection;
		
		
		double crossoverProbability = 0.9 ;
	    double crossoverDistributionIndex = 20.0 ;
	    crossover = new IntegerSBXCrossover(crossoverProbability, crossoverDistributionIndex) ;
	    	    		
		
		double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
	    double mutationDistributionIndex = 20.0 ;
	    mutation = new IntegerPolynomialMutation(mutationProbability, mutationDistributionIndex) ;
		
	    //selection = new BinaryTournamentSelection<IntegerSolution>() ;
	    
	    selection = new BinaryTournamentSelection<>(
	            new RankingAndCrowdingDistanceComparator<IntegerSolution>()); 
		
		DynamicAlgorithm<List<IntegerSolution>, AlgorithmObservedData<IntegerSolution>> algorithm;
		algorithm = new DynamicNSGAIIBuilder<>(crossover, mutation, new DefaultObservable<>())
	            .setMaxEvaluations(25000)
	            .setPopulationSize(100)
	            .setSelectionOperator(selection)
	            .build(problem);
		
		// Step 3. Create the streaming data source and register the problem		
		
		
		StreamingPRBDSource streamingPRBDSource = new StreamingPRBDSource(
				new DefaultObservable<>(), Integer.parseInt(args[5]),
				"Datos/" + args[3]);
		streamingPRBDSource.getObservable().register(problem);
		
		//Step 4. Create the data consumers and register into the algorithm
		DataConsumer<AlgorithmObservedData<IntegerSolution>> localDirectoryOutputConsumer =
	            new LocalDirectoryOutputConsumer<IntegerSolution>(args[4]);
	    //DataConsumer<AlgorithmObservedData<IntegerSolution>> chartConsumer =
	      //      new ChartConsumer<IntegerSolution>(algorithm);
	    
	    algorithm.getObservable().register(localDirectoryOutputConsumer);
	    //algorithm.getObservable().register(chartConsumer);
			    
	    
	    //Step 5 Create the application and run
	    JMetalSPApplication<
	    		IntegerSolution,
	    		DynamicProblem<IntegerSolution, SingleObservedData<Integer>>,
	    		DynamicAlgorithm<List<IntegerSolution>, AlgorithmObservedData<IntegerSolution>>> application;

		application = new JMetalSPApplication<>();
		
		application.setStreamingRuntime(new DefaultRuntime())
		        .setProblem(problem)
		        .setAlgorithm(algorithm)
		        .addStreamingDataSource(streamingPRBDSource, problem)
		        .addAlgorithmDataConsumer(localDirectoryOutputConsumer)
		        //.addAlgorithmDataConsumer(chartConsumer)
		        .run();
	}
}
