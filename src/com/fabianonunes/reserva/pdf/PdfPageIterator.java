package com.fabianonunes.reserva.pdf;


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

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfReader;

public class PdfPageIterator<T> {

	PdfReader reader;

	private Integer totalOfPages;

	private Integer currentPage;

	public PdfPageIterator(File file) throws IOException {
		setReader(new PdfReader(file.getAbsolutePath()));
		setTotalOfPages(getReader().getNumberOfPages());
	}

	public PdfPageIterator(PdfReader reader) {
		setReader(reader);
		setTotalOfPages(getReader().getNumberOfPages());
	}

	public void setReader(PdfReader reader) {
		this.reader = reader;
	}

	public PdfReader getReader() {
		return this.reader;
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
	public Collection<T> iterate(final PageProcessor<T> processor) {

		Vector<T> retVal = new Vector<T>();

		ExecutorService executor = Executors.newFixedThreadPool(3);

		LinkedList<Future<T>> tasks = new LinkedList<Future<T>>();

		for (int i = 1; i <= getTotalOfPages(); ++i) {

			final int counter = i;

			Future<T> future = executor.submit(new Callable<T>() {

				@Override
				public T call() throws Exception {

					setCurrentPage(counter);

					PdfDictionary page = reader.getPageN(counter);

					T iretVal = null;

					try {

						iretVal = processor.process(page, counter);

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Float progress = (float) (++counter * 100 / tasks.size());

			processor.setProgress(progress);

		}

		executor.shutdown();

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
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

}
