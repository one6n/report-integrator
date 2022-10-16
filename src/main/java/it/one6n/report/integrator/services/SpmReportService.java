package it.one6n.report.integrator.services;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.one6n.report.integrator.exceptions.ConfigurationException;
import it.one6n.report.integrator.models.ReportConfiguration;
import it.one6n.report.integrator.utils.ReportsUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Service
public class SpmReportService {

	@Value("${spm.output.dir}")
	private String spmOutputDir;

	public void generateExportToSpm(String customer, ReportConfiguration reportConfiguration, File spoolDir,
			Date processDate, List<String> headers, List<Map<String, String>> lineMaps) {
		File workDir = ReportsUtils.makeExportWorkDir(spoolDir, this.getClass().getSimpleName(), false);
		try {
			List<String> spmIndexFields = reportConfiguration.getFieldsToSpm();
			if (spmIndexFields == null || spmIndexFields.isEmpty())
				throw new ConfigurationException("fieldsToSpm must not be null for spm export");
			if (ReportsUtils.isValidHeader(headers, spmIndexFields)) {
				String spmIndexFilename = String.join(".",
						new SimpleDateFormat(ReportsUtils.DEFAULT_DATE_FORMAT).format(processDate),
						ReportsUtils.CSV_EXTENSION);
				File spmIndex = new File(workDir, spmIndexFilename);
				ReportsUtils.writeIndexFile(spmIndex, spmIndexFields, lineMaps, ReportsUtils.CSV_SEMICOLON_SEPARATOR,
						true);
				moveFileToOutputDir(customer, spmIndex);
			} else
				throw new ConfigurationException(
						"Index doesn't contains all the required Field for exporToSpm. Expected=%s, found=%s"
								.formatted(spmIndexFields, headers));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			FileUtils.deleteQuietly(workDir);
		}
	}

	private void moveFileToOutputDir(String customer, File spmIndex) throws IOException {
		File indexOutputDir = new File(getSpmOutputDir(), customer);
		FileUtils.moveFileToDirectory(spmIndex, indexOutputDir, true);
	}
}
