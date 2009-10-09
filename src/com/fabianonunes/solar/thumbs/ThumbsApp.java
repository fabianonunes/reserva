/*
 * ThumbsApp.java
 */
package com.fabianonunes.solar.thumbs;

import ilist.ui.generic.grid.JCell;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.media.jai.Histogram;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.CropDescriptor;
import javax.media.jai.operator.SubsampleBinaryToGrayDescriptor;
import javax.swing.JFileChooser;

import org.apache.commons.io.FilenameUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;
import org.jdesktop.application.Task.BlockingScope;

import br.jus.tst.sesdi2.file.selector.gui.FileSelectorDialog;
import br.jus.tst.sesdi2.pdf.PageProcessor;
import br.jus.tst.sesdi2.pdf.PdfImageUtils;
import br.jus.tst.sesdi2.pdf.PdfPageIterator;

import com.fabianonunes.solar.thumbs.model.PageImage;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.SimpleBookmark;

/**
 * The main class of the application.
 */
public class ThumbsApp extends SingleFrameApplication {

	private ThumbsView view;
	private boolean runned = false;
	ArrayList<Integer> lightPages;
	private PdfReader reader;
	File file;

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

	@SuppressWarnings("unchecked")
	@Action(block = BlockingScope.APPLICATION)
	public Task<Object, PageImage> runThumbs() throws IOException,
			DocumentException {

		if (!runned) {
			file = null;
			runned = true;
			return new RunThumbsTask(org.jdesktop.application.Application
					.getInstance(com.fabianonunes.solar.thumbs.ThumbsApp.class));
		}

		ArrayList<String> pagesToKeepInString = getView().grid.getNames();
		ArrayList<Integer> pagesToKeep = new ArrayList<Integer>();

		for (String name : pagesToKeepInString) {
			pagesToKeep.add(Integer.parseInt(name));
		}

		PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(
				buildOutputFilename("[L]")));

		int numberOfPages = reader.getNumberOfPages();

		ArrayList<Integer> pagesToRemove = new ArrayList<Integer>();

		for (int i = 1; i <= numberOfPages; i++) {

			if (pagesToKeep.contains(i)) {
				continue;
			}

			if (!lightPages.contains(i)) {
				pagesToKeep.add(i);
				continue;
			}

			pagesToRemove.add(i);

		}

		Document document = new Document();

		PdfCopy copy = new PdfCopy(document, new FileOutputStream(new File(
				buildOutputFilename("[r]"))));

		document.open();

		for (Integer pageRemoved : pagesToRemove) {
			PdfImportedPage p = copy.getImportedPage(reader, pageRemoved);
			try {
				copy.addPage(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		document.close();

		List<HashMap<String, String>> bookmarks = SimpleBookmark
				.getBookmark(reader);

		for (HashMap<String, String> hashMap : bookmarks) {

			String pageAtt = hashMap.get("Page");
			if (pageAtt == null) {
				continue;
			}

			pageAtt = pageAtt.substring(0, pageAtt.indexOf(" ")).trim();

			Integer page = Integer.parseInt(pageAtt);
			if (!pagesToKeep.contains(page))
				pagesToKeep.add(page);
			System.out.println(page + ":" + hashMap.get("Page"));

		}

		Collections.sort(pagesToKeep);

		stamp.getReader().selectPages(pagesToKeep);

		stamp.close();

		reader.close();

		System.gc();

		return null;
	}

	private String buildOutputFilename(String suffix) {

		if (file != null) {

			String name = FilenameUtils.getBaseName(file.getAbsolutePath());
			String extension = FilenameUtils.getExtension(file
					.getAbsolutePath());

			String out = file.getParent() + File.separator + name + suffix
					+ "." + extension;

			System.out.println(out);

			return out;

		}

		return "";
	}

	public File selectFile() {

		FileSelectorDialog c = new FileSelectorDialog(getApplication()
				.getMainFrame(), true);

		JFileChooser fileChooser = c.getJFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);

		int rv = fileChooser.showOpenDialog(getApplication().getMainFrame());

		if (rv == JFileChooser.APPROVE_OPTION) {

			return fileChooser.getSelectedFile();

		}

		return null;

	}

	private class RunThumbsTask extends
			org.jdesktop.application.Task<Object, PageImage> {

		long start = System.currentTimeMillis();

		RunThumbsTask(org.jdesktop.application.Application app)
				throws IOException {

			super(app);

			lightPages = new ArrayList<Integer>();

			file = selectFile();

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

			if (file == null) {
				return null;
			}

			try {

				reader = new PdfReader(file.getAbsolutePath());

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

						float scale = (float) 350 / imageOfPage.getHeight();

						rop = SubsampleBinaryToGrayDescriptor.create(
								imageOfPage, scale, scale, hints);

						Float margin;

						margin = 0.10f * Math.min(rop.getWidth(), rop
								.getHeight());

						RenderedOp croppped = CropDescriptor.create(rop,
								margin * 2, margin,
								rop.getWidth() - margin * 4, rop.getHeight()
										- margin * 2, null);

						Histogram histogram = (Histogram) JAI.create(
								"histogram", croppped).getProperty("histogram");
						Double key = histogram.getStandardDeviation()[0];

						if (key <= 30d) {

							margin = 0.10f * Math.min(rop.getWidth(), rop
									.getHeight());

							RenderedOp topCropped = CropDescriptor.create(rop,
									margin, 0f, rop.getWidth() - margin * 2,
									rop.getHeight() * 0.20f, null);

							Histogram topHistogram = (Histogram) JAI.create(
									"histogram", topCropped).getProperty(
									"histogram");

							Double topKey = topHistogram.getStandardDeviation()[0];

							Double topLimit;

							if (key > 21) {
								topLimit = 5d;
							} else {
								topLimit = 9d;
							}
							
							//209-677

							if (topKey < topLimit) {

								PageImage pi = new PageImage();
								pi.setImage(((RenderedOp) rop)
										.getAsBufferedImage());
								pi.setPageNumber(pageNumber);
								pi.setStandardDeviation(key);

								System.out.println(pageNumber + "-" + key + "-"
										+ topKey);

								publish(pi);

								lightPages.add(pageNumber);

							}

						}

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
