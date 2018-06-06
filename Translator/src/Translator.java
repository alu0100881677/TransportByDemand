import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;


public class Translator {
	private final static int initialDelay = 5000;
	
	public static void main(String[] args) throws Exception {
		Thread.sleep(Translator.initialDelay);
		if(args.length != 2) {
			System.out.println("Debe de pasar el nombre del directorio donde se han generado los frentes de pareto bajo la ruta jMetalSP-master/jmetalsp-examples/");
			System.out.println("El segundo argumento debe de ser el periodo, expresado como número de milisegundos, que pasa entre la traducción de cada fichero");
			System.exit(-1);
		}
		File x = new File("jmetalsp-examples/" + args[0]);
		String ruta = x.getAbsolutePath();
		String nuevaRuta ="";
		StringTokenizer st = new StringTokenizer(ruta, "/");
		while(st.hasMoreTokens()) {
			String directory =st.nextToken();
			if((!directory.equals("Translator")) && (!directory.equals("JarFiles"))){
				nuevaRuta += "/" + directory;
			}
		}
		String baseDir = nuevaRuta;
		File dir = new File(baseDir);
		System.out.println("Buscando archivos de frente de pareto en --> " + baseDir);
		String[] ficheros = dir.list();
		int numFicherosFun = ficheros.length / 2;
		int i = 0;
		while(i < numFicherosFun) {
			ficheros = dir.list();
			numFicherosFun =  ficheros.length / 2;
			System.out.println("Existen " + numFicherosFun + " ficheros en el directorio actual");
			Translator.TransformToCSV(baseDir + "/FUN" + i + ".tsv", "FUN" + i);
			System.out.println("Fichero--> " + "/FUN" + i + ".tsv TRADUCIDO" );
			Thread.sleep(Integer.parseInt(args[1]));
			i++;
		}
	}
	
	
	public static void TransformToCSV(String filename, String name) throws Exception{
		FileReader fich = new FileReader(filename);
	      BufferedReader buf = new BufferedReader(fich);
	      String cadena;
	      FileWriter fw = new FileWriter("datos.csv",false);
	      String linea = null;
	      BufferedWriter bw = new BufferedWriter(fw);
	      bw.write("symbol,x,y\n");
	      while((cadena = buf.readLine())!=null){
	    	  cadena = cadena.trim();
	    	  String inst2[] = cadena.split("\\s+");
	    	  linea = name + "," + inst2[0] + "," + inst2[1];
	    	  bw.write(linea + "\n");
	      }
	      buf.close();
	      fich.close();
	      bw.close();
	  }

}
