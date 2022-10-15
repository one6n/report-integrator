package it.one6n.report.integrator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import it.one6n.report.integrator.utils.ReportUtils;
import lombok.Getter;

@Getter
@ActiveProfiles("test")
@SpringBootTest
class ReportUtilsTests {

	@Test
	void shouldReadIndexAllLines() throws IOException {
		File indexFile = generateIndexTestFile();
		try {
			List<Map<String, String>> lineMaps = ReportUtils.readIndexAllLines(indexFile);
			assertFalse(lineMaps.isEmpty());
			assertTrue(lineMaps.size() == 10);
			assertEquals("field-three-1", lineMaps.get(0).get("HEADER3"));
			assertEquals("field-two-5", lineMaps.get(4).get("HEADER2"));
			assertEquals("field-one-10", lineMaps.get(9).get("HEADER1"));
		} finally {
			if (indexFile.exists())
				FileUtils.delete(indexFile);
		}
	}

	private File generateIndexTestFile() throws IOException {
		File indexFile = Paths.get("src", "test", "resources", "testIndex.csv").toFile();
		String headerString = "HEADER1;HEADER2;HEADER3";
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(indexFile));) {
			bw.write(headerString);
			bw.newLine();
			String field1Prefix = "field-one-";
			String field2Prefix = "field-two-";
			String field3Prefix = "field-three-";
			for (int i = 0; i < 10; i++) {
				StringJoiner joiner = new StringJoiner(ReportUtils.CSV_SEMICOLON_SEPARATOR);
				joiner.add(field1Prefix + (i + 1)).add(field2Prefix + (i + 1)).add(field3Prefix + (i + 1));
				bw.write(joiner.toString());
				bw.newLine();
			}
			bw.flush();
		}
		return indexFile;
	}
}
