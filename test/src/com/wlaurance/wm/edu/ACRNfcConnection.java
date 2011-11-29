package com.wlaurance.wm.edu;

import ds.nfcip.NFCIPException;
import ds.nfcip.se.NFCIPConnection;

public class ACRNfcConnection {

	final NFCIPConnection connection1 = new NFCIPConnection();
	final NFCIPConnection connection2 = new NFCIPConnection();
	int LOG_LEVEL = 3;

	public void run() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				setConnection1(1);
			}

		}).start();

		new Thread(new Runnable() {

			@Override
			public void run() {
				setConnection2(2);
			}

		}).start();

	}

	private void setConnection1(int terminal) {

		try {
			connection1.setTerminal(terminal);
			connection1.setLogging(System.out, this.LOG_LEVEL);
			connection1.setMode(NFCIPConnection.INITIATOR);
			System.out.println(connection1.getMode());
			System.out.println(connection1.getFirmwareVersion());
			connection1.send(getByteData());
			// System.out.println("Recieved " + connection1.receive());
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
		byte[] data = new byte[10];
		for (int k = 0; k < data.length; k++)
			data[k] = (byte) (255 - k);
		return data;
	}

}
