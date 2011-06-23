package com.fabianonunes.reserva.pdf.iterator.processor;

import com.fabianonunes.reserva.pdf.iterator.PageIterator;

/**
 * Interface que define um processor para a classe <tt>PdfPageIterator</tt>. O
 * parâmetro <tt>T</tt> representa o tipo do objeto de retorno.
 * 
 * @author fabiano
 * 
 * @param <T>
 *            o tipo do retorno do processor
 */
public interface PageProcessor<T> {

	/**
	 * Process the page
	 * 
	 * @param pageNumber
	 *            the page number
	 * @return returns a result for <tt>PdfPageIterator</tt>
	 * @throws Throwable
	 */
	public T process(Integer pageNumber) throws Throwable;

	/**
	 * Stops the processor
	 */
	public void stop();

	/**
	 * Returns the state of processor
	 * 
	 * @return <code>true</code> if the processor is stopped
	 */
	public Boolean isStopped();

	/**
	 * Sets a iterator as a property of this processor
	 * 
	 * @param iterator
	 */
	public void setIterator(PageIterator<T> iterator);

	public int getNumberOfPages();

}
