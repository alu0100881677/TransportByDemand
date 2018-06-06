package com.ull.tfg.prbd;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Launcher {
	
	public static void main(String args[]) {
		if(args.length == 0) {
			System.out.println("Ejecute2 el programa con la opción -h para mostrar la ayuda");
			System.exit(-1);
		}
		if(args[0].equals("-h")) {
			System.out.println("Para ejecutar este programa debe de pasar 4 argumentos:\n"
					+ "\t - nombre que recibirá el fichero\n"
					+ "\t - número de paradas del problema a resolver\n"
					+ "\t - número que representa el paso del tiempo entre petición y petición, se aconseja 150\n"
					+ "\t - número de milisegundos entre la generación de cada petición (5 segundos = 5000)\n");
			System.exit(-1);
		}
		else if(args.length != 4) {
			System.out.println("Ejecuteeeeee el programa con la opción -h para mostrar la ayuda");
			System.exit(-1);
		}else {
			new Launcher().configurarApplicationProperties(args);
			//new PrbdGeneratorApplication().arrancar();
		}
	}
	
	public void configurarApplicationProperties(String args[]) {
		String cadena = "";
	    FileWriter fw;
		try {
			fw = new FileWriter("src/main/resources/application.properties",false);
			BufferedWriter bw = new BufferedWriter(fw);
		    bw.write("key.fileName=" + args[0] + "\n" + 
		    		"key.numStations=" + args[1] + "\n" + 
		    		"key.petitionDelay=" + args[2] + "\n" + 
		    		"key.fixedDelay=" + args[3] + "\n");
		    bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
