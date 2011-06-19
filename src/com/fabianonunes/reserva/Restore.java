package com.fabianonunes.reserva;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class Restore extends FileOutputter {

	private File reservedDir;

	public Restore(File inputFile, File reservedDir) throws IOException {

		this.inputFile = inputFile.getCanonicalFile();

		this.reservedDir = reservedDir.getCanonicalFile();

	}

	public void join() throws IOException, DocumentException {

		PdfReader reader = new PdfReader(inputFile.getAbsolutePath());

		OutputStream os = getOutputStream("[R]");

		PdfStamper stamper = new PdfStamper(reader, os);

		File[] files = reservedDir.listFiles();

		if (files.length == 0) {

			return;

		}

		TreeMap<Integer, File> map = new TreeMap<Integer, File>();

		for (File file : files) {

			Pattern p = Pattern.compile("[0-9]+");

			Matcher m = p.matcher(file.getName());

			m.find();

			Integer pageNumber = Integer.parseInt(m.group());

			map.put(pageNumber, file);

		}

		for (Integer pageNumber : map.keySet()) {

			File file = map.get(pageNumber);

			PdfReader pageReader = new PdfReader(file.getAbsolutePath());

			PdfImportedPage page = stamper.getImportedPage(pageReader, 1);

			stamper.insertPage(pageNumber, pageReader.getPageSize(1));

			stamper.getUnderContent(pageNumber).addTemplate(page, 0, 0);

		}

		stamper.close();

	}

}
