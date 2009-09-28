/*
 * ThumbsApp.java
 */
package com.fabianonunes.solar.thumbs;

import ilist.ui.generic.grid.JCell;

import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
import org.jdesktop.swingx.JXImagePanel;

import br.jus.tst.sesdi2.pdf.PageProcessor;
import br.jus.tst.sesdi2.pdf.PdfImageUtils;
import br.jus.tst.sesdi2.pdf.PdfPageIterator;

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
	public Task<Object, BufferedImage> runThumbs() throws FileNotFoundException {
		if(!runned ){
			runned = true;
			return new RunThumbsTask(org.jdesktop.application.Application
					.getInstance(com.fabianonunes.solar.thumbs.ThumbsApp.class));
		}
		
		int count = getView().grid.contentPanel.getComponentCount();
		getView().grid.contentPanel.removeAll();
		getView().grid.contentPanel.revalidate();
		getView().grid.cells.clear();
		
		
		return null;
	}

	private class RunThumbsTask extends
			org.jdesktop.application.Task<Object, BufferedImage> {

		InputStream is;

		long start = System.currentTimeMillis();

		RunThumbsTask(org.jdesktop.application.Application app)
				throws FileNotFoundException {

			super(app);

			is = new FileInputStream(new File("/home/fabiano/teste.pdf"));

		}

		@Override
		protected void process(List<BufferedImage> values) {
			super.process(values);

			for (BufferedImage image : values) {

				Histogram histogram = (Histogram) JAI
						.create("histogram", image).getProperty("histogram");

				JXImagePanel panel = new JXImagePanel();
				panel.setSize(image.getWidth(), image.getHeight());
				panel.setImage((Image) image);

				JCell<Double> cell = getView().grid.getCell(panel,
						getView().grid.cells.size() + 1);
				Double key = histogram.getStandardDeviation()[0];//
				cell.setComparable(key);

				getView().grid.addCell(cell);

			}
		}

		protected Object doInBackground() {

			try {

				PdfPageIterator<Object> iterator = new PdfPageIterator<Object>(
						new PdfReader(is));

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

						float scale = (float) 250 / imageOfPage.getHeight();

						rop = SubsampleBinaryToGrayDescriptor.create(
								imageOfPage, scale, scale, hints);

						publish(((RenderedOp) rop).getAsBufferedImage());

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
		}

		@Override
		protected void succeeded(Object result) {
			// Runs on the EDT. Update the GUI based on
			// the result computed by doInBackground().
		}

	}
}
