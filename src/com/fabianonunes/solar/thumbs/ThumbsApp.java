/*
 * ThumbsApp.java
 */
package com.fabianonunes.solar.thumbs;

import ilist.ui.generic.grid.JCell;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.jai.Histogram;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.SubsampleBinaryToGrayDescriptor;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;
import org.jdesktop.swingx.JXImagePanel;
import org.jpedal.PdfDecoder;
import org.jpedal.ThumbnailDecoder;
import org.w3c.dom.Document;

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
	public Task<Object, BufferedImage> runThumbs() {
		return new RunThumbsTask(org.jdesktop.application.Application
				.getInstance(com.fabianonunes.solar.thumbs.ThumbsApp.class));
	}

	private class RunThumbsTask extends
			org.jdesktop.application.Task<Object, BufferedImage> {

		PdfDecoder decoder = new PdfDecoder();
		ThumbnailDecoder tdecoder;
		InputStream is;

		long start = System.currentTimeMillis();

		RunThumbsTask(org.jdesktop.application.Application app) {

			super(app);

			// try {
			// File file = new File("/home/fabiano/teste.pdf");
			// is = file.toURI().toURL().openStream();
			// } catch (FileNotFoundException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (MalformedURLException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			is = ThumbsApp.class
					.getResourceAsStream("resources/10-2009-000-00-00.1 v1-6.pdf");
			// .getResourceAsStream("resources/10-2009-000-00-00.1(v)[m].pdf");

			// f = new File("/home/fabiano/workdir/teste[pronto].pdf");
			// //
			// try {
			// decoder.openPdfFile(f.getAbsolutePath());
			// } catch (PdfException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// tdecoder = new ThumbnailDecoder(decoder);

		}

		@Override
		protected void process(List<BufferedImage> values) {
			super.process(values);

			for (BufferedImage image : values) {

				Histogram histogram = (Histogram) JAI
						.create("histogram", image).getProperty("histogram");

				JXImagePanel panel = new JXImagePanel();
				panel.setSize(image.getWidth(), image.getHeight());
				panel.setFocusable(true);
				panel.requestFocusInWindow();
				panel.setOpaque(false);
				panel.setImage(image);

				JCell<Double> cell = getView().grid.getCell(panel,
						getView().grid.cells.size() + 1);// .addElement(image);
				Double key = histogram.getStandardDeviation()[0];//
				cell.setComparable(key);

				// getView().unsortedModel.addElement(image);

				getView().grid.addCell(cell);

			}
		}

		protected Object doInBackground() {

			try {

				PdfPageIterator<Object> iterator = new PdfPageIterator<Object>(
						new PdfReader(is));

				PageProcessor<Object> processor = new PageProcessor<Object>() {

					private int counter = 0;

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

						// Histogram histogram = (Histogram) JAI.create(
						// "histogram", rop).getProperty("histogram");
						// System.out.println(Arrays.toString(histogram
						// .getStandardDeviation()));
						// System.out.println("-----");

						publish(((RenderedOp) rop).getAsBufferedImage());

						return null;
					}

					@Override
					public Boolean stop() {
						return false;
					}

					@Override
					public void setProgress(Float progress) {
						RunThumbsTask.this.setProgress(progress / 100);
					}

				};

				iterator.iterate(processor);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return decoder;

		}

		@SuppressWarnings("unused")
		public Object doInBackgroundJPedal() {
			try {
				PdfReader reader = new PdfReader(is);
				byte[] info = reader.getMetadata();
				DocumentBuilderFactory fact = DocumentBuilderFactory
						.newInstance();
				fact.setNamespaceAware(true);
				DocumentBuilder db = fact.newDocumentBuilder();
				ByteArrayInputStream bais = new ByteArrayInputStream(info);
				Document doc = db.parse(bais);

				String value = doc.getElementsByTagName("StandartDeviation")
						.item(0).getTextContent();
				String[] values = StringUtils.split(value, ",");

				SortedMap<Double, Integer> map = new TreeMap<Double, Integer>();

				int counter = 1;

				for (String ivalue : values) {
					map.put(Double.parseDouble(ivalue), counter++);
				}
				Set<Double> keys = map.keySet();

				int counter2 = 1;

				for (Double key : keys) {
					publish(tdecoder.getPageAsThumbnail(map.get(key), 200));

					counter2++;

					if (counter2 > 100) {
						break;
					}
				}

			} catch (Exception ex) {
				Logger.getLogger(ThumbsApp.class.getName()).log(Level.SEVERE,
						null, ex);
			}
			return null; // return your result
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
