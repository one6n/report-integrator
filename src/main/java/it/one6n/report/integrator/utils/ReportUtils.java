package it.one6n.report.integrator.utils;

import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;

public class ReportUtils {

	public static final String ZIP_EXTENSION = "zip";

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
}
