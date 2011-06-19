package com.fabianonunes.reserva;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class Merger {

	public void join(File outputFile) throws IOException, DocumentException {

		outputFile = new File("/home/fabiano/L.pdf");

		PdfReader reader = new PdfReader(outputFile.getAbsolutePath());

		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(
				new File("/home/fabiano/output.pdf")));

		File dir = new File("/home/fabiano/10-2009-000-00-00.1");

		File[] files = dir.listFiles();

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
