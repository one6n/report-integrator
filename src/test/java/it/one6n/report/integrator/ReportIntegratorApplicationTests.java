package it.one6n.report.integrator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import lombok.Getter;

@Getter
@ActiveProfiles("test")
@SpringBootTest
class ReportIntegratorApplicationTests {

	@Test
	void contextLoads() {
	}
}
