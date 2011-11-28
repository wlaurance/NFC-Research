package com.wlaurance.wm.edu;

import java.util.List;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

public class AcrNfcResearch {
	public static void main(String args[]){
		List<CardTerminal> terminals;
		TerminalFactory fac = TerminalFactory.getDefault();
		try {
			terminals = fac.terminals().list();
			System.out.println(terminals);
		} catch (CardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
