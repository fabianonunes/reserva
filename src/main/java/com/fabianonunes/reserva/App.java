package com.fabianonunes.reserva;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.jpedal.exception.PdfException;

import com.itextpdf.text.DocumentException;
import com.jmupdf.exceptions.DocException;
import com.jmupdf.exceptions.DocSecurityException;

public class App {

	private static GnuParser parser;

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception {

		Option oMethod = OptionBuilder.withArgName("arquivo")
				.withDescription("reserva as páginas em branco do arquivo")
				.hasArgs(1).create("store");

		Option oRestore = OptionBuilder.withArgName("arquivo [diretorio]")
				.hasArgs(2).withDescription("restaura as paginas reservadas")
				.create("restore");

		Options options = new Options();
		options.addOption(oMethod);
		options.addOption(oRestore);

		parser = new GnuParser();

		try {

			CommandLine line = parser.parse(options, args, true);

			if (line.getOptions().length == 0) {
				printHelp(options);
			}

			String toStore = line.getOptionValue("store");
			String[] toRestore = line.getOptionValues("restore");

			if (toStore != null) {

				store(toStore);

			} else if (toRestore != null && toRestore.length >= 1) {

				restore(toRestore);

			}

		} catch (ParseException e) {

			printHelp(options);

			e.printStackTrace();

		}

	}

	private static void restore(String[] args) throws IOException,
			DocumentException {

		File inputFile = new File(args[0]);

		String filename = inputFile.getAbsolutePath();

		String basename = FilenameUtils.getBaseName(filename);

		File reservedDir = null;

		if (args.length == 2) {

			reservedDir = new File(args[1]);

		} else if (args.length == 1) {

			reservedDir = new File(inputFile.getParentFile(), basename);

		}

		if (reservedDir.isFile()) {

			throw new IOException("target <" + reservedDir + "> exists");

		}

		Restore r = new Restore(inputFile, reservedDir);

		r.join();

	}

	private static void store(String fileToStore) throws PdfException,
			DocException, DocSecurityException, IOException, DocumentException {

		File inputFile = new File(fileToStore);

		Store s = new Store(inputFile);

		s.split();

	}

	private static void printHelp(Options options) {

		System.out
				.println("Version: Reserva 0.1 - 2011-06-22 [ https://github.com/fabianonunes/reserva ]");

		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("reserva", options);

	}
}
