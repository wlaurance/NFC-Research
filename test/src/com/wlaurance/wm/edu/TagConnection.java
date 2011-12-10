package com.wlaurance.wm.edu;

public abstract class TagConnection {

	public abstract void writeToCard(String data) throws TagConnectionException;
	public abstract String readFromCard();
	
	public String humanReadable(byte[] bArray){
		return new String(bArray);
	}
}
