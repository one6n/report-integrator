package it.one6n.report.integrator.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
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
public class DocumentRoomReportService {

	@Value("${documentRoom.output.dir}")
	private String documentRoomOutputDir;

	public void generateExportToDocumentRoom(String customer, ReportConfiguration reportConfiguration, File spoolDir,
			Date processDate, List<String> headers, List<Map<String, String>> lineMaps) {
		File workDir = ReportsUtils.makeExportWorkDir(spoolDir, this.getClass().getSimpleName(), false);
		try {
			List<String> documentRoomIndexFields = reportConfiguration.getFieldsToDocumentRoom();
			if (documentRoomIndexFields == null || documentRoomIndexFields.isEmpty())
				throw new ConfigurationException("fieldsToDocumentRoom must not be null for documentRoom export");
			if (ReportsUtils.isValidHeader(headers, documentRoomIndexFields)) {
				String documentRoomIndexFilename = String.join(".",
						new SimpleDateFormat(ReportsUtils.DEFAULT_DATE_FORMAT).format(processDate),
						ReportsUtils.IDX_EXTENSION);
				File documentRoomIndex = new File(workDir, documentRoomIndexFilename);
				String fieldsSeparator = reportConfiguration.getDocumentRoomFieldsSeparator() != null
						? reportConfiguration.getDocumentRoomFieldsSeparator()
						: ReportsUtils.CSV_SEMICOLON_SEPARATOR;
				ReportsUtils.writeIndexFile(documentRoomIndex, documentRoomIndexFields, lineMaps, fieldsSeparator,
						false);
				moveFileToOutputDir(customer, documentRoomIndex, spoolDir);
			} else
				throw new ConfigurationException(
						"Index doesn't contains all the required Field for exporToDocumentRoom. Expected=%s, found=%s"
								.formatted(documentRoomIndexFields, headers));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			FileUtils.deleteQuietly(workDir);
		}
	}

	private void moveFileToOutputDir(String customer, File spmIndex, File workDir) throws IOException {
		File indexOutputDir = new File(getDocumentRoomOutputDir(), Paths.get(customer, "index").toString());
		File pdfOutputDir = new File(getDocumentRoomOutputDir(), Paths.get(customer, "pdf").toString());
		FileUtils.moveFileToDirectory(spmIndex, indexOutputDir, true);
		ReportsUtils.copyPdFiles(workDir, pdfOutputDir);
	}
}
