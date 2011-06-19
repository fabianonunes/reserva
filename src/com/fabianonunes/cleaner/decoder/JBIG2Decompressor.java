package com.fabianonunes.cleaner.decoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class JBIG2Decompressor {
	
	public static File getDecompressor() throws FileNotFoundException, IOException  {

		File jd = File.createTempFile("jdx", "tmp");
		jd.setExecutable(true);
		jd.deleteOnExit();

		String exec;

		if (System.getProperty("os.name").equals("Linux")) {
			exec = "resources/jbig2dec-0.1";
		} else {
			exec = "resources/jdw";
		}
		
		InputStream is = JBIG2Decompressor.class.getResourceAsStream(exec);

		FileOutputStream fos = new FileOutputStream(jd);

		byte[] buf = new byte[1024];
		int len;
		while ((len = is.read(buf)) > 0) {
			fos.write(buf, 0, len);
		}
		is.close();
		fos.close();

		return jd;

	}
	

}
