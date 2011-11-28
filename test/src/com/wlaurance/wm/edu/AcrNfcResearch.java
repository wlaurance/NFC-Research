package com.wlaurance.wm.edu;

import java.util.List;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

import ds.nfcip.se.NFCIPConnection;

public class AcrNfcResearch {
	private NFCIPConnection connection = null;

	public static void main(String args[]) {
		List<CardTerminal> terminals = null;
		TerminalFactory fac = TerminalFactory.getDefault();
		try {
			terminals = fac.terminals().list();
			System.out.println(terminals);
		} catch (CardException e) {
			System.err.println("No terminals detected");
			e.printStackTrace();
		}
		printTerminalStatus(terminals);
	}

	public void initConnection() {
		connection = new NFCIPConnection();
	}

	public static void printTerminalStatus(List<CardTerminal> t) {
		if (t != null) {
			for (CardTerminal terminal : t) {
				try {
					System.out.println(terminal.toString() + "  "
							+ terminal.isCardPresent());
				} catch (CardException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			System.err.println("CardTerminal list is null");
		}
	}
}
