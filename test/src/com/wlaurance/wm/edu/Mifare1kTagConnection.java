package com.wlaurance.wm.edu;

import java.util.ArrayList;

import ds.nfcip.se.NFCIPConnection;

public class Mifare1kTagConnection extends TagConnection{
	ACRNfcConnection connection;
	static final int MIFARE1K_BLOCK_SIZE = 16; 
	static final int MIFARE1K_SECTOR_LENGTH = 16;
	static final int MIFARE1K_BLOCK_PER_SECTOR = 4;
	static final int MIFARE1K_AVAILABLE = 752;
	public Mifare1kTagConnection(){
		connection = new ACRNfcConnection();
	}
	
	public void writeToCard(String data) throws TagConnectionException{
		byte[] bytes = data.getBytes();
		NFCIPConnection.printByteArray(bytes);
		int length = bytes.length;
		if (length > MIFARE1K_AVAILABLE){
			throw new TagConnectionException("Cannot write this much data to the card");
		}
		byte[] bytePointer = null;
		ArrayList<byte[]> myList = new ArrayList<byte[]>();
		for (int i = 0; i < length; i++){
			int relativeByte = i % MIFARE1K_BLOCK_SIZE; 
			if (relativeByte == 0){
				if (bytePointer != null)
					myList.add(bytePointer);
				bytePointer = new byte[MIFARE1K_BLOCK_SIZE];
				bytePointer[0] = bytes[i];
			} else {
				bytePointer[relativeByte] = bytes[i];
			}
			
			if (i+1 >= length && bytePointer != null){
				myList.add(bytePointer);
			}
		}
		
		for (byte[] x : myList){
			NFCIPConnection.printByteArray(x);
		}
		
	}

	@Override
	public String readFromCard() {
		// TODO Auto-generated method stub
		return null;
	}
}
