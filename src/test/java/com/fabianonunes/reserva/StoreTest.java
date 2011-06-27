package com.fabianonunes.reserva;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.text.Normalizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StoreTest {

	Store s;

	@Before
	public void setUp() throws Exception {

		s = new Store(new File("/home/fabiano/combo.pdf"));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSplit() throws Exception {
		// s.split();

		File file = new File("/home/fabiano/Desktop/untitled");
		File output = new File("/home/fabiano/Desktop/output");

		String reference = FileUtils.readFileToString(file);

		reference = Normalizer.normalize(reference, Normalizer.Form.NFD);
		reference = reference.replaceAll("[^\\p{L}\\s]", "");
		
//		System.out.println(reference);

		IOUtils.write(reference, new FileOutputStream(output));
	}

}
