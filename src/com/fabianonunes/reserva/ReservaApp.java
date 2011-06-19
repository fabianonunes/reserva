/*
 * ThumbsApp.java
 */
package com.fabianonunes.reserva;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.fabianonunes.reserva.pdf.PdfPageIterator;
import com.fabianonunes.reserva.pdf.processor.EmptiesFilter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class ReservaApp {

	Collection<Integer> lightPages;
	private PdfReader reader;
	File file;

	public ReservaApp(File file) {
		this.file = file;
	}

	public static void main(String[] args) throws FileNotFoundException,
			DocumentException, IOException {

		ReservaApp r = new ReservaApp(new File(args[0]));

		r.run();

	}

	public void run() throws FileNotFoundException, DocumentException,
			IOException {

		lightPages = analyzePages();

		ArrayList<Integer> pagesToKeep = new ArrayList<Integer>();

		ArrayList<Integer> pagesToRemove = new ArrayList<Integer>();

		int numberOfPages = reader.getNumberOfPages();

		for (int i = 1; i <= numberOfPages; i++) {

			if (lightPages.contains(i)) {

				pagesToRemove.add(i);

			} else {

				pagesToKeep.add(i);

			}

		}

		Collections.sort(pagesToRemove);

		for (Integer pageRemoved : pagesToRemove) {

			Document document = new Document();

			PdfCopy copy = new PdfCopy(document, new FileOutputStream(
					buildOutputFilename("[" + pageRemoved + "]")));

			document.open();

			if (pagesToKeep.contains(pageRemoved)) {
				continue;
			}

			PdfImportedPage p = copy.getImportedPage(reader, pageRemoved);

			try {

				copy.addPage(p);

			} catch (Exception e) {

				e.printStackTrace();

			} finally {

				document.close();

			}

		}

		Collections.sort(pagesToKeep);

		reader.selectPages(pagesToKeep);

		PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(
				buildOutputFilename("[L]")));

		stamp.close();

		reader.close();

	}

	protected Collection<Integer> analyzePages() throws IOException {

		if (file == null) {
			return null;
		}

		reader = new PdfReader(file.getAbsolutePath());

		PdfPageIterator<Integer> iterator = new PdfPageIterator<Integer>(reader);

		return iterator.iterate(new EmptiesFilter());

	}

	private File buildOutputFilename(String suffix) throws IOException {

		File output = null;

		if (file != null) {

			String name = FilenameUtils.getBaseName(file.getAbsolutePath());

			String extension = FilenameUtils.getExtension(file
					.getAbsolutePath());

			String out = file.getParent() + File.separator + name
					+ File.separator + suffix + "." + extension;

			output = new File(out);

			FileUtils.forceMkdir(output.getParentFile());

		}

		return output;

	}

}
