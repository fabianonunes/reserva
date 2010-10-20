/*
 * ThumbsApp.java
 */
package com.fabianonunes.solar.thumbs;

import ilist.ui.generic.grid.JCell;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.jai.Histogram;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.CropDescriptor;
import javax.media.jai.operator.RotateDescriptor;
import javax.media.jai.operator.ScaleDescriptor;
import javax.media.jai.operator.SubsampleBinaryToGrayDescriptor;
import javax.swing.JFileChooser;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FilenameUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;
import org.jdesktop.application.Task.BlockingScope;

import br.jus.tst.sesdi2.file.selector.gui.FileSelectorDialog;

import com.fabianonunes.cleaner.pdf.PageProcessor;
import com.fabianonunes.cleaner.pdf.PdfImageUtils;
import com.fabianonunes.cleaner.pdf.PdfPageIterator;
import com.fabianonunes.cleaner.pdf.cleaner.tools.PdfTools;
import com.fabianonunes.solar.thumbs.amostra.Absoluto;
import com.fabianonunes.solar.thumbs.amostra.Amostra;
import com.fabianonunes.solar.thumbs.amostra.ObjectFactory;
import com.fabianonunes.solar.thumbs.model.PageAttributes;
import com.fabianonunes.solar.thumbs.model.PageImage;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
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
	Map<Integer, PageAttributes> attributes;
	List<Amostra> amostras;
	private PdfReader reader;
	File file;

	@Override
	protected void startup() {
		setView(new ThumbsView(this));
		show(getView());

	}

	public static ThumbsApp getApplication() {
		return Application.getInstance(ThumbsApp.class);
	}

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
	@Action(block = BlockingScope.WINDOW)
	public Task<Object, PageImage> runThumbs() throws IOException,
			DocumentException {

		if (!runned) {
			file = null;
			runned = true;
			return new RunThumbsTask(org.jdesktop.application.Application
					.getInstance(com.fabianonunes.solar.thumbs.ThumbsApp.class));
		}

		amostras = new ArrayList<Amostra>();

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

			PageAttributes att = attributes.get(i);

			reader.getPageContent(i);

			Amostra amostra = new Amostra();

			if (att != null) {

				amostra.setBottom(BigDecimal.valueOf(att.getBottomKey()));
				amostra.setMiddle(BigDecimal.valueOf(att.getMiddleKey()));
				amostra.setTop(BigDecimal.valueOf(att.getTopKey()));
				amostra.setContent(att.getTextInPage());

			}

			if (pagesToKeep.contains(i)) {
				amostra.setRemoved(false);
				amostras.add(amostra);
				continue;
			}

			if (!lightPages.contains(i)) {
				pagesToKeep.add(i);
				amostra.setRemoved(false);
				amostras.add(amostra);
				continue;
			}

			pagesToRemove.add(i);
			amostra.setRemoved(true);
			amostras.add(amostra);

		}

		List<HashMap<String, String>> bookmarks = SimpleBookmark
				.getBookmark(reader);

		if (bookmarks != null) {

			for (HashMap<String, String> hashMap : bookmarks) {

				String pageAtt = hashMap.get("Page");
				if (pageAtt == null) {
					continue;
				}

				pageAtt = pageAtt.substring(0, pageAtt.indexOf(" ")).trim();

				Integer page = Integer.parseInt(pageAtt);
				if (!pagesToKeep.contains(page))
					pagesToKeep.add(page);

			}

		}

		Document document = new Document();

		PdfCopy copy = new PdfCopy(document, new FileOutputStream(new File(
				buildOutputFilename("[r]"))));

		document.open();

		Collections.sort(pagesToRemove);

		for (Integer pageRemoved : pagesToRemove) {

			if (pagesToKeep.contains(pageRemoved)) {
				continue;
			}
			PdfImportedPage p = copy.getImportedPage(reader, pageRemoved);
			try {
				copy.addPage(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		document.close();

		Collections.sort(pagesToKeep);

		stamp.getReader().selectPages(pagesToKeep);

		stamp.close();

		reader.close();

		try {
			createStats();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		exit();

		return null;
	}

	private void createStats() throws JAXBException, FileNotFoundException {

		File currentDir = new File(System.getProperty("user.dir"));
		String fileName = "stats.xml";
		File outFile = new File(currentDir, fileName);

		System.out.println(outFile);

		Absoluto authors;
		String pkgName = Absoluto.class.getPackage().getName();
		JAXBContext jc = JAXBContext.newInstance(pkgName);

		try {

			Unmarshaller u = jc.createUnmarshaller();
			authors = (Absoluto) u.unmarshal(outFile);

		} catch (Exception e) {

			ObjectFactory oFactory = new ObjectFactory();

			authors = oFactory.createAbsoluto();

		}

		List<Amostra> currentAmostras = authors.getAmostra();

		for (Amostra amostra : amostras) {

			currentAmostras.add(amostra);

		}

		Marshaller marshaller;

		marshaller = jc.createMarshaller();

		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(
				true));

		marshaller.marshal(authors, new FileOutputStream(outFile));

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

		RunThumbsTask(org.jdesktop.application.Application app)
				throws IOException {

			super(app);

			lightPages = new ArrayList<Integer>();

			attributes = new HashMap<Integer, PageAttributes>();

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

						int width = imageOfPage.getWidth();
						int height = imageOfPage.getHeight();

						RenderedImage rop = imageOfPage;

						Interpolation interpolation = Interpolation
								.getInstance(Interpolation.INTERP_BICUBIC_2);

						RenderingHints hints = new RenderingHints(
								JAI.KEY_INTERPOLATION, interpolation);

						float scale = (float) 350
								/ Math.max(imageOfPage.getHeight(), imageOfPage
										.getWidth());

						if (width > height) {

							float factor = 1.2f;

							RenderedOp irop = RotateDescriptor
									.create(
											rop,
											0f,
											0f,
											(float) Math.toRadians(90f),
											Interpolation
													.getInstance(Interpolation.INTERP_BICUBIC_2),
											null, null);
							irop = ScaleDescriptor.create(irop, scale * factor,
									scale * factor, 0f, 0f, null, null);
							irop = SubsampleBinaryToGrayDescriptor.create(irop
									.getAsBufferedImage(), 1 / factor,
									1 / factor, hints);
							rop = irop;
						} else {
							rop = SubsampleBinaryToGrayDescriptor.create(rop,
									scale, scale, hints);
						}

						Double key = getKey(0.10f, rop);
						Double middleKey = getMiddleKey(0.08f, rop);
						Double topKey = getTopKey(0.08f, rop);
						Double bottomKey = getBottomKey(0.08f, rop);

						PdfObject contents = PdfReader.getPdfObject(page
								.get(PdfName.CONTENTS));

						String textInPage;

						textInPage = PdfTools.getTextFromObject(contents);

						PageAttributes pa = new PageAttributes(pageNumber, key,
								bottomKey, topKey, middleKey, textInPage);

						attributes.put(pageNumber, pa);

						Boolean test = false;

						if (topKey <= 8d
								&& (key < 15d || middleKey < 30d || bottomKey < 30)) {
							test = true;
						}

						if (topKey <= 8d && middleKey < 8d) {
							test = true;
						}

						if (key <= 19.8d && topKey <= 17) {

							test = true;

						}

						if (key > 19.8d && key <= 21d) {

							if (topKey <= 18d) {
								test = true;
							}

						} else if (key < 26d) {

							if (bottomKey + topKey < 31d) {
								test = true;
							}

						}

						if (topKey + middleKey + bottomKey > 95) {

							test = false;

						}

						if (test) {

							PageImage pi = new PageImage();
							pi
									.setImage(((RenderedOp) rop)
											.getAsBufferedImage());
							pi.setPageNumber(pageNumber);
							pi.setStandardDeviation(key);

							publish(pi);

							lightPages.add(pageNumber);

						} else {

						}

						return null;

					}

					private Double getMiddleKey(float margin, RenderedImage rop) {

						float pageWidth = rop.getWidth();

						float pageHeight = rop.getHeight();

						margin = margin * Math.min(pageWidth, pageHeight);

						float x = margin * 2;
						float width = pageWidth - 2 * x;

						float height = pageHeight * 0.20f;
						float y = margin + height;

						RenderedOp topCropped = CropDescriptor.create(rop, x,
								y, width, height, null);

						Histogram topHistogram = (Histogram) JAI.create(
								"histogram", topCropped).getProperty(
								"histogram");

						return topHistogram.getStandardDeviation()[0];
					}

					private Double getKey(float margin, RenderedImage rop) {

						margin = margin
								* Math.min(rop.getWidth(), rop.getHeight());

						float x = margin * 2;
						float y = margin;
						float width = rop.getWidth() - margin * 4;
						float height = rop.getHeight() - margin * 2;

						RenderedOp croppped = CropDescriptor.create(rop, x, y,
								width, height, null);

						Histogram histogram = (Histogram) JAI.create(
								"histogram", croppped).getProperty("histogram");

						return histogram.getStandardDeviation()[0];

					}

					private Double getTopKey(float margin, RenderedImage rop) {

						float pageWidth = rop.getWidth();

						float pageHeight = rop.getHeight();

						margin = margin * Math.min(pageWidth, pageHeight);

						float x = margin * 2;
						float width = pageWidth - 2 * x;

						float y = margin;
						float height = pageHeight * 0.20f;

						RenderedOp topCropped = CropDescriptor.create(rop, x,
								y, width, height, null);

						Histogram topHistogram = (Histogram) JAI.create(
								"histogram", topCropped).getProperty(
								"histogram");

						return topHistogram.getStandardDeviation()[0];

					}

					private Double getBottomKey(Float margin, RenderedImage rop) {

						float pageWidth = rop.getWidth();
						float x = pageWidth * margin * 2;
						float width = pageWidth - 2 * x;

						float pageHeight = rop.getHeight();
						float y = (float) (2 * pageHeight / 3);
						float height = (pageHeight / 3) - pageHeight * margin;

						RenderedOp bottomCropped = CropDescriptor.create(rop,
								x, y, width, height, null);

						Histogram bottomHistogram = (Histogram) JAI.create(
								"histogram", bottomCropped).getProperty(
								"histogram");

						return bottomHistogram.getStandardDeviation()[0];

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
