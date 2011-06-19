package com.fabianonunes.reserva.pdf;


import java.util.Formatter;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

public abstract class CLIPageProcessor<T> implements PageProcessor<T> {

	private Boolean stop = false;

	protected PdfPageIterator<T> pdfIterator;

	public CLIPageProcessor(PdfPageIterator<T> pdfIterator) {
		this.pdfIterator = pdfIterator;
	}

	@Override
	public Boolean isStopped() {
		return stop;
	}

	@Override
	public void stop() {

		this.stop = true;

	}

	public void printAndReturn(String text) {
		System.out.print(text + "\r");
	}

	@Override
	public void setProgress(Float progress) {

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

		formater.format("%04d", pdfIterator.getCurrentPage());

		printAndReturn(sb.toString() + "/" + pdfIterator.getTotalOfPages());

	}

}
