package com.wlaurance.wm.edu;

import javax.smartcardio.ResponseAPDU;

import ds.nfcip.NFCIPException;
import ds.nfcip.se.NFCIPConnection;

public class ACRNfcConnection {

	NFCIPConnection connection1;
	int LOG_LEVEL = 0;
	public static boolean DEBUG = false;

	byte MIFARE_1K_BLOCK = 0x01;
	byte MIFARE_1K_BYTES = (byte) 0x10;

	byte[] MY_DATA = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xBF };

	public ACRNfcConnection(boolean debug) {
		DEBUG = debug;
		connection1 = new NFCIPConnection(DEBUG);
		setConnection1(1);
		loadKeys();
	}

	public void run() {

		setConnection1(1);
		loadKeys();
		readCardBinary(MIFARE_1K_BLOCK, MIFARE_1K_BYTES);
		writeCardBinary((byte) 0x01, MY_DATA);

	}

	private void authenticate(byte block) {
		try {
			this.printADPU(connection1.authenticate(block), "authenticate");
		} catch (NFCIPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadKeys() {
		try {
			this.printADPU(connection1
					.loadAuthenticationKeys(NFCIPConnection.KEY),
					"loadAuthenticationKeys");
		} catch (NFCIPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ResponseAPDU readCardUID() {
		try {
			return connection1.readCardUID();
		} catch (NFCIPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public byte[] readCardBinary(byte block, byte numBytes) {
		try {
			authenticate(block);
			return connection1.readCard(block, numBytes).getBytes();
		} catch (NFCIPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return MY_DATA;
	}

	public void writeCardBinary(byte block, byte[] data) {
		try {
			authenticate(block);
			this.printADPU(connection1.writeBinBlocks(block, data),
					"writeBinBlocks");
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
			if (DEBUG)
				System.out.println(connection1.getMode());

		} catch (NFCIPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void printADPU(ResponseAPDU r, String method) {
		if (DEBUG) {
			System.out.println(r + "\n************DATA****FROM****" + method
					+ "******");
			NFCIPConnection.printByteArray(r.getData());
		}
	}
}
