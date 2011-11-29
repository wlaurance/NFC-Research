package com.wlaurance.wm.edu;

import java.util.List;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

public class AcrNfcResearch {
	

	public static void main(String args[]) {
		List<CardTerminal> terminals = null;
		ACRNfcConnection connection = null;
		TerminalFactory fac = TerminalFactory.getDefault();
		try {
			terminals = fac.terminals().list();
		} catch (CardException e) {
			System.err.println("No terminals detected");
			e.printStackTrace();
		}
		printTerminalStatus(terminals);
		connection = new ACRNfcConnection();
		connection.run();
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
