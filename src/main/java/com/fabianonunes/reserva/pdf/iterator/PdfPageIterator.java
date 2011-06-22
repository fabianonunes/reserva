package com.fabianonunes.reserva.pdf.iterator;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;

import com.fabianonunes.reserva.pdf.iterator.processor.PageProcessor;

public class PdfPageIterator<T> {

	private PdfDecoder decoder;

	private Integer totalOfPages;

	private Integer currentPage;

	private PageProcessor<T> processor;

	public PdfPageIterator(File file) throws IOException, PdfException {

		decoder = new PdfDecoder(true);

		PdfDecoder.setFontReplacements(decoder);

		decoder.setExtractionMode(PdfDecoder.FINALIMAGES
				+ PdfDecoder.CLIPPEDIMAGES, 300, 1);

		decoder.openPdfFile(file.getAbsolutePath());

		setTotalOfPages(decoder.getPageCount());

		System.out.println("pages: " + decoder.getNumberOfPages());

	}

	public void setDecoder(PdfDecoder decoder) {
		this.decoder = decoder;
	}

	public PdfDecoder getDecoder() {
		return decoder;
	}

	/**
	 * Iterates over all PDF pages and execute
	 * <code>PageProcessor.process</code> from processor
	 * 
	 * @param processor
	 *            the processor
	 * @return a collection of results
	 * @author Fabiano Nunes
	 */
	public Collection<T> iterate(final PageProcessor<T> pageProcessor) {

		processor = pageProcessor;

		processor.setIterator(this);

		Vector<T> retVal = new Vector<T>();

		ExecutorService executor = Executors.newFixedThreadPool(4);

		LinkedList<Future<T>> tasks = new LinkedList<Future<T>>();

		for (int i = 1; i <= getTotalOfPages(); ++i) {

			final int counter = i;

			Future<T> future = executor.submit(new Callable<T>() {

				@Override
				public T call() throws Exception {

					T iretVal = null;

					try {

						iretVal = processor.process(counter);

					} catch (Throwable e) {
						e.printStackTrace();
					}

					return iretVal;
				}

			});

			if (processor.isStopped()) {
				executor.shutdownNow();
				// TODO: ao parar a aplicação, também tenho que cuidar da
				// finalização das threads
				break;
			}

			tasks.add(future);

		}

		int counter = 0;

		for (Future<T> future : tasks) {

			try {

				retVal.add(future.get());

				if (processor.isStopped()) {
					break;
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

			setCurrentPage(++counter);

		}

		executor.shutdown();

		System.out.println("");

		return retVal;

	}

	public void setTotalOfPages(Integer totalOfPages) {
		this.totalOfPages = totalOfPages;
	}

	public Integer getTotalOfPages() {
		return totalOfPages;
	}

	public void setCurrentPage(Integer currentPage) {

		this.currentPage = currentPage;

		Float progress = (float) (currentPage * 100 / getTotalOfPages());

		processor.setProgress(progress);

	}

	public Integer getCurrentPage() {
		return currentPage;
	}

}
