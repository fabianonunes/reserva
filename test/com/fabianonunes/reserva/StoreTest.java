package com.fabianonunes.reserva;

import java.io.File;
import java.io.IOException;

import org.jpedal.exception.PdfException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.itextpdf.text.DocumentException;

public class StoreTest {

	Store s;

	@Before
	public void setUp() throws Exception {

		s = new Store(
				new File(
						"/home/fabiano/Desktop/separa/AIRR.0040700-13.2005.5.17.0012.P1.pdf"));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSplit() throws IOException, DocumentException, PdfException {
		s.split();
	}

}
