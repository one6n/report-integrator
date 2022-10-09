package it.one6n.report.integrator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import it.one6n.report.integrator.services.ReportIntegratorService;
import lombok.Getter;

@Getter
@ActiveProfiles("test")
@SpringBootTest
class ReporIntegratorServiceTests {

	@Autowired
	private ReportIntegratorService reportIntegratorService;

	@Test
	void shouldReadIndexAllLines() {
		try {

		} finally {
		}
	}
}
