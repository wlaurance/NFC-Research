package com.wlaurance.wm.edu.testing;

import com.wlaurance.wm.edu.DataIntegrityFormat;

import junit.framework.TestCase;

public class DataIntegrityFormatTest extends TestCase {
	DataIntegrityFormat dif;

	public void setUp() {
		dif = new DataIntegrityFormat("SHA1", "www.wlaurance.com");
	}

	public void testDataIntegrityFormat() {
		assertFalse(dif == null);
	}

	public void testSetData() {
		dif.setData("blahblah");
		assertEquals("blahblah", dif.getData());
	}

	public void testGetData() {
		assertEquals("www.wlaurance.com", dif.getData());
	}

	public void testGetCanonicalFormat() {
		System.out.println(dif.getCanonicalFormat());
	}

}
