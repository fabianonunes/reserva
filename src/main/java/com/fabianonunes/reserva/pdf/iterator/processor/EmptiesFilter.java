package com.fabianonunes.reserva.pdf.iterator.processor;

import java.awt.image.BufferedImage;
import java.io.File;

import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;

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

		Double key = keys.getKey(0.10f);
		Double middleKey = keys.getMiddleKey(0.08f);
		Double topKey = keys.getTopKey(0.08f);
		Double bottomKey = keys.getBottomKey(0.08f);

		Boolean isLightPage = false;

		if (topKey <= 8d && (key < 15d || middleKey < 30d || bottomKey < 30)) {

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
