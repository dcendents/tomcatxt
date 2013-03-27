package org.uqbar.eclipse.tomcat.xt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Util class to manage .zip compressed files
 * 
 * @author ccancinos
 */
public class ZipUtils {
	static final int BUFFER = 2048;

	public static File unzip(String zipFileName, String outputFolder) {
		return ZipUtils.unzip(new File(zipFileName), new File(outputFolder));
	}
	
	public static File unzip(File inFile, File outFolder) {
		try {
			BufferedOutputStream out = null;
			ZipInputStream in = new ZipInputStream(new BufferedInputStream(
					new FileInputStream(inFile)));
			ZipEntry entry;
			while ((entry = in.getNextEntry()) != null) {
				int count;
				byte data[] = new byte[BUFFER];

				// write the files to the disk
				String outputFileName = outFolder.getPath() + "/" + entry.getName();
				File outputFile;
				outputFile = new File(outputFileName);
				if(entry.isDirectory()) {
					outputFile.mkdirs();
				} else {
					outputFile.getAbsoluteFile().getParentFile().getAbsoluteFile().mkdirs();
					outputFile.createNewFile();
				}
				out = new BufferedOutputStream(new FileOutputStream(outputFile), BUFFER);

				while ((count = in.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				cleanUp(out);
			}
			cleanUp(in);
			return outFolder;
		} catch (Exception e) {
			e.printStackTrace();
			return inFile;
		}
	}

	private static void cleanUp(InputStream in) throws Exception {
		in.close();
	}

	private static void cleanUp(OutputStream out) throws Exception {
		out.flush();
		out.close();
	}

}
