package com.fabianonunes.reserva.pdf.iterator.processor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.jpedal.exception.PdfException;

import com.fabianonunes.reserva.classification.ImageClassifier;
import com.fabianonunes.reserva.image.ImageKeys;
import com.jmupdf.Page;
import com.jmupdf.PageRenderer;
import com.jmupdf.exceptions.DocException;
import com.jmupdf.exceptions.DocSecurityException;
import com.jmupdf.pdf.PdfDocument;

public class MuPdfFilter extends CLIPageProcessor<Integer> {

	private Integer numberOfPages;

	PdfDocument doc;

	private String fileName;

	public MuPdfFilter(File file) throws PdfException, DocException,
			DocSecurityException {

		fileName = file.getAbsolutePath();

		doc = new PdfDocument(file.getAbsolutePath());

		numberOfPages = doc.getPageCount();

		doc.dispose();

	}

	@Override
	public Integer process(Integer pageNumber) throws Throwable {

		super.process(pageNumber);

		BufferedImage imageOfPage = getImage(pageNumber);

		ImageKeys keys = ImageKeys.normalize(imageOfPage);

		// Double key = keys.getKey(0.10f);
		Double middle = keys.getMiddle(0.08f);
		Double top = keys.getTop(0.08f);
		Double bottom = keys.getBottom(0.08f);

		// Boolean isLightPage = false;
		// if (top <= 8d && (key < 15d || middle < 30d || bottom < 30)) {
		// isLightPage = true;
		// }
		// if (top <= 8d && middle < 8d) {
		// isLightPage = true;
		// }
		// if (key <= 19.8d && top <= 17) {
		// isLightPage = true;
		// }
		// if (key > 19.8d && key <= 21d) {
		// if (top <= 18d) {
		// isLightPage = true;
		// }
		// } else if (key < 26d) {
		// if (bottom + top < 31d) {
		// isLightPage = true;
		// }
		// }
		// if (top + middle + bottom > 95) {
		// isLightPage = false;
		// }

		double dcl = ImageClassifier.check(top, middle, bottom);

		if (dcl == 1.0) {

			return pageNumber;

		}

		return null;

	}

	/**
	 * Renderiza a página como uma imagem usando o decoder do JPedal. Como o
	 * JPedal não é thread-safe, essa thread precisou ficar synchronized
	 * 
	 * @param pageNumber
	 *            Número da página (começa em 1)
	 * @return A imagem bruta do rendering
	 * @throws PdfException
	 * @throws DocSecurityException
	 * @throws DocException
	 * @throws IOException
	 */
	private BufferedImage getImage(Integer pageNumber) throws PdfException,
			DocException, DocSecurityException, IOException {

		PdfDocument doc = new PdfDocument(fileName);

		Page page = doc.getPage(pageNumber);

		PageRenderer render = new PageRenderer(page, 1f, Page.PAGE_ROTATE_AUTO,
				PageRenderer.IMAGE_TYPE_GRAY);
		render.render(true);

		doc.dispose();

		BufferedImage bImage = render.getImage();

		return bImage;

	}

	@Override
	public int getNumberOfPages() {

		return numberOfPages;

	}

}
