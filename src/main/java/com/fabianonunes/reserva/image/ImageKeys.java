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

@SuppressWarnings("restriction")
public class ImageKeys {

	public RenderedImage rop;

	public ImageKeys(RenderedImage rop) {

		this.rop = rop;

	}

	public Double getMiddle(float margin) {

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

	public Double getTop(float margin) {

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

	public Double getBottom(Float margin) {

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

		if (width > height) {

			RenderedOp irop = RotateDescriptor.create(imageOfPage, 0f, 0f,
					(float) Math.toRadians(90f),
					Interpolation.getInstance(Interpolation.INTERP_BICUBIC_2),
					null, null);

			imageOfPage = irop.getAsBufferedImage();

			width = imageOfPage.getWidth();
			height = imageOfPage.getHeight();

		}

		// float scale = (float) 350
		// / Math.max(imageOfPage.getHeight(), imageOfPage.getWidth());
		//
		// float factor = 1f;
		// RenderedOp r = ScaleDescriptor.create(imageOfPage, scale * factor,
		// scale * factor, 0f, 0f, null, null);
		//
		// width = r.getWidth();
		// height = r.getHeight();
		//
		// r = MedianFilterDescriptor.create(r,
		// MedianFilterDescriptor.MEDIAN_MASK_SQUARE, 3, null);
		//
		// imageOfPage = r.getAsBufferedImage();

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_GRAY);
		Graphics g = image.getGraphics();
		g.drawImage(imageOfPage, 0, 0, null);
		g.dispose();

		return new ImageKeys(image);

	}

}
