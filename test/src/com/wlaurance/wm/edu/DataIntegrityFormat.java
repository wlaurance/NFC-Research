package com.wlaurance.wm.edu;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class DataIntegrityFormat {
	private String data;
	private String UID = "123456789";
	private MessageDigest md;
	private String secretSalt = "cs454";
	private SingletonDataStore ds = SingletonDataStore.getInstance();

	public DataIntegrityFormat(String messageDigestAlgorithm, String data) {
		ds.store(UID, secretSalt);
		setData(data);
		try {
			md = MessageDigest.getInstance(messageDigestAlgorithm);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public String getCanonicalFormat() {
		return "UID=" + UID + "?MD=" + getMessageDigest(secretSalt) + "?URL=" + data;
	}

	public String getMessageDigest(String secretSalt) {
		DigestInputStream dis = new DigestInputStream(new ByteArrayInputStream(
				new String(data + secretSalt).getBytes()), md);
		try {
			while (dis.read() != -1)
				;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteArray2Hex(dis.getMessageDigest().digest());
	}

	/**
	 * Taken from http://www.javablogging.com/sha1-and-md5-checksums-in-java/
	 * Author: PPOW, Ocotober 21, 2009
	 * 
	 * @param hash
	 * @return
	 */
	private static String byteArray2Hex(byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}
}
