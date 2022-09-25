package it.one6n.report.integrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ReportIntegratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportIntegratorApplication.class, args);
	}

}
