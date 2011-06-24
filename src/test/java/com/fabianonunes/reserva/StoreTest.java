package com.fabianonunes.reserva;

import java.io.File;

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
		s.split();
	}

}
