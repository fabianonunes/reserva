package com.fabianonunes.reserva;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

public abstract class FileOutputter {
	
	File inputFile;

	protected OutputStream getOutputStream(String suffix) throws IOException {

		File output = null;

		String fileName = inputFile.getAbsolutePath();

		File parent = inputFile.getCanonicalFile().getParentFile();

		String name = FilenameUtils.getBaseName(fileName);

		String extension = FilenameUtils.getExtension(fileName);

		output = join(parent.getAbsolutePath(), name + suffix + "." + extension);

		output.getParentFile().mkdirs();

		return new FileOutputStream(output);

	}

	private File join(String... paths) {

		return new File(StringUtils.join(paths, File.separator));

	}

}
