package com.fabianonunes.reserva.pdf.iterator.processor;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.jpedal.exception.PdfException;

import com.fabianonunes.reserva.image.ImageKeys;

public class EmptiesFilter extends CLIPageProcessor<Integer> {

	@Override
	public Integer process(Integer pageNumber) throws Throwable {

		BufferedImage imageOfPage = getImage(pageNumber);

		ImageIO.write(imageOfPage, "PNG", new File("/home/fabiano/"
				+ pageNumber + ".png"));

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

		return iterator.getDecoder().getPageAsImage(pageNumber);

	}

}
