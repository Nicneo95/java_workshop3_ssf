package com.example.workshop3;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.example.workshop3.util.IOUtil.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.DefaultApplicationArguments;

@SpringBootApplication
public class Workshop3Application {

	private static final Logger logger = LoggerFactory.getLogger(Workshop3Application.class);

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Workshop3Application.class);
		DefaultApplicationArguments appArgs = new DefaultApplicationArguments(args);
		List<String> opsVal = appArgs.getOptionValues("dataDir");
		System.out.println(opsVal);
		if (opsVal != null) {
			logger.info("" + (String) opsVal.get(0));
			createDir((String) opsVal.get(0));
		} else {
			logger.warn("No data directory was provided");
			System.exit(1);
		}
		app.run(args);
	}

}