package com.fabianonunes.reserva.pdf.iterator.processor;

import java.awt.image.BufferedImage;
import java.io.File;

import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;

import com.fabianonunes.reserva.classification.ImageClassifier;
import com.fabianonunes.reserva.image.ImageKeys;

public class EmptiesFilter extends CLIPageProcessor<Integer> {

	private PdfDecoder decoder;

	private Integer numberOfPages;

	public EmptiesFilter(File file) throws PdfException {

		decoder = new PdfDecoder(true);

		PdfDecoder.setFontReplacements(decoder);

		decoder.setExtractionMode(PdfDecoder.FINALIMAGES
				+ PdfDecoder.CLIPPEDIMAGES, 300, 1);

		decoder.openPdfFile(file.getAbsolutePath());

		numberOfPages = decoder.getPageCount();

	}

	@Override
	public Integer process(Integer pageNumber) throws Throwable {

		super.process(pageNumber);

		BufferedImage imageOfPage = getImage(pageNumber);

		ImageKeys keys = ImageKeys.normalize(imageOfPage);

		Double middle = keys.getMiddle(0.08f);
		Double top = keys.getTop(0.08f);
		Double bottom = keys.getBottom(0.08f);

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
	 */
	synchronized private BufferedImage getImage(Integer pageNumber)
			throws PdfException {

		return decoder.getPageAsImage(pageNumber);

	}

	@Override
	public int getNumberOfPages() {

		return numberOfPages;

	}

}
