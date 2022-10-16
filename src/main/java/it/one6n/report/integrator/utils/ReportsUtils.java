package it.one6n.report.integrator.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class ReportsUtils {

	public static final String ZIP_EXTENSION = "zip";
	public static final String CSV_EXTENSION = "csv";
	public static final String PDF_EXTENSION = "pdf";
	public static final String IDX_EXTENSION = "idx";

	public static final String CSV_SEMICOLON_SEPARATOR = ";";

	public static final String DEFAULT_DATE_FORMAT = "yyyyMMddhhssSSS";

	private ReportsUtils() {
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
		return StringUtils.endsWithIgnoreCase(StringUtils.substringAfterLast(filename, "."),
				ReportsUtils.ZIP_EXTENSION);
	}

	public static boolean isValidIndexFormat(Path file) {
		return isValidIndexFormat(file.toString());
	}

	public static boolean isValidIndexFormat(String filename) {
		return StringUtils.endsWithIgnoreCase(StringUtils.substringAfterLast(filename, "."),
				ReportsUtils.CSV_EXTENSION);
	}

	public static boolean isValidPdfFormat(Path file) {
		return isValidPdfFormat(file.toString());
	}

	public static boolean isValidPdfFormat(String filename) {
		return StringUtils.endsWithIgnoreCase(StringUtils.substringAfterLast(filename, "."),
				ReportsUtils.PDF_EXTENSION);
	}

	public static boolean isValidHeader(List<String> headerToValidate, List<String> header) {
		return headerToValidate != null && !headerToValidate.isEmpty() ? headerToValidate.containsAll(header) : false;
	}

	public static List<Map<String, String>> readIndexAllLines(File indexFile, List<String> headers) {
		List<Map<String, String>> lineMaps = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(indexFile))) {
			String line = null;
			boolean firstLine = true;
			while ((line = br.readLine()) != null) {
				String[] splittedLine = line.split(ReportsUtils.CSV_SEMICOLON_SEPARATOR);
				if (firstLine) {
					headers.addAll(Arrays.asList(splittedLine));
					firstLine = false;
				} else {
					Map<String, String> lineMap = new HashMap<>();
					for (int i = 0; i < headers.size(); i++)
						lineMap.put(headers.get(i), splittedLine[i]);
					lineMaps.add(lineMap);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return lineMaps;
	}

	public static void writeIndexFile(File indexFile, List<String> indexFields, List<Map<String, String>> lineMaps,
			String fieldsSeparator, boolean addHeader) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(indexFile))) {
			if (addHeader) {
				bw.write(String.join(ReportsUtils.CSV_SEMICOLON_SEPARATOR, indexFields));
				bw.newLine();
			}
			for (Map<String, String> lineMap : lineMaps) {
				StringBuilder sb = new StringBuilder();
				int i = 0;
				for (String field : indexFields) {
					sb.append(lineMap.get(field));
					i++;
					if (i < indexFields.size())
						sb.append(fieldsSeparator);
				}
				bw.write(sb.toString());
				bw.newLine();
			}
			bw.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<File> getIndexFiles(File spoolDir) {
		List<File> indexFiles = new ArrayList<>();
		try (Stream<Path> files = Files.list(spoolDir.toPath())) {
			files.filter(Files::isRegularFile).filter(ReportsUtils::isValidIndexFormat).forEach(file -> {
				indexFiles.add(file.toFile());
			});
			return indexFiles;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void copyPdFiles(File sourceDir, File destDir) {
		try (Stream<Path> files = Files.list(sourceDir.toPath())) {
			files.filter(Files::isRegularFile).filter(ReportsUtils::isValidPdfFormat).forEach(file -> {
				try {
					FileUtils.copyToDirectory(file.toFile(), destDir);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void movePdFiles(File sourceDir, File destDir) {
		try (Stream<Path> files = Files.list(sourceDir.toPath())) {
			files.filter(Files::isRegularFile).filter(ReportsUtils::isValidPdfFormat).forEach(file -> {
				try {
					FileUtils.moveFileToDirectory(file.toFile(), destDir, true);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static File makeExportWorkDir(File spoolDir, String exportName, boolean copyFiles) {
		File workDir = new File(spoolDir, exportName);
		if (!workDir.exists())
			workDir.mkdirs();
		if (copyFiles)
			ReportsUtils.copyPdFiles(spoolDir, workDir);
		return workDir;
	}
}
