package com.fabianonunes.reserva;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

public abstract class FileOutputter {

	File inputFile;

	File outputFile;

	protected OutputStream getOutputStream(String suffix) throws IOException {

		String fileName = inputFile.getAbsolutePath();

		File parent = inputFile.getCanonicalFile().getParentFile();

		String name = FilenameUtils.getBaseName(fileName);

		String extension = FilenameUtils.getExtension(fileName);

		outputFile = join(parent.getAbsolutePath(), name + suffix + "."
				+ extension);

		outputFile.getParentFile().mkdirs();

		return new FileOutputStream(outputFile);

	}

	private File join(String... paths) {

		return new File(StringUtils.join(paths, File.separator));

	}

	public File getOutputFile() {

		return outputFile;

	}

}
