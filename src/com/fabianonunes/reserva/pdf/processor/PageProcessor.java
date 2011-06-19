package com.fabianonunes.reserva.pdf.processor;

import com.fabianonunes.reserva.pdf.PdfPageIterator;
import com.itextpdf.text.pdf.PdfDictionary;

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
	 * @param page
	 *            the <tt>PdfDictionary</tt> representing the PDF page
	 * @param pageNumber
	 *            the page number
	 * @return returns a result for <tt>PdfPageIterator</tt>
	 * @throws Throwable
	 */
	public T process(PdfDictionary page, Integer pageNumber) throws Throwable;

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
	 * Sets the progress of processor
	 * 
	 * @param progress
	 *            the progress from 0 to 100
	 */
	public void setProgress(Float progress);

	/**
	 * Sets a iterator as a property of this processor
	 * 
	 * @param iterator
	 */
	public void setIterator(PdfPageIterator<T> iterator);

}
