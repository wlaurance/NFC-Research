package com.wlaurance.wm.edu;

import ds.nfcip.NFCIPException;
import ds.nfcip.se.NFCIPConnection;

public class ACRNfcConnection {

	final NFCIPConnection connection1 = new NFCIPConnection();
	final NFCIPConnection connection2 = new NFCIPConnection();
	int LOG_LEVEL = 5;

	byte MIFARE_1K_BLOCK = 0x00;
	byte MIFARE_1K_BYTES = (byte) 0x04;
	byte[] MY_KEY = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

	public void run() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				setConnection1(1);
				loadKeys();
				authenticate();
				readCard();
			}

		}).start();

	}

	private void authenticate(){
		try {
			System.out.println(connection1.authenticate());
		} catch (NFCIPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loadKeys() {
		try {
			System.out.println(connection1.loadAuthenticationKeys(MY_KEY));
		} catch (NFCIPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void readCard() {
		try {
			System.out.println(connection1.readCard(this.MIFARE_1K_BLOCK,
					this.MIFARE_1K_BYTES));
		} catch (NFCIPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setConnection1(int terminal) {

		try {
			connection1.setTerminal(terminal);
			connection1.setLogging(System.out, this.LOG_LEVEL);
			connection1.setMode(NFCIPConnection.INITIATOR);
			System.out.println(connection1.getMode());

		} catch (NFCIPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setConnection2(int terminal) {
		try {
			connection2.setTerminal(terminal);
			connection2.setLogging(System.err, this.LOG_LEVEL);
			connection2.setMode(NFCIPConnection.TARGET);
			System.out.println(connection2.getMode());
			System.out.println(connection2.receive());
		} catch (NFCIPException e) {
			e.printStackTrace();
		}
	}

	private byte[] getByteData() {
		byte[] data = new byte[200];
		for (int k = 0; k < data.length; k++)
			data[k] = (byte) (255 - k);
		return data;
	}

}
