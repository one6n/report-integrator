package it.one6n.report.integrator.services;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
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

	public void generateExportToSpm(String customer, ReportConfiguration reportConfiguration, File spoolDir,
			Date processDate, List<String> headers, List<Map<String, String>> lineMaps) {
		File workDir = ReportsUtils.makeExportWorkDir(spoolDir, this.getClass().getSimpleName(), false);
		try {
			List<String> spmIndexFields = reportConfiguration.getFieldsToSpm();
			if (spmIndexFields == null || spmIndexFields.isEmpty())
				throw new ConfigurationException("fieldsToSpm must not be null for spm export");
			if (ReportsUtils.isValidHeader(headers, spmIndexFields)) {

			} else
				throw new ConfigurationException(
						"Index dosn't contains all the required Field for exporToSpm. Expected=%s, found=%s"
								.formatted(spmIndexFields, headers));
		} finally {
			FileUtils.deleteQuietly(workDir);
		}
	}
}
