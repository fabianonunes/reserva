/*
 * ThumbsApp.java
 */
package com.fabianonunes.reserva;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.fabianonunes.reserva.model.PageAttributes;
import com.fabianonunes.reserva.pdf.PageProcessor;
import com.fabianonunes.reserva.pdf.PdfImageUtils;
import com.fabianonunes.reserva.pdf.PdfPageIterator;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class ThumbsApp {

	Collection<Integer> lightPages;
	Map<Integer, PageAttributes> attributes;
	private PdfReader reader;
	File file;

	public static void main(String[] args) {
	}

	public void run() throws FileNotFoundException, DocumentException,
			IOException {

		doInBackground();

		ArrayList<Integer> pagesToKeep = new ArrayList<Integer>();

		ArrayList<Integer> pagesToRemove = new ArrayList<Integer>();

		int numberOfPages = reader.getNumberOfPages();

		for (int i = 1; i <= numberOfPages; i++) {

			reader.getPageContent(i);

			pagesToRemove.add(i);

		}

		Document document = new Document();

		PdfCopy copy = new PdfCopy(document, new FileOutputStream(new File(
				buildOutputFilename("[r]"))));

		document.open();

		Collections.sort(pagesToRemove);

		for (Integer pageRemoved : pagesToRemove) {

			if (pagesToKeep.contains(pageRemoved)) {
				continue;
			}
			PdfImportedPage p = copy.getImportedPage(reader, pageRemoved);
			try {
				copy.addPage(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		document.close();

		Collections.sort(pagesToKeep);

		reader.selectPages(pagesToKeep);

		PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(
				buildOutputFilename("[L]")));

		stamp.close();

		reader.close();

	}

	protected Object doInBackground() throws IOException {

		lightPages = Collections
				.synchronizedCollection(new ArrayList<Integer>());

		if (file == null) {
			return null;
		}

		reader = new PdfReader(file.getAbsolutePath());

		PdfPageIterator<Object> iterator = new PdfPageIterator<Object>(reader);

		PageProcessor<Object> processor = new PageProcessor<Object>() {

			private int counter = 0;

			@Override
			public Object process(PdfDictionary page, Integer pageNumber)
					throws Throwable {

				counter++;

				BufferedImage imageOfPage = PdfImageUtils
						.extractImageFromPage(page);

				ImageKeys keys = ImageKeys.normalize(imageOfPage);

				Double key = keys.getKey(0.10f);
				Double middleKey = keys.getMiddleKey(0.08f);
				Double topKey = keys.getTopKey(0.08f);
				Double bottomKey = keys.getBottomKey(0.08f);

				Boolean isLightPage = false;

				if (topKey <= 8d
						&& (key < 15d || middleKey < 30d || bottomKey < 30)) {
					isLightPage = true;
				}

				if (topKey <= 8d && middleKey < 8d) {
					isLightPage = true;
				}

				if (key <= 19.8d && topKey <= 17) {

					isLightPage = true;

				}

				if (key > 19.8d && key <= 21d) {

					if (topKey <= 18d) {
						isLightPage = true;
					}

				} else if (key < 26d) {

					if (bottomKey + topKey < 31d) {
						isLightPage = true;
					}

				}

				if (topKey + middleKey + bottomKey > 95) {

					isLightPage = false;

				}

				if (isLightPage) {

					lightPages.add(pageNumber);

				}

				return null;

			}

			@Override
			public void stop() {
				// TODO Auto-generated method stub

			}

			@Override
			public Boolean isStopped() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setProgress(Float progress) {
				// TODO Auto-generated method stub

			}

		};

		iterator.iterate(processor);

		return null;

	}

	private String buildOutputFilename(String suffix) {

		if (file != null) {

			String name = FilenameUtils.getBaseName(file.getAbsolutePath());
			String extension = FilenameUtils.getExtension(file
					.getAbsolutePath());

			String out = file.getParent() + File.separator + name + suffix
					+ "." + extension;

			System.out.println(out);

			return out;

		}

		return "";
	}

}
