package it.one6n.report.integrator.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

	public static List<Map<String, String>> readIndexAllLines(File indexFile) {
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

	public static List<File> getIndexFiles(File spoolDir) {
		List<File> indexFiles = new ArrayList<>();
		try (Stream<Path> files = Files.list(spoolDir.toPath())) {
			files.filter(Files::isRegularFile).filter(ReportUtils::isValidIndexFormat).forEach(file -> {
				indexFiles.add(file.toFile());
			});
			return indexFiles;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
