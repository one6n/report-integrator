package it.one6n.report.integrator.utils;

import java.nio.file.Path;

import org.springframework.util.StringUtils;

public class ReportUtils {

	public static final String ZIP_EXTENSION = ".zip";

	private ReportUtils() {
	}

	public static boolean isValidReportFormat(Path file) {
		return isValidReportFormat(file.toString());
	}

	public static boolean isValidReportFormat(String filename) {
		return StringUtils.endsWithIgnoreCase(filename, ZIP_EXTENSION);
	}
}
