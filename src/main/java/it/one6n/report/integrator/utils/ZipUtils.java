package it.one6n.report.integrator.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {

	private ZipUtils() {
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
