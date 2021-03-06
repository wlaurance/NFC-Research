/*
 * NFCIPConnection - Java SE implementation of NFCIPConnection for the ACS 
 *                   ACR122 NFC reader
 * 
 * Copyright (C) 2009  François Kooman <F.Kooman@student.science.ru.nl>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package ds.nfcip.se;

import java.util.List;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

import ds.nfcip.NFCIPAbstract;
import ds.nfcip.NFCIPException;
import ds.nfcip.NFCIPInterface;
import ds.nfcip.NFCIPUtils;

/**
 * Java SE implementation of NFCIPConnection for the ACS ACR122 NFC reader
 * 
 * @author F. Kooman <F.Kooman@student.science.ru.nl> Additional stuff by W.
 *         Laurance <w.laurance@gmail.com>
 */
public class NFCIPConnection extends NFCIPAbstract implements NFCIPInterface {

	/* PN53x instructions */
	// private final byte GET_GENERAL_STATUS = (byte) 0x04;
	private final byte IN_DATA_EXCHANGE = (byte) 0x40;
	// private final byte IN_LIST_PASSIVE_TARGET = (byte) 0x4a;
	// private final byte IN_ATR = (byte) 0x50;
	private final byte IN_RELEASE = (byte) 0x52;
	private final byte IN_JUMP_FOR_DEP = (byte) 0x56;
	private final byte TG_GET_DATA = (byte) 0x86;
	private final byte TG_INIT_AS_TARGET = (byte) 0x8c;
	private final byte TG_SET_DATA = (byte) 0x8e;
	// private final byte TG_SET_META_DATA = (byte) 0x94;
	private final byte[] GET_FIRMWARE_VERSION = { (byte) 0xff, (byte) 0x00,
			(byte) 0x48, (byte) 0x00, (byte) 0x00 };

	/**
	 * New byte arrays added by Will Laurance <w.laurance@gmail.com>
	 */
	private final byte KEY_NUMBER = (byte) 0x00;
	private final int AUTH_DATA_BLOCK_NUMBER = 6;
	public final static byte[] KEY = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
	private final byte KEY_TYPE = (byte) 0x60;
	private final byte VERSION = (byte) 0x01;
	private final byte UID_LENGTH = (byte) 0x04;

	private final byte[] READ_CARD_UID = { (byte) 0xFF, (byte) 0xCA,
			(byte) 0x01, (byte) 0x00, UID_LENGTH };

	private final byte[] READ_CARD_DATA = { (byte) 0xFF, (byte) 0xB0,
			(byte) 0x00, (byte) 0x00, (byte) 0x00 };

	private final byte[] LOAD_AUTHENTICATION_HEADER = { (byte) 0xff,
			(byte) 0x82, (byte) 0x00, KEY_NUMBER, (byte) 0x06 };

	private byte[] LOAD_AUTHENTICATION_KEYS = new byte[this.LOAD_AUTHENTICATION_HEADER.length
			+ KEY.length];

	private final byte[] DO_AUTHENTICATE = { (byte) 0xff, (byte) 0x86,
			(byte) 0x00, (byte) 0x00, (byte) 0x05, VERSION, (byte) 0x00,
			(byte) 0x01, KEY_TYPE, KEY_NUMBER };

	private final byte[] UPDATE_BIN_BLOCKS = { (byte) 0xff, (byte) 0xD6,
			(byte) 0x00, (byte) 0x00, (byte) 0x10 };
	/**
	 * temporary buffer for storing data from sendCommand when in initiator mode
	 */
	private byte[] tmpSendStorage;

	private CardTerminal terminal = null;
	private CardChannel ch = null;
	boolean DEBUG = false;

	public NFCIPConnection(boolean debug) {
		super();
		DEBUG = debug;
		blockSize = 240;
		genAuthHeader();
	}

	private void genAuthHeader() {
		for (int i = 0; i < this.LOAD_AUTHENTICATION_HEADER.length + KEY.length; i++) {
			if (0 <= i && i < this.LOAD_AUTHENTICATION_HEADER.length) {
				this.LOAD_AUTHENTICATION_KEYS[i] = this.LOAD_AUTHENTICATION_HEADER[i];
			} else {
				this.LOAD_AUTHENTICATION_KEYS[i] = KEY[i
						- this.LOAD_AUTHENTICATION_HEADER.length];
			}
		}
	}

	private void connectToTerminal() throws NFCIPException {
		if (terminal == null)
			throw new NFCIPException("need to set terminal device first");
		Card card;
		try {
			if (terminal.isCardPresent()) {
				card = terminal.connect("*");
				ch = card.getBasicChannel();
			} else {
				throw new NFCIPException("Card Not Present");
			}
		} catch (CardException e) {
			throw new NFCIPException("problem with connecting to reader");
		}
		logMessage(2, "successful connection");
		logMessage(2, "ACS ACR122 firmware version: " + getFirmwareVersion());
	}

	protected void rawClose() throws NFCIPException {
		/*
		 * we don't really need to do anything here as the targets are released
		 * already by the close method and that is all that is needed for Java
		 * SE
		 */
	}

	protected void releaseTargets() throws NFCIPException {
		if (getMode() == INITIATOR || getMode() == FAKE_TARGET) {
			/* release all targets */
			transmit(IN_RELEASE, new byte[] { 0x00 });
			/*
			 * sleep after a release of target to turn off the radio for a while
			 * which helps with reconnecting to the phone that needs a little
			 * more time to reset target mode
			 */
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected void setInitiatorMode() throws NFCIPException {
		// byte[] initiatorPayload = { 0x00, 0x00, 0x00 }; // passive, 106kbps
		byte[] initiatorPayload = { 0x00, 0x02, 0x01, 0x00, (byte) 0xff,
				(byte) 0xff, 0x00, 0x00 }; // passive, 424kbps
		// byte[] initiatorPayload = { 0x01, 0x00, 0x00 }; // active, 106kbps
		// byte[] initiatorPayload = { 0x01, 0x02, 0x00, (byte) 0xff,(byte)
		// 0xff, 0x00, 0x00 }; // active, 424kbps

		transmit(IN_JUMP_FOR_DEP, initiatorPayload);
	}

	protected void setTargetMode() throws NFCIPException {
		byte[] targetPayload = { (byte) 0x00, (byte) 0x08, (byte) 0x00,
				(byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x40,
				(byte) 0x01, (byte) 0xFE, (byte) 0xA2, (byte) 0xA3,
				(byte) 0xA4, (byte) 0xA5, (byte) 0xA6, (byte) 0xA7,
				(byte) 0xC0, (byte) 0xC1, (byte) 0xC2, (byte) 0xC3,
				(byte) 0xC4, (byte) 0xC5, (byte) 0xC6, (byte) 0xC7,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xAA, (byte) 0x99,
				(byte) 0x88, (byte) 0x77, (byte) 0x66, (byte) 0x55,
				(byte) 0x44, (byte) 0x33, (byte) 0x22, (byte) 0x11,
				(byte) 0x00, (byte) 0x00 };
		transmit(TG_INIT_AS_TARGET, targetPayload);
	}

	/**
	 * Set the terminal to use
	 * 
	 * @param terminalNumber
	 *            the terminal to use, specify this as a number, the first
	 *            terminal has number 0
	 */
	public void setTerminal(int terminalNumber) throws NFCIPException {
		List<CardTerminal> terminals;
		try {
			TerminalFactory factory = TerminalFactory.getDefault();
			terminals = factory.terminals().list();
			if (terminals.size() == 0)
				terminals = null;
		} catch (CardException c) {
			terminals = null;
		}
		if (terminals != null && terminalNumber >= 0
				&& terminalNumber < terminals.size())
			terminal = terminals.get(terminalNumber);
		connectToTerminal();
	}

	/**
	 * Sends and receives APDUs to and from the PN53x, handles APDU and NFCIP
	 * data transfer error handling.
	 * 
	 * @param instr
	 *            The PN53x instruction
	 * @param payload
	 *            The payload to send
	 * 
	 * @return The response payload (without instruction bytes and status bytes)
	 */
	private byte[] transmit(byte instr, byte[] payload) throws NFCIPException {
		if (ch == null)
			throw new NFCIPException("channel not open");

		logMessage(3, instructionToString(instr));

		int payloadLength = (payload != null) ? payload.length : 0;
		byte[] instruction = { (byte) 0xd4, instr };

		/* ACR122 header */
		byte[] header = { (byte) 0xff, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) (instruction.length + payloadLength) };

		/* construct the command */
		byte[] cmd = NFCIPUtils.appendToByteArray(header, instruction, 0,
				instruction.length);
		/*
		 * if we are initiator and we want to send data to a target we need to
		 * add the target, either 0x01 or 0x02 because the PN53x supports 2
		 * targets at once. IN_JUMP_FOR_DEP handles only one target, so we use
		 * 0x01 here
		 */
		if (instr == IN_DATA_EXCHANGE) {
			cmd = NFCIPUtils.appendToByteArray(cmd, new byte[] { 0x01 }, 0, 1);
			/* increase APDU length byte */
			cmd[4]++;
		}

		cmd = NFCIPUtils.appendToByteArray(cmd, payload);

		try {
			logMessage(4, "Sent     (" + cmd.length + " bytes): "
					+ NFCIPUtils.byteArrayToString(cmd));

			CommandAPDU c = new CommandAPDU(cmd);
			ResponseAPDU r = ch.transmit(c);

			byte[] ra = r.getBytes();

			logMessage(4, "Received (" + ra.length + " bytes): "
					+ NFCIPUtils.byteArrayToString(ra));

			/* check whether APDU command was accepted by the ACS ACR122 */
			if (r.getSW1() == 0x63 && r.getSW2() == 0x27) {
				throw new CardException(
						"wrong checksum from contactless response (0x63 0x27");
			} else if (r.getSW1() == 0x63 && r.getSW2() == 0x7f) {
				throw new CardException("wrong PN53x command (0x63 0x7f)");
			} else if (r.getSW1() != 0x90 && r.getSW2() != 0x00) {
				throw new CardException("unknown error ("
						+ NFCIPUtils.byteToString(r.getSW1()) + " "
						+ NFCIPUtils.byteToString(r.getSW2()));
			}

			/*
			 * some responses to commands have a status field, we check that
			 * here, this applies for TgSetData, TgGetData and InDataExchange.
			 */
			if ((instr == TG_SET_DATA || instr == TG_GET_DATA || instr == IN_DATA_EXCHANGE)
					&& ra[2] != (byte) 0x00) {
				throw new NFCIPException("communication error ("
						+ NFCIPUtils.byteToString(ra[2]) + ")");
			}
			/* strip of the response command codes and the status field */
			ra = NFCIPUtils.subByteArray(ra, 2, ra.length - 4);

			/*
			 * remove status byte from result as we don't need this for custom
			 * chaining
			 */
			if (instr == TG_GET_DATA || instr == IN_DATA_EXCHANGE) {
				ra = NFCIPUtils.subByteArray(ra, 1, ra.length - 1);
			}
			return ra;
		} catch (CardException e) {
			throw new NFCIPException("problem with transmitting data ("
					+ e.getMessage() + ")");
		}
	}

	/**
	 * Convert an instruction byte to a human readable text
	 * 
	 * @param instr
	 *            the instruction byte
	 * @return the human readable text
	 */
	public String instructionToString(byte instr) {
		switch (instr) {
		case IN_DATA_EXCHANGE:
			return "IN_DATA_EXCHANGE";
		case IN_RELEASE:
			return "IN_RELEASE";
		case IN_JUMP_FOR_DEP:
			return "IN_JUMP_FOR_DEP";
		case TG_GET_DATA:
			return "TG_GET_DATA";
		case TG_INIT_AS_TARGET:
			return "TG_INIT_AS_TARGET";
		case TG_SET_DATA:
			return "TG_SET_DATA";
		default:
			return "UNKNOWN INSTRUCTION (" + NFCIPUtils.byteToString(instr)
					+ ")";
		}
	}

	protected void sendCommand(byte[] data) throws NFCIPException {
		if (getMode() == INITIATOR || getMode() == FAKE_TARGET) {
			tmpSendStorage = transmit(IN_DATA_EXCHANGE, data);
		} else {
			transmit(TG_SET_DATA, data);
		}
	}

	protected byte[] receiveCommand() throws NFCIPException {
		if (getMode() == INITIATOR || getMode() == FAKE_TARGET) {
			return tmpSendStorage;
		} else {
			return transmit(TG_GET_DATA, null);
		}
	}

	public String getFirmwareVersion() throws NFCIPException {
		try {
			CommandAPDU c = new CommandAPDU(GET_FIRMWARE_VERSION);
			if (ch == null) {
				throw new NFCIPException("channel not open");
			}
			return new String(ch.transmit(c).getBytes());
		} catch (CardException e) {
			throw new NFCIPException("problem requesting firmware version");
		}
	}

	/**
	 * ========================================================================
	 * =========== New Stuff by Will Laurance, <w.laurance@gmail.com>
	 * 
	 */

	public ResponseAPDU readCardUID() throws NFCIPException {
		if (DEBUG)
			printByteArray(READ_CARD_UID);
		try {
			CommandAPDU d = new CommandAPDU(READ_CARD_UID);
			if (ch == null) {
				throw new NFCIPException("channel not open");
			}
			return ch.transmit(d);
		} catch (CardException e) {
			throw new NFCIPException("problem reading NUID");
		}
	}

	public ResponseAPDU readCard(byte blockNumber, byte numToRead)
			throws NFCIPException {
		byte[] cp = READ_CARD_DATA;
		cp[3] = blockNumber;
		cp[4] = numToRead;
		if (DEBUG)
			printByteArray(cp);
		try {
			CommandAPDU d = new CommandAPDU(cp);
			if (DEBUG)
				System.out.println(d.toString());
			if (ch == null) {
				throw new NFCIPException("channel not open");
			}
			return ch.transmit(d);
		} catch (CardException e) {
			throw new NFCIPException("problem reading data");
		}
	}

	public ResponseAPDU loadAuthenticationKeys(byte[] key)
			throws NFCIPException {
		byte[] cp = this.LOAD_AUTHENTICATION_KEYS;
		if (key.length != 6)
			throw new NFCIPException("supplied key must be 6 bytes");
		for (int i = 0; i < key.length; i++) {
			cp[i + 5] = key[i];
		}
		if (DEBUG)
			printByteArray(cp);
		try {
			CommandAPDU d = new CommandAPDU(cp);
			if (DEBUG)
				System.out.println(d.toString());
			if (ch == null) {
				throw new NFCIPException("channel not open");
			}
			return ch.transmit(d);
		} catch (CardException e) {
			throw new NFCIPException("problem reading data");
		}
	}

	public ResponseAPDU authenticate(byte blockNumber) throws NFCIPException {
		byte[] cp = this.DO_AUTHENTICATE;
		cp[AUTH_DATA_BLOCK_NUMBER] = blockNumber;
		if (DEBUG)
			NFCIPConnection.printByteArray(cp);
		try {
			CommandAPDU d = new CommandAPDU(cp);
			if (DEBUG)
				System.out.println(d.toString());
			if (ch == null) {
				throw new NFCIPException("channel not open");
			}
			return ch.transmit(d);
		} catch (CardException e) {
			throw new NFCIPException("problem reading data");
		}
	}

	public ResponseAPDU writeBinBlocks(byte blockNum, byte[] blockData)
			throws NFCIPException {
		byte[] write = new byte[this.UPDATE_BIN_BLOCKS.length
				+ blockData.length];
		for (int i = 0; i < this.UPDATE_BIN_BLOCKS.length; i++) {
			write[i] = this.UPDATE_BIN_BLOCKS[i];
			if (i == 3)
				write[i] = blockNum;
			if (i == 4)
				write[i] = Byte.parseByte(String.valueOf(blockData.length));
		}

		for (int i = 5; i < blockData.length + 5; i++) {
			write[i] = blockData[i - 5];
		}

		if (DEBUG)
			printByteArray(write);
		try {
			CommandAPDU d = new CommandAPDU(write);
			if (DEBUG)
				System.out.println(d.toString());
			if (ch == null) {
				throw new NFCIPException("channel not open");
			}
			return ch.transmit(d);
		} catch (CardException e) {
			throw new NFCIPException("problem reading data");
		}
	}

	public static void printByteArray(byte[] array) {
		for (byte a : array)
			System.out.println(a);
	}
}