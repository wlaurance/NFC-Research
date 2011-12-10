package com.wlaurance.wm.edu;

import ds.nfcip.se.NFCIPConnection;

public class Mifare1kTagConnection extends TagConnection{
	ACRNfcConnection connection;
	static int MIFARE1K_BLOCK_SIZE = 16; 
	
	public Mifare1kTagConnection(){
		connection = new ACRNfcConnection();
	}
	
	public void writeToCard(String data){
		byte[] bytes = data.getBytes();
		NFCIPConnection.printByteArray(bytes);
	}

	@Override
	public String readFromCard() {
		// TODO Auto-generated method stub
		return null;
	}
}
