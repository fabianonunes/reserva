package com.fabianonunes.reserva;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.itextpdf.text.DocumentException;

public class ReservaAppTest {

	ReservaApp app;

	@Before
	public void setUp() throws Exception {

		app = new ReservaApp(new File(
				"/home/fabiano/10-2009-000-00-00.1 v1-6.pdf"));
	}
	
	@Test
	public void testOne() throws IOException, DocumentException {
		
		app.run();
		
	}

}
