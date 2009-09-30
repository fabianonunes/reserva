/*
 * ThumbsApp.java
 */
package com.fabianonunes.solar.thumbs;

import ilist.ui.generic.grid.JCell;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.media.jai.Histogram;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.SubsampleBinaryToGrayDescriptor;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

import br.jus.tst.sesdi2.pdf.PageProcessor;
import br.jus.tst.sesdi2.pdf.PdfImageUtils;
import br.jus.tst.sesdi2.pdf.PdfPageIterator;

import com.fabianonunes.solar.thumbs.model.PageImage;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfReader;

/**
 * The main class of the application.
 */
public class ThumbsApp extends SingleFrameApplication {

	private ThumbsView view;
	private boolean runned = false;
	/**
	 * At startup create and show the main frame of the application.
	 */
	@Override
	protected void startup() {
		setView(new ThumbsView(this));
		show(getView());

	}

	/**
	 * This method is to initialize the specified window by injecting resources.
	 * Windows shown in our application come fully initialized from the GUI
	 * builder, so this additional configuration is not needed.
	 */
	@Override
	protected void configureWindow(java.awt.Window root) {
	}

	/**
	 * A convenient static getter for the application instance.
	 * 
	 * @return the instance of ThumbsApp
	 */
	public static ThumbsApp getApplication() {
		return Application.getInstance(ThumbsApp.class);
	}

	/**
	 * Main method launching the application.
	 */
	public static void main(String[] args) {
		launch(ThumbsApp.class, args);
	}

	private void setView(ThumbsView thumbsView) {
		this.view = thumbsView;
	}

	private ThumbsView getView() {
		return this.view;
	}

	@Action
	public Task<Object, PageImage> runThumbs() throws IOException {
		if (!runned) {
			runned = true;
			return new RunThumbsTask(org.jdesktop.application.Application
					.getInstance(com.fabianonunes.solar.thumbs.ThumbsApp.class));
		}

		return null;
	}

	private class RunThumbsTask extends
			org.jdesktop.application.Task<Object, PageImage> {

		File f;
		//138305

		long start = System.currentTimeMillis();

		private PdfReader reader;

		RunThumbsTask(org.jdesktop.application.Application app)
				throws IOException {

			super(app);

			f = new File("/home/fabiano/205480-2009-000-00-00.2.pdf");

		}

		@Override
		protected void process(List<PageImage> values) {
			super.process(values);

			for (PageImage pageImage : values) {

				JCell<Double> cell = getView().grid.getCell(pageImage,
						getView().grid.cells.size() + 1);
				cell.setComparable(pageImage.getStandardDeviation());

				getView().grid.addCell(cell);

			}
		}

		protected Object doInBackground() {

			try {

				reader = new PdfReader(f.getAbsolutePath());

				PdfPageIterator<Object> iterator = new PdfPageIterator<Object>(
						reader);

				PageProcessor<Object> processor = new PageProcessor<Object>() {

					private int counter = 0;
					private boolean stop = false;

					@Override
					public Object process(PdfDictionary page, Integer pageNumber)
							throws Throwable {

						counter++;

						BufferedImage imageOfPage = PdfImageUtils
								.extractImageFromPage(page);

						RenderedImage rop = imageOfPage;

						Interpolation interpolation = Interpolation
								.getInstance(Interpolation.INTERP_BICUBIC_2);

						RenderingHints hints = new RenderingHints(
								JAI.KEY_INTERPOLATION, interpolation);

						float scale = (float) 300 / imageOfPage.getHeight();

						rop = SubsampleBinaryToGrayDescriptor.create(
								imageOfPage, scale, scale, hints);

						Histogram histogram = (Histogram) JAI.create(
								"histogram", rop).getProperty("histogram");
						Double key = histogram.getStandardDeviation()[0];

						if (key <= 22.46d) {

							PageImage pi = new PageImage();
							pi
									.setImage(((RenderedOp) rop)
											.getAsBufferedImage());
							pi.setPageNumber(pageNumber);
							pi.setStandardDeviation(key);

							publish(pi);
						}

						// 23.459921323655763

						return null;
					}

					@Override
					public void stop() {
						stop = true;
					}

					@Override
					public void setProgress(Float progress) {
						RunThumbsTask.this.setProgress(progress / 100);
					}

					@Override
					public Boolean isStopped() {
						return stop;
					}

				};

				iterator.iterate(processor);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;

		}

		@Override
		protected void finished() {
			super.finished();
			System.out.println(System.currentTimeMillis() - start);
			reader.close();
			reader = null;
			f = null;
			System.gc();
		}

		@Override
		protected void succeeded(Object result) {

			int childrenLenght = getView().grid.contentPanel
					.getComponentCount();

			for (int i = 0; i < childrenLenght; i++) {

				JCell<?> cell = (JCell<?>) getView().grid.contentPanel
						.getComponent(i);
				cell.setIndex(i);

			}

		}

	}
}
