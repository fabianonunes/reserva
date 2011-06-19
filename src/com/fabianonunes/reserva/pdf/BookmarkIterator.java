package com.fabianonunes.reserva.pdf

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.SimpleBookmark;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class BookmarkIterator {

	private PdfReader reader;
	private List bookmarks;

	public BookmarkIterator(PdfReader reader) {
		this.reader = reader;
		this.bookmarks = SimpleBookmark.getBookmark(this.reader);
	}

	public List<Map<String, String>> dumpBookmarks() {

		if (bookmarks != null)
			return plainBookmarks(bookmarks);

		return new ArrayList<Map<String, String>>();

	}

	private List<Map<String, String>> plainBookmarks(List<Map<String, ?>> c) {

		List<Map<String, String>> retval = new ArrayList<Map<String, String>>();

		for (Map<String, ?> map : c) {
			if (map.containsKey("Kids")) {
				List<?> kids = (List<?>) map.get("Kids");
				plainBookmarks((List<Map<String, ?>>) kids);
				Map<String, ?> clone = new HashMap<String, Object>(map);
				clone.remove("Kids");
				retval.add((Map<String, String>) clone);
				retval.addAll(plainBookmarks((List<Map<String, ?>>) kids));
			} else {
				retval.add((Map<String, String>) map);
			}
		}
		return retval;
	}

	public static void main(String[] args) throws IOException,
			URISyntaxException {

		File dir = new File(
				"/media/My Passport/Digitalização/Pauta 2010-04/4-Indexado");

		File[] files = dir.listFiles();

		filesloop:

		for (File file : files) {

			System.out.println(file);

			if (!FilenameUtils.getExtension(file.getAbsolutePath())
					.toLowerCase().equals("pdf")) {
				continue;
			}

			PDDocument pddoc = null;

			try {

				pddoc = PDDocument.load(file.toURI().toURL(), true);
				PdfReader reader = new PdfReader(file.getAbsolutePath());
				BookmarkIterator it = new BookmarkIterator(reader);
				List<Map<String, String>> list = it.dumpBookmarks();

				String iq = "all";

				if (iq.equals("all")) {

					int count = pddoc.getNumberOfPages();
					for (int i = 1; i <= count; i++) {

						BufferedWriter writer = new BufferedWriter(
								new FileWriter(new File(
										"/home/fabiano/Desktop/training/training/decla/"
												+ i + " - "
												+ +System.currentTimeMillis()
												+ ".txt")));

						PDFTextStripper stripper = new PDFTextStripper();
						stripper.setStartPage(i);
						stripper.setEndPage(i + 1);
						stripper.writeText(pddoc, writer);

						writer.close();

					}

					break filesloop;

				} else if (iq.equals("bkonly")) {

					for (Map<String, String> map : list) {

						String title = map.get("Title").toLowerCase();

						if (map.get("Page") == null)
							continue;

						final String page = StringUtils.split(map.get("Page"),
								" ")[0];

						if (title.contains("embargo")) {

							System.out.println(title + " - " + page);

							// PdfDictionary pdfpage = reader.getPageN(Integer
							// .parseInt(page));
							//
							// PdfObject contents =
							// PdfReader.getPdfObject(pdfpage
							// .get(PdfName.CONTENTS));
							//
							// String textInPage;

							BufferedWriter writer = new BufferedWriter(
									new FileWriter(
											new File(
													"/home/fabiano/Desktop/training/training/decla/"
															+ System.currentTimeMillis()
															+ "-" + page
															+ ".txt")));

							//
							// textInPage =
							// PdfTools.getTextFromObject(contents);

							PDFTextStripper stripper = new PDFTextStripper();
							stripper.setStartPage(Integer.parseInt(page));
							stripper.setEndPage(Integer.parseInt(page) + 1);
							stripper.writeText(pddoc, writer);

							// writer.write(Normalizer.normalize(textInPage,
							// Normalizer.Form.NFD));

							writer.close();

						}

					}
				}

			} catch (Exception e) {

				e.printStackTrace();

			} finally {
				if (pddoc != null) {
					pddoc.close();
				}

			}
		}
	}
}
