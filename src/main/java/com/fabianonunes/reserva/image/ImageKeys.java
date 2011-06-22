package com.fabianonunes.reserva.image;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;

import javax.media.jai.Histogram;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.CropDescriptor;
import javax.media.jai.operator.RotateDescriptor;
import javax.media.jai.operator.ScaleDescriptor;

@SuppressWarnings("restriction")
public class ImageKeys {

	public RenderedImage rop;

	public ImageKeys(RenderedImage rop) {

		this.rop = rop;

	}

	public Double getMiddleKey(float margin) {

		float pageWidth = rop.getWidth();

		float pageHeight = rop.getHeight();

		margin = margin * Math.min(pageWidth, pageHeight);

		float x = margin * 2;
		float width = pageWidth - 2 * x;

		float height = pageHeight * 0.20f;
		float y = margin + height;

		RenderedOp topCropped = CropDescriptor.create(rop, x, y, width, height,
				null);

		Histogram topHistogram = (Histogram) JAI
				.create("histogram", topCropped).getProperty("histogram");

		return topHistogram.getStandardDeviation()[0];
	}

	public Double getKey(float margin) {

		margin = margin * Math.min(rop.getWidth(), rop.getHeight());

		float x = margin * 2;
		float y = margin;
		float width = rop.getWidth() - margin * 4;
		float height = rop.getHeight() - margin * 2;

		RenderedOp croppped = CropDescriptor.create(rop, x, y, width, height,
				null);

		Histogram histogram = (Histogram) JAI.create("histogram", croppped)
				.getProperty("histogram");

		return histogram.getStandardDeviation()[0];

	}

	public Double getTopKey(float margin) {

		float pageWidth = rop.getWidth();

		float pageHeight = rop.getHeight();

		margin = margin * Math.min(pageWidth, pageHeight);

		float x = margin * 2;
		float width = pageWidth - 2 * x;

		float y = margin;
		float height = pageHeight * 0.20f;

		RenderedOp topCropped = CropDescriptor.create(rop, x, y, width, height,
				null);

		Histogram topHistogram = (Histogram) JAI
				.create("histogram", topCropped).getProperty("histogram");

		return topHistogram.getStandardDeviation()[0];

	}

	public Double getBottomKey(Float margin) {

		float pageWidth = rop.getWidth();
		float x = pageWidth * margin * 2;
		float width = pageWidth - 2 * x;

		float pageHeight = rop.getHeight();
		float y = (float) (2 * pageHeight / 3);
		float height = (pageHeight / 3) - pageHeight * margin;

		RenderedOp bottomCropped = CropDescriptor.create(rop, x, y, width,
				height, null);

		Histogram bottomHistogram = (Histogram) JAI.create("histogram",
				bottomCropped).getProperty("histogram");

		return bottomHistogram.getStandardDeviation()[0];

	}

	public static ImageKeys normalize(BufferedImage imageOfPage)
			throws IOException {

		int width = imageOfPage.getWidth();
		int height = imageOfPage.getHeight();

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_GRAY);
		Graphics g = image.getGraphics();
		g.drawImage(imageOfPage, 0, 0, null);
		g.dispose();

		float scale = (float) 800 / Math.max(height, width);

		RenderedImage irop = image;

		if (width > height) {

			float factor = 1.2f;

			irop = RotateDescriptor.create(image, 0f, 0f,
					(float) Math.toRadians(90f),
					Interpolation.getInstance(Interpolation.INTERP_BICUBIC_2),
					null, null);

			irop = ScaleDescriptor.create(irop, scale * factor, scale * factor,
					0f, 0f, null, null);

		}

		return new ImageKeys(irop);

	}
}
