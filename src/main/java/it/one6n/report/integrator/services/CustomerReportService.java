package it.one6n.report.integrator.services;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.one6n.report.integrator.exceptions.ConfigurationException;
import it.one6n.report.integrator.models.ReportConfiguration;
import it.one6n.report.integrator.utils.ReportsUtils;
import it.one6n.report.integrator.utils.ZipUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Service
public class CustomerReportService {

	@Value("${customer.output.dir}")
	private String customerOutputDir;

	public void generateExportToCustomer(String customer, ReportConfiguration reportConfiguration, File spoolDir,
			Date processDate, List<String> headers, List<Map<String, String>> lineMaps) {
		boolean exportOnlyIndex = BooleanUtils.isTrue(reportConfiguration.getExportOnlyIndex());
		File workDir = ReportsUtils.makeExportWorkDir(spoolDir, this.getClass().getSimpleName(), !exportOnlyIndex);
		try {
			List<String> customerIndexFields = reportConfiguration.getFieldsToCustomer();
			if (customerIndexFields == null || customerIndexFields.isEmpty())
				throw new ConfigurationException("fieldsToCustomer must not be null for customer export");
			// add enrichment of lineMaps for idRdv and transcodification
			if (ReportsUtils.isValidHeader(headers, customerIndexFields)) {
				String customerIndexFilename = getCustomerIndexFilename(reportConfiguration.getCustomPrefix(),
						reportConfiguration.getCustomDateFormat(), reportConfiguration.getReportExtension(),
						processDate);
				File customerIndex = new File(workDir, customerIndexFilename);
				String fieldsSeparator = reportConfiguration.getReportFieldsSeparator() != null
						? reportConfiguration.getReportFieldsSeparator()
						: ReportsUtils.CSV_SEMICOLON_SEPARATOR;
				ReportsUtils.writeIndexFile(customerIndex, customerIndexFields, lineMaps, fieldsSeparator,
						BooleanUtils.isTrue(reportConfiguration.getAddHeader()));
				String zipFilename = null;
				if (!exportOnlyIndex)
					zipFilename = getZipFilename(customer, reportConfiguration.getCustomPrefix(),
							reportConfiguration.getCustomDateFormat(), processDate);
				moveFileToOutputDir(customer, customerIndex, workDir, exportOnlyIndex, zipFilename);
			} else
				throw new ConfigurationException(
						"Index doesn't contains all the required Field for exporToCustomer. Expected=%s, found=%s"
								.formatted(customerIndexFields, headers));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			FileUtils.deleteQuietly(workDir);
		}
	}

	private String getCustomerIndexFilename(String customPrefix, String customDateFormat, String fileExtension,
			Date processingDate) {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotBlank(customPrefix))
			sb.append(customPrefix).append("_");
		if (StringUtils.isNotBlank(customDateFormat))
			sb.append(new SimpleDateFormat(customDateFormat).format(processingDate));
		else
			sb.append(new SimpleDateFormat(ReportsUtils.DEFAULT_DATE_FORMAT).format(processingDate));
		if (StringUtils.isNotBlank(fileExtension))
			sb.append(".").append(fileExtension);
		else
			sb.append(".").append(ReportsUtils.CSV_EXTENSION);
		return sb.toString();
	}

	private String getZipFilename(String customer, String customPrefix, String customDateFormat, Date processingDate) {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotBlank(customPrefix))
			sb.append(customPrefix).append("_");
		else
			sb.append(customer).append("_");
		if (StringUtils.isNotBlank(customDateFormat))
			sb.append(new SimpleDateFormat(customDateFormat).format(processingDate));
		else
			sb.append(new SimpleDateFormat(ReportsUtils.DEFAULT_DATE_FORMAT).format(processingDate));

		sb.append(".").append(ReportsUtils.ZIP_EXTENSION);
		return sb.toString();
	}

	private void moveFileToOutputDir(String customer, File customerIndex, File workDir, boolean exportOnlyIndex,
			String zipFilename) throws IOException {
		File outputDir = new File(getCustomerOutputDir(), customer);
		if (exportOnlyIndex)
			FileUtils.moveFileToDirectory(customerIndex, outputDir, true);
		else {
			File zipFile = new File(workDir, zipFilename);
			ZipUtils.zipFiles(zipFile, workDir);
			FileUtils.moveFileToDirectory(zipFile, outputDir, true);
		}
	}
}
