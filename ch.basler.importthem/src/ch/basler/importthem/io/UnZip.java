package ch.basler.importthem.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZip {

	public static void unzip(InputStream in, File dest) throws IOException {
		try {
			ZipInputStream zin = new ZipInputStream(in);
			ZipEntry ze = null;
			byte[] buffer = new byte[8 * 1024];
			while ((ze = zin.getNextEntry()) != null) {
				if (!ze.isDirectory()) {
					String name = ze.getName();
					File outputFile = new File(dest, name);
					File outputDir = outputFile.getParentFile();
					if (!(outputDir.exists() && outputDir.isDirectory())
							&& !outputDir.mkdirs())
						throw new IOException("Can not create directories for "
								+ outputDir.getAbsolutePath());
					outputFile.createNewFile();
					FileOutputStream fout = new FileOutputStream(outputFile);
					int len = 0;
					while ((len = zin.read(buffer)) > 0) {
						fout.write(buffer, 0, len);
					}
					fout.close();
				}
				zin.closeEntry();
			}
			zin.close();
		} catch (IOException e) {
			throw new IOException("Can not unzip to " + dest.getAbsolutePath());
		}
	}
}