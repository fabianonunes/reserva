package com.fabianonunes.reserva.pdf.iterator;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.fabianonunes.reserva.pdf.iterator.processor.PageProcessor;

public class PageIterator<T> {

	private PageProcessor<T> processor;

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

		Vector<T> retVal = new Vector<T>();

		LinkedList<Future<T>> tasks = new LinkedList<Future<T>>();

		ExecutorService executor = Executors.newFixedThreadPool(4);

		for (int i = 1; i <= pageProcessor.getNumberOfPages(); ++i) {

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

		}

		executor.shutdown();

		return retVal;

	}

}
