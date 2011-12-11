package com.wlaurance.wm.edu;

import javax.smartcardio.ResponseAPDU;

public abstract class TagConnection {

	public abstract void writeToCard(String data) throws TagConnectionException;
	public abstract String readFromCard();
	public abstract ResponseAPDU readUIDFromCard();
	
	public String humanReadable(byte[] bArray){
		return new String(bArray);
	}
}
