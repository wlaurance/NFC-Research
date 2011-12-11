package com.wlaurance.wm.edu.testing;

import com.wlaurance.wm.edu.DataIntegrityFormat;
import com.wlaurance.wm.edu.SingletonDataStore;

import junit.framework.TestCase;

public class DataIntegrityFormatTest extends TestCase {
	DataIntegrityFormat dif;
	SingletonDataStore ds = SingletonDataStore.getInstance();

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
		assertEquals(
				"UID=123456789?MD=3b2b133bbcd76d6770a885d1e2e8faea7fc3c4b7?URL=www.wlaurance.com",
				dif.getCanonicalFormat());
		System.out.println(dif.getCanonicalFormat());
	}
	
	public void testGetSalt(){
		assertEquals("cs454", ds.getSalt("123456789"));
	}

}
