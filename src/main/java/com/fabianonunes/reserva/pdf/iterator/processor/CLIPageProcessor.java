package com.fabianonunes.reserva.pdf.iterator.processor;


import java.util.Formatter;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.fabianonunes.reserva.pdf.iterator.PageIterator;

public abstract class CLIPageProcessor<T> implements PageProcessor<T> {

	private Boolean stop = false;

	protected PageIterator<T> iterator;

	@Override
	public Boolean isStopped() {
		return stop;
	}

	@Override
	public void stop() {

		this.stop = true;

	}
	
	@Override
	public T process(Integer pageNumber) throws Throwable {
		
		Float progress = (float) (pageNumber * 100 / getNumberOfPages());

		StringBuilder sb = new StringBuilder();

		Formatter formater = new Formatter(sb, Locale.US);

		Integer barProgress = (int) (50 * progress / 100);

		String bar = StringUtils.leftPad(">", barProgress, "=");
		String empty = StringUtils.repeat(" ", 50 - bar.length());
		String current = progress.intValue() + "% ";

		sb.append(StringUtils.leftPad(current, 5, " "));
		sb.append("[");
		sb.append(bar);
		sb.append(empty);
		sb.append("] ");

		formater.format("%04d", pageNumber);

		printAndReturn(sb.toString() + "/" + getNumberOfPages());

		return null;
		
	}

	public void printAndReturn(String text) {
		System.out.print(text + "\r");
	}

	@Override
	public void setIterator(PageIterator<T> iterator) {
		this.iterator = iterator;
	}

}
