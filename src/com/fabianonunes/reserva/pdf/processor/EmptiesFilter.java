package com.fabianonunes.reserva.pdf.processor;

import java.awt.image.BufferedImage;

import com.fabianonunes.reserva.ImageKeys;
import com.fabianonunes.reserva.pdf.PageProcessor;
import com.fabianonunes.reserva.pdf.PdfImageUtils;
import com.itextpdf.text.pdf.PdfDictionary;

public class EmptiesFilter implements PageProcessor<Integer> {

	private int counter = 0;

	@Override
	public Integer process(PdfDictionary page, Integer pageNumber)
			throws Throwable {

		counter++;

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

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public Boolean isStopped() {
		return false;
	}

	@Override
	public void setProgress(Float progress) {
		// TODO Auto-generated method stub

	}

}
