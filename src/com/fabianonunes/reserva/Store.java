package com.fabianonunes.reserva;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import com.fabianonunes.reserva.pdf.PdfPageIterator;
import com.fabianonunes.reserva.pdf.processor.EmptiesFilter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class Store extends FileOutputter {

	private Collection<Integer> reservePages;
	private PdfReader reader;

	public Store(File file) throws IOException {

		this.inputFile = file;

		reader = new PdfReader(inputFile.getAbsolutePath());

	}

	public void split() throws IOException, DocumentException {

		reservePages = analyzePages();

		TreeSet<Integer> pagesToKeep = new TreeSet<Integer>();

		TreeSet<Integer> pagesToReserve = new TreeSet<Integer>();

		int numberOfPages = reader.getNumberOfPages();

		for (int i = 1; i <= numberOfPages; i++) {

			if (reservePages.contains(i)) {

				pagesToReserve.add(i);

			} else {

				pagesToKeep.add(i);

			}

		}

		for (Integer pageReserved : pagesToReserve) {

			if (pagesToKeep.contains(pageReserved)) {
				continue;
			}

			OutputStream outputStream = getOutputStream(File.separator
					+ pageReserved.toString());

			Document document = new Document();

			PdfCopy copy = new PdfCopy(document, outputStream);

			document.open();

			PdfImportedPage p = copy.getImportedPage(reader, pageReserved);

			try {

				copy.addPage(p);

			} catch (Exception e) {

				e.printStackTrace();

			} finally {

				document.close();

			}

		}

		reader.selectPages(new ArrayList<Integer>(pagesToKeep));

		OutputStream outputStream = getOutputStream("[L]");

		PdfStamper stamp = new PdfStamper(reader, outputStream);

		stamp.close();

		reader.close();

	}

	protected Collection<Integer> analyzePages() {

		PdfPageIterator<Integer> iterator = new PdfPageIterator<Integer>(reader);

		return iterator.iterate(new EmptiesFilter());

	}

}
