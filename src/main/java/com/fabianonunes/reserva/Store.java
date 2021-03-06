package com.fabianonunes.reserva;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import com.fabianonunes.reserva.pdf.iterator.PageIterator;
import com.fabianonunes.reserva.pdf.iterator.processor.MuPdfFilter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.jmupdf.exceptions.DocException;
import com.jmupdf.exceptions.DocSecurityException;

public class Store extends FileOutputter {

	private Collection<Integer> reservePages;
	private PdfReader reader;

	public Store(File file) throws IOException {

		this.inputFile = file;

		reader = new PdfReader(inputFile.getAbsolutePath());

	}

	public void split() throws DocException,
			DocSecurityException, IOException, DocumentException {

		reservePages = analyzePages();

		TreeSet<Integer> pagesToKeep = new TreeSet<Integer>();

		int numberOfPages = reader.getNumberOfPages();

		for (Integer pageNumber = 1; pageNumber <= numberOfPages; pageNumber++) {

			if (!reservePages.contains(pageNumber)) {

				pagesToKeep.add(pageNumber);

				continue;

			}

			OutputStream os = getOutputStream(File.separator + pageNumber);

			extractPage(pageNumber, os);

		}

		saveRemainder(pagesToKeep);

	}

	private void extractPage(int pageNumber, OutputStream os)
			throws IOException, DocumentException {

		Document document = new Document();

		PdfCopy copy = new PdfCopy(document, os);

		document.open();

		PdfImportedPage p = copy.getImportedPage(reader, pageNumber);

		copy.addPage(p);

		document.close();

	}

	private void saveRemainder(Collection<Integer> pagesToKeep)
			throws IOException, DocumentException {

		reader.selectPages(new ArrayList<Integer>(pagesToKeep));

		OutputStream outputStream = getOutputStream("[L]");

		PdfStamper stamp = new PdfStamper(reader, outputStream);

		stamp.close();

		reader.close();

	}

	protected Collection<Integer> analyzePages() throws 
			DocException, DocSecurityException {

		PageIterator<Integer> iterator = new PageIterator<Integer>();

		return iterator.iterate(new MuPdfFilter(inputFile));

	}

}
