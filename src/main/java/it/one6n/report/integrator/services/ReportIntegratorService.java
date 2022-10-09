package it.one6n.report.integrator.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
			List<Map<String, String>> linesMap = buildLineMaps(spoolDir);
			if (BooleanUtils.isTrue(reportConfiguration.getExportToCustomer()))
				createExportToCustomer(customer, reportConfiguration, spoolDir, processingDate, linesMap);
			if (BooleanUtils.isTrue(reportConfiguration.getExportToSpm()))
				createExportToSpm(customer, reportConfiguration, spoolDir, processingDate, linesMap);
			if (BooleanUtils.isTrue(reportConfiguration.getExportToDocumentRoom()))
				createExportToDocumentRoom(customer, reportConfiguration, spoolDir, processingDate, linesMap);
			throw new RuntimeException("Ciao");
		} finally {
			deleteSpoolDir(spoolDir);
		}
	}

	private void createExportToCustomer(String customer, ReportConfiguration reportConfiguration, File spoolDir,
			Date processDate, List<Map<String, String>> lineMaps) {
		log.info("exportToCustomer={}", customer);
	}

	private void createExportToSpm(String customer, ReportConfiguration reportConfiguration, File spoolDir,
			Date processDate, List<Map<String, String>> lineMaps) {
		log.info("exportToSpm for customer={}", customer);
	}

	private void createExportToDocumentRoom(String customer, ReportConfiguration reportConfiguration, File spoolDir,
			Date processDate, List<Map<String, String>> lineMaps) {
		log.info("exportToDocument for customer={}", customer);
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

	private List<Map<String, String>> buildLineMaps(File spoolDir) {
		File indexFile = foundIndexFile(spoolDir);
		List<Map<String, String>> lineMaps = readIndexAllLines(indexFile);
		if (lineMaps == null || lineMaps.isEmpty() || lineMaps.size() < 2)
			throw new ReportIndexException(String.format(
					"Error, the index file must contain at least the header and one line. Line(s) found(s)=%s",
					lineMaps.size()));
		return lineMaps;
	}

	private List<Map<String, String>> readIndexAllLines(File indexFile) {
		List<Map<String, String>> lineMaps = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(indexFile))) {
			String line = null;
			String[] headers = null;
			boolean firstLine = true;
			while ((line = br.readLine()) != null) {
				String[] splittedLine = line.split(ReportUtils.CSV_SEMICOLON_SEPARATOR);
				if (firstLine) {
					headers = splittedLine;
					firstLine = false;
				} else {
					Map<String, String> lineMap = new HashMap<>();
					for (int i = 0; i < headers.length; i++)
						lineMap.put(headers[i], splittedLine[i]);
					lineMaps.add(lineMap);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return lineMaps;
	}

	private File foundIndexFile(File spoolDir) {
		List<File> indexFiles = new ArrayList<>();
		try (Stream<Path> files = Files.list(spoolDir.toPath())) {
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
}
