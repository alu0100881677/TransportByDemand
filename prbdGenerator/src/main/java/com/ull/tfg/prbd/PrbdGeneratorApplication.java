package com.ull.tfg.prbd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class PrbdGeneratorApplication {

	public static void main(String[] args) {
		new PrbdGeneratorApplication().arrancar();	
	}
	
	public void arrancar() {
		ConfigurableApplicationContext ctx = SpringApplication.run(PrbdGeneratorApplication.class);
		ScheduledTasks s = ctx.getBean(ScheduledTasks.class);	
	}
}
