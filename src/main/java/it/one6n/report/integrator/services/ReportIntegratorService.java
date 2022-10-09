package it.one6n.report.integrator.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.one6n.report.integrator.exceptions.ReportIndexException;
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
	@Value("${spm.output.dir}")
	private String spmOutputDir;
	@Value("${documentRoom.output.dir}")
	private String documentRoomOutputDir;

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
			List<Map<String, String>> linesMap = buildLinesMap(spoolDir);
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

	private List<Map<String, String>> buildLinesMap(File spoolDir) {
		List<Map<String, String>> linesMap = new ArrayList<>();
		Path spoolDirPath = spoolDir.toPath();
		File indexFile = foundIndexFile(spoolDirPath);
		return linesMap;
	}

	private File foundIndexFile(Path spoolDirPath) {
		List<File> indexFiles = new ArrayList<>();
		try (Stream<Path> files = Files.list(spoolDirPath)) {
			files.filter(Files::isRegularFile).filter(ReportUtils::isValidIndexFormat).forEach(file -> {
				indexFiles.add(file.toFile());
			});
			if (indexFiles.size() != 1)
				throw new ReportIndexException(String
						.format("Invalid number of Index file. Found=%s, only one is requerid", indexFiles.size()));
			return indexFiles.get(0);
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
