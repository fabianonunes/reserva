package com.fabianonunes.reserva;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.itextpdf.text.DocumentException;

public class MergerTest {

	Merger mg;

	@Before
	public void setUp() {

		mg = new Merger();

	}

	@Test
	public void testMerging() throws IOException, DocumentException {

		mg.untitled(new File(""));

	}

}
