package com.fabianonunes.reserva.pdf.processor;

import java.awt.image.BufferedImage;

import com.fabianonunes.reserva.image.ImageKeys;
import com.fabianonunes.reserva.pdf.PdfImageUtils;
import com.itextpdf.text.pdf.PdfDictionary;

public class EmptiesFilter extends CLIPageProcessor<Integer> {

	@Override
	public Integer process(PdfDictionary page, Integer pageNumber)
			throws Throwable {
		
		BufferedImage imageOfPage = PdfImageUtils.extractImageFromPage(page);

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


}
