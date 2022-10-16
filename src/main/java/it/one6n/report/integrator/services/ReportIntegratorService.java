package it.one6n.report.integrator.services;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.one6n.report.integrator.exceptions.ReportIndexException;
import it.one6n.report.integrator.models.ReportConfiguration;
import it.one6n.report.integrator.utils.ReportsUtils;
import it.one6n.report.integrator.utils.ZipUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Service
public class ReportIntegratorService {

	@Value("${report.spool.dir}")
	private String reportSpoolDir;

	private ReportConfigurationService reportConfigurationService;
	private SpmReportService spmReportService;
	private DocumentRoomReportService documentRoomReportService;

	@Autowired
	public ReportIntegratorService(ReportConfigurationService reportConfigurationService,
			SpmReportService spmReportService, DocumentRoomReportService documentRoomReportService) {
		this.reportConfigurationService = reportConfigurationService;
		this.spmReportService = spmReportService;
		this.documentRoomReportService = documentRoomReportService;
	}

	public void processReportFile(File file) {
		log.info("Start processing report={}", file.getName());
		String customer = ReportsUtils.getCustomerFromFilename(file.getName(), "_");
		ReportConfiguration reportConfiguration = getReportConfigurationService().findByCustomer(customer);
		if (log.isDebugEnabled())
			log.debug("reportConfiguration={}", reportConfiguration);
		Date processingDate = new Date();
		String filenameWithoutExtension = StringUtils.substringBeforeLast(file.getName(), ".");
		File spoolDir = makeSpoolDir(filenameWithoutExtension, processingDate);
		try {
			populateSpoolDir(file, spoolDir);
			List<String> headers = new ArrayList<>();
			List<Map<String, String>> linesMap = buildLineMaps(spoolDir, headers);
			if (BooleanUtils.isTrue(reportConfiguration.getExportToCustomer()))
				createExportToCustomer(customer, reportConfiguration, spoolDir, processingDate, headers, linesMap);
			if (BooleanUtils.isTrue(reportConfiguration.getExportToSpm()))
				createExportToSpm(customer, reportConfiguration, spoolDir, processingDate, headers, linesMap);
			if (BooleanUtils.isTrue(reportConfiguration.getExportToDocumentRoom()))
				createExportToDocumentRoom(customer, reportConfiguration, spoolDir, processingDate, headers, linesMap);
			log.info("End processing report={}", file.getName());
		} finally {
			deleteSpoolDir(spoolDir);
		}
	}

	private void createExportToCustomer(String customer, ReportConfiguration reportConfiguration, File spoolDir,
			Date processDate, List<String> headers, List<Map<String, String>> lineMaps) {
		log.info("exportToCustomer={}", customer);
	}

	private void createExportToSpm(String customer, ReportConfiguration reportConfiguration, File spoolDir,
			Date processDate, List<String> headers, List<Map<String, String>> lineMaps) {
		log.info("exportToSpm for customer={}", customer);
		getSpmReportService().generateExportToSpm(customer, reportConfiguration, spoolDir, processDate, headers,
				lineMaps);
	}

	private void createExportToDocumentRoom(String customer, ReportConfiguration reportConfiguration, File spoolDir,
			Date processDate, List<String> headers, List<Map<String, String>> lineMaps) {
		log.info("exportToDocument for customer={}", customer);
		getDocumentRoomReportService().generateExportToDocumentRoom(customer, reportConfiguration, spoolDir,
				processDate, headers, lineMaps);
	}

	private File makeSpoolDir(String filenameWithoutExtension, Date processingDate) {
		String processingDateString = new SimpleDateFormat(ReportsUtils.DEFAULT_DATE_FORMAT).format(processingDate);
		File reportSpoolDir = new File(getReportSpoolDir(),
				String.join("_", filenameWithoutExtension, processingDateString));
		if (!reportSpoolDir.exists())
			reportSpoolDir.mkdirs();
		return reportSpoolDir;
	}

	private void populateSpoolDir(File file, File reportSpoolDir) {
		ZipUtils.unzipFile(file, reportSpoolDir);
	}

	private void deleteSpoolDir(File spoolDir) {
		try {
			if (spoolDir.exists())
				FileUtils.deleteDirectory(spoolDir);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private List<Map<String, String>> buildLineMaps(File spoolDir, List<String> headers) {
		List<File> indexFiles = ReportsUtils.getIndexFiles(spoolDir);
		if (indexFiles.size() != 1)
			throw new ReportIndexException(
					"Invalid number of Index file. Found=%s, must be one".formatted(indexFiles.size()));
		File indexFile = indexFiles.get(0);
		List<Map<String, String>> lineMaps = ReportsUtils.readIndexAllLines(indexFile, headers);
		if (lineMaps == null || lineMaps.isEmpty() || lineMaps.size() < 1)
			throw new ReportIndexException(
					"Error, the index file must contain at least the header and one line. Line(s) found(s)=%s"
							.formatted(lineMaps.size()));
		return lineMaps;
	}
}
