package com.wlaurance.wm.edu;

import java.util.List;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

public class AcrNfcResearch {
	
public static String myURL = "www.wlaurance.com";
	
	public static void main(String args[]) {
		Mifare1kTagConnection c = new Mifare1kTagConnection();
		c.writeToCard(myURL);
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
