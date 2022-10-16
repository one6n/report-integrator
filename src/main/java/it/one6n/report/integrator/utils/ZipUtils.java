package it.one6n.report.integrator.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

public class ZipUtils {

	private ZipUtils() {
	}

	public static void zipFiles(File zipFile, File sourceFilesDir) {
		Path sourcePath = sourceFilesDir.toPath();
		try (FileOutputStream fos = new FileOutputStream(zipFile);
				ZipOutputStream zos = new ZipOutputStream(fos);
				Stream<Path> files = Files.list(sourcePath)) {
			files.filter(Files::isRegularFile).filter(file -> !ReportsUtils.isValidReportFormat(file)).forEach(file -> {
				File fileToZip = new File(file.toString());
				try (FileInputStream fis = new FileInputStream(fileToZip)) {
					ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
					zos.putNextEntry(zipEntry);

					byte[] bytes = new byte[1024];
					int length;
					while ((length = fis.read(bytes)) >= 0)
						zos.write(bytes, 0, length);
					fis.close();
					FileUtils.delete(fileToZip);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void unzipFile(File zipFile, File destDir) {
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
			byte[] buffer = new byte[1024];
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				File newFile = new File(destDir, zipEntry.getName());
				try (FileOutputStream fos = new FileOutputStream(newFile)) {
					int len;
					while ((len = zis.read(buffer)) > 0)
						fos.write(buffer, 0, len);
				}
				zipEntry = zis.getNextEntry();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
