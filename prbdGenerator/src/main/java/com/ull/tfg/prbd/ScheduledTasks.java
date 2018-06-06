package com.ull.tfg.prbd;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Random;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

@ContextConfiguration(classes = {BasicPropertiesWithJavaConfig.class},loader = AnnotationConfigContextLoader.class)
@Component
public class ScheduledTasks implements ApplicationRunner{
	//java -Dspring.config.location=src/main/resources/application.properties -jar target/prbdGenerator-0.0.1-SNAPSHOT.jar
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    
    //private final String dir = "/Users/Guille/Google Drive/universidad/TFG/Datos";
    
    @Value("${key.numStations:100}")
    private int numStations;
    
    private int instante = 0;
    
    private boolean done = false;
    
    
    
    @Autowired
	private Environment env;
	
	@Value("${key.fileName:kroA100.prbd}")
	private String fileName;
	
	@Value("${key.petitionDelay:600}")
	private Integer petitionDelay;
    
    

    public ScheduledTasks() {
		super();
	}
    
    @Override
	public void run(ApplicationArguments args) throws Exception {
		
	}
    
	@Scheduled(initialDelay = 5000, fixedDelayString = "${key.fixedDelay:5000}")
    public void reportCurrentTime() throws Exception{
		if(!done) {
			done = true;
	    	File x = new File("jmetalsp-examples/Datos/" + fileName);
			System.out.println(x.getAbsolutePath());
			String ruta = x.getAbsolutePath();
			String nuevaRuta ="";
			StringTokenizer st = new StringTokenizer(ruta, "/");
			while(st.hasMoreTokens()) {
				String directory =st.nextToken();
				if((!directory.equals("prbdGenerator")) && (!directory.equals("target"))){
					nuevaRuta += "/" + directory;
					//System.out.println(nuevaRuta);
				}
			}
			fileName = nuevaRuta;	    	
		}
		
    	updateUserPetitions();
    	//instante++;
    	String cadena;
        FileReader f = new FileReader(fileName);
        BufferedReader b = new BufferedReader(f);
        while((cadena = b.readLine())!=null) {
            System.out.println(cadena);
        }
        b.close();
        log.info("The time is now {}", dateFormat.format(new Date()));
    }

    private void updateUserPetitions() throws IOException{
        FileWriter fichero = null;
        PrintWriter pw = null;
        try
        {        	
            fichero = new FileWriter(fileName, false);
            pw = new PrintWriter(fichero);
            Random r = new Random();
            for (int i = 0; i < r.nextInt(5); i++) {
            	instante += r.nextInt(petitionDelay);
                pw.println( r.nextInt(numStations) + " " + r.nextInt(numStations) + " " + instante);

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
           try {
               if (null != fichero)
                  fichero.close();
               } catch (Exception e2) {
                  e2.printStackTrace();
               }
        }

   }
}

