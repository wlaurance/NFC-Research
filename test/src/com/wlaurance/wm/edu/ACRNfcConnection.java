package com.wlaurance.wm.edu;

import javax.smartcardio.ResponseAPDU;

import ds.nfcip.NFCIPException;
import ds.nfcip.se.NFCIPConnection;

public class ACRNfcConnection {

	final NFCIPConnection connection1 = new NFCIPConnection();
	final NFCIPConnection connection2 = new NFCIPConnection();
	int LOG_LEVEL = 5;

	byte MIFARE_1K_BLOCK = 0x01;
	byte MIFARE_1K_BYTES = (byte) 0x04;

	byte[] MY_DATA = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF };

	public void run() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				setConnection1(1);
				loadKeys();
				readCard();
				writeCardBinary((byte)0x01, MY_DATA);
				
			}

		}).start();

	}

	private void authenticate(byte block) {
		try {
			this.printADPU(connection1.authenticate(block));
		} catch (NFCIPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadKeys() {
		try {
			this.printADPU(connection1.loadAuthenticationKeys(NFCIPConnection.KEY));
		} catch (NFCIPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void readCard() {
		try {
			this.printADPU(connection1.readCard(this.MIFARE_1K_BLOCK,
					this.MIFARE_1K_BYTES));
		} catch (NFCIPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void readCardBinary(byte block, byte numBytes) {
		try {
			this.printADPU(connection1.readCard(block, numBytes));
		} catch (NFCIPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeCardBinary(byte block, byte[] data) {
		try {
			this.connection1.authenticate(block);
			this.printADPU(connection1.writeBinBlocks(block, data));
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

	public void printADPU(ResponseAPDU r) {
		System.out.println(r + "\n************DATA************ ");
		NFCIPConnection.printByteArray(r.getData());
	}
}
