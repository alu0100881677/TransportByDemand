package org.uma.jmetalsp.examples.PRBD;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.problem.prbd.PRBDMatrixData;
import org.uma.jmetalsp.problem.tsp.DynamicMultiobjectiveTSP;
import org.uma.jmetalsp.problem.tsp.TSPMatrixData;


public class StreamingPRBDSource implements StreamingDataSource<SingleObservedData<PRBDMatrixData>> {
	private Observable<SingleObservedData<PRBDMatrixData>> observable;
	private int dataDelay ;
	private int timeline = 0;
	private String fileName = "";

	public StreamingPRBDSource(Observable<SingleObservedData<PRBDMatrixData>> observable, int dataDelay, String fileName) {
		this.observable = observable ;
		this.dataDelay = dataDelay ;
		this.fileName = fileName;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(dataDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			FileReader fich;
			try {
				fich = new FileReader(fileName);
				
				BufferedReader buf = new BufferedReader(fich);
				String cadena = "";
				
							
				while((cadena = buf.readLine()) != null){
					StringTokenizer tokens = new StringTokenizer(cadena, " ");
					int x = Integer.parseInt(tokens.nextToken());
					int y = Integer.parseInt(tokens.nextToken());
					int val = Integer.parseInt(tokens.nextToken());
					//si el instante supera el final de la jornada laboral 8h no se añade
					if(val < (8*60*60)) {
						observable.setChanged();
						observable.notifyObservers(new SingleObservedData<PRBDMatrixData>(new PRBDMatrixData("PETITION", x, y, val)));
					}
				}
				buf.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public Observable<SingleObservedData<PRBDMatrixData>> getObservable() {
		return this.observable;
	}
}


/**
 * esquema generico
 * detallar esquema
 * 
 * */
