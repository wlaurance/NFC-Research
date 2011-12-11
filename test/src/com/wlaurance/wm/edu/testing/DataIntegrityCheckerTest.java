package com.wlaurance.wm.edu.testing;

import junit.framework.TestCase;

import com.wlaurance.wm.edu.DataIntegrityChecker;
import com.wlaurance.wm.edu.DataIntegrityFormat;

public class DataIntegrityCheckerTest extends TestCase {

	DataIntegrityChecker dic;

	public void setUp() {
		dic = new DataIntegrityChecker((new DataIntegrityFormat("SHA1",
				"www.wlaurance.com")).getCanonicalFormat());
	}
	
	public void testDoesItPass(){
		dic.parse();
		assertTrue(dic.doesItPass());
	}
	
	public void testBADDATA(){
		dic.setWrongURL("www.evilsite.com");
		assertFalse(dic.doesItPass());
	}

}
