package it.one6n.report.integrator.utils;

import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;

public class ReportUtils {

	public static final String ZIP_EXTENSION = "zip";
	public static final String CSV_EXTENSION = "csv";

	public static final String CSV_SEMICOLON_SEPARATOR = ";";

	public static final String DEFAULT_DATE_FORMAT = "yyyyMMddhhssSSS";

	private ReportUtils() {
	}

	public static String getCustomerFromFilename(String filename, String delimiter) {
		if (filename.contains(delimiter))
			return StringUtils.substringBefore(filename, delimiter);
		else
			return StringUtils.EMPTY;
	}

	public static boolean isValidReportFormat(Path file) {
		return isValidReportFormat(file.toString());
	}

	public static boolean isValidReportFormat(String filename) {
		return StringUtils.endsWithIgnoreCase(StringUtils.substringAfterLast(filename, "."), ReportUtils.ZIP_EXTENSION);
	}

	public static boolean isValidIndexFormat(Path file) {
		return isValidIndexFormat(file.toString());
	}

	public static boolean isValidIndexFormat(String filename) {
		return StringUtils.endsWithIgnoreCase(StringUtils.substringAfterLast(filename, "."), ReportUtils.CSV_EXTENSION);
	}
}
