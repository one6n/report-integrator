package it.one6n.report.integrator.services;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.one6n.report.integrator.models.ReportConfiguration;
import it.one6n.report.integrator.repos.ReportConfigurationRepo;
import it.one6n.report.integrator.utils.ReportUtils;
import it.one6n.report.integrator.utils.ZipUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Service
public class ReportIntegratorService {

	@Value("${report.spool.dir}")
	private String reportSpoolDir;

	private ReportConfigurationRepo reportConfigurationRepo;

	@Autowired
	public ReportIntegratorService(ReportConfigurationRepo reportConfigurationRepo) {
		this.reportConfigurationRepo = reportConfigurationRepo;
	}

	public void processReportFile(File file) {
		log.info("Start processing report={}", file.getName());
		String customer = ReportUtils.getCustomerFromFilename(file.getName(), "_");
		ReportConfiguration reportConfiguration = getReportConfigurationRepo().findOneByCustomer(customer).orElseThrow(
				() -> new NoSuchElementException(String.format("No configuration found for customer=%s", customer)));
		if (log.isDebugEnabled())
			log.debug("reportConfiguration={}", reportConfiguration);
		Date processingDate = new Date();
		String filenameWithoutExtension = StringUtils.substringBeforeLast(file.getName(), ".");
		File spoolDir = makeSpoolDir(filenameWithoutExtension, processingDate);
		try {
			populateSpoolDir(file, spoolDir);
			// search a single csv for validate the report file
			// read the csv and build the list of map of lines
			if (BooleanUtils.isTrue(reportConfiguration.getExportToCustomer()))
				createExportToCustomer(reportConfiguration, spoolDir, customer, processingDate);
			if (BooleanUtils.isTrue(reportConfiguration.getExportToSpm()))
				createExportToSpm(reportConfiguration, spoolDir, customer, processingDate);
			if (BooleanUtils.isTrue(reportConfiguration.getExportToDocumentRoom()))
				createExportToDocumentRoom(reportConfiguration, spoolDir, customer, processingDate);
			throw new RuntimeException("Ciao");
		} finally {
			deleteSpoolDir(spoolDir);
		}
	}

	private File makeSpoolDir(String filenameWithoutExtension, Date processingDate) {
		String processingDateString = new SimpleDateFormat(ReportUtils.DEFAULT_DATE_FORMAT).format(processingDate);
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

	private void createExportToCustomer(ReportConfiguration reportConfiguration, File spoolDir, String customer,
			Date processDate) {
		log.info("exportToCustomer={}", customer);
	}

	private void createExportToSpm(ReportConfiguration reportConfiguration, File spoolDir, String customer,
			Date processDate) {
		log.info("exportToSpm for customer={}", customer);
	}

	private void createExportToDocumentRoom(ReportConfiguration reportConfiguration, File spoolDir, String customer,
			Date processDate) {
		log.info("exportToDocument for customer={}", customer);
	}
}
