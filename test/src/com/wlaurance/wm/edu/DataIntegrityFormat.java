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

	public DataIntegrityFormat(String messageDigestAlgorithm, String data) {
		setData(data);
		try {
			md = MessageDigest.getInstance(messageDigestAlgorithm);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
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
		return "UID=" + UID + "?MD=" + getMessageDigest() + "?URL=" + data;
	}

	private String getMessageDigest() {
		DigestInputStream dis = new DigestInputStream(new ByteArrayInputStream(
				new String(data + secretSalt).getBytes()), md);
		try {
			while (dis.read() != -1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return byteArray2Hex(dis.getMessageDigest().digest());
	}
	
	  private static String byteArray2Hex(byte[] hash) {
	        Formatter formatter = new Formatter();
	        for (byte b : hash) {
	            formatter.format("%02x", b);
	        }
	        return formatter.toString();
	    }
}
