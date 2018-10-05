package data;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

import visulas.GUI;

public class DeEnCode {
	
	private static String CHARSET = "ISO_8859_1";
	private static int CHARAMOUNT = 256;
	private static BigInteger CHARSETSIZE   = new BigInteger( CHARAMOUNT   +"");
	private static BigInteger CHARSETMAXINT = new BigInteger((CHARAMOUNT/2)+"");
	
	private GUI gui;
	
	/**
	 * new DeEnCode Object
	 * @param gui the GUI with is currently used by the user
	 */
	public DeEnCode(GUI gui){
		this.gui = gui;
	}
	
	/**
	 * decodes the inputed String using One-Time-Pad Logic and the inputed password
	 * @param text the text to be decoded
	 * @param password the password to be used for decoding
	 * @return decoded String 
	 * @throws UnsupportedEncodingException is returned if a Char from the inputed text is not existing in the default Charset
	 */
	public String decode(String text, String password) throws UnsupportedEncodingException{
		try {
			return new String(decode(text.getBytes(CHARSET), password), CHARSET);
		} catch (UnsupportedEncodingException e) {
			System.out.println("Error while reading a char, that is not supported");
			throw new UnsupportedEncodingException();
		}
		
	}
	
	/**
	 * decodes byte[] data using One-Time-Pad Logic and the inputed password
	 * @param bytes the encoded bytes (double amount of decoded ones)
	 * @param password the password to be used for decoding
	 * @return decoded byte[] which is half as long as the encoded array 
	 */
	public byte[] decode(byte[] bytes, String password) {
		
		bytes = Arrays.copyOf(bytes, bytes.length-1);
		gui.setProgress(0.1);		
		
		//get BitArray from ByteArray
		BitSet decode = BitSet.valueOf(bytes);
		gui.setProgress(0.2);
		
		//get BitArray out of the ByteArray representing the code which should be as close as possible to a unique one generated from the password
		BitSet code = getCloseToUniqueCode(password, decode.length());
		gui.setProgress(0.3);
		
		//Perform decoding operation
		code.xor(decode);
		gui.setProgress(0.6);
		
		//remove random part
		byte[] data = deRandomize(decode.toByteArray());
		gui.setProgress(0.9);
		return data;
	}

	/**
	 * encodes the inputed String using One-Time-Pad Logic and the inputed password
	 * @param text the text to be encoded
	 * @param password the password to be used for encoding
	 * @return encoded String 
	 * @throws UnsupportedEncodingException is returned if a Char from the inputed text is not existing in the default Charset
	 */
	public String encode(String text, String password) throws UnsupportedEncodingException{
		try {
			byte[] encooded = encode(text.getBytes(CHARSET), password);
			return new String(encooded, CHARSET);
		} catch (UnsupportedEncodingException e) {
			System.out.println("Error while reading a char, that is not supported");
			throw new UnsupportedEncodingException();
		}
	}

	/**
	 * encodes byte[] data using One-Time-Pad Logic and the inputed password
	 * @param bytes the decoded bytes (double amount of decoded ones)
	 * @param password the password to be used for encoding
	 * @return encoded byte[] which is twice as long as the decoded array 
	 */
	public byte[] encode(byte[] bytes, String password) {
		gui.setProgress(0.1);
		
		//generate BitArray out of ByteArray (adding randomness)
		BitSet encode = randomize(bytes);
		gui.setProgress(0.3);

		//get BitArray out of the ByteArray representing the code which should be as close as possible to a unique one generated from the password
		BitSet code = getCloseToUniqueCode(password, encode.length());
		gui.setProgress(0.4);

		//Perform encoding operation
		code.xor(encode);
		gui.setProgress(0.8);
		
		//Encoded BitArray to encoded ByteArray
		byte[] data = encode.toByteArray();
		gui.setProgress(0.9);
		return data;
	}

	/**
	 * adds randomness to the byte array (per byte 256 to 256*127 only a product of 256)
	 * @param bytes the bytes to be randomized
	 * @return randomized BitArray with double length then the origin
	 */
	private BitSet randomize(byte[] bytes) {
		ByteBuffer data = ByteBuffer.allocate(bytes.length*2+1);
		//for each byte add randomness
		for(int i = 0; i<bytes.length; i++){
			//add randomness
			int d = ((int)bytes[i])+(int)(CHARAMOUNT*((int)(126*Math.random())))+CHARAMOUNT;
			//add 2 byte arrays (representing the randomized one)
			byte[] intBytes = new BigInteger(d+"").toByteArray();
			data.put(intBytes);
		}
		data.put((byte)255);
		//return BitArray out of ByteBuffer
		data.rewind();
		return BitSet.valueOf(data);
	}

	/**
	 * removes randomness from the ByteArray (two bytes to one that is not randomized)
	 * @param bytes where to remove randomness (has to be an even amount of bytes)
	 * @return a non randomized ByteArray representing the origin ByteArray
	 */
	private byte[] deRandomize(byte[] bytes) {
		ByteBuffer data = ByteBuffer.allocate((int) Math.ceil(bytes.length/2.0));
		//for each to bytes
		for(int i = 0; i<Math.ceil(bytes.length/2.0)*2; i+=2){
			//get randomized integer
			BigInteger integer;
			if(i+1<bytes.length) integer = new BigInteger(new byte[]{bytes[i], bytes[i+1]});
			else integer = new BigInteger(new byte[]{bytes[i], (byte)0});
			BigInteger randomPart = ((integer.divide(CHARSETSIZE)).multiply(CHARSETSIZE));
			//remove randomized part
			integer = integer.subtract(randomPart);
			if 		(integer.subtract(CHARSETMAXINT).compareTo(BigInteger.ZERO)>=0)integer = CHARSETMAXINT.negate().add(integer.subtract(CHARSETMAXINT));
			else if (integer.add	 (CHARSETMAXINT).compareTo(BigInteger.ZERO)<=0)integer = integer.negate().subtract(CHARSETMAXINT);
			//add orign byte to ByteBuffer
			data.put(integer.toByteArray());
		}
		//return non-randomized ByteArray
		data.rewind();
		return data.array();
	}
	
	/**
	 * tries to generate a Code from the password with is unique to that password
	 * @param password the password from which the unique Code is to be generated
	 * @param length the length the unique Code is supposed to have
	 * @return the unique code in form of a BitArray
	 */
	private BitSet getCloseToUniqueCode(String password, int length){
		try {
			//get Integer representing the password
			BigInteger integer = new BigInteger(password.getBytes(CHARSET));
			//increase the integer until its binary value has the code length
			while(integer.toByteArray().length<length/8){
				integer = integer.pow(5);
			}
			//return BiteArray representing the integer
			BitSet set = BitSet.valueOf(integer.toByteArray());
			return set;
		} catch (UnsupportedEncodingException e) {
			System.out.println("Error while reading a byte, that is not supported");			
		}
		return new BitSet();
	}
	
	/**
	 * calculates the String which is represented by the ByteArray using the default Charset 
	 * @param data the data to be represented as a String
	 * @return String representing the data using the default Charset
	 * @throws UnsupportedEncodingException is thrown if the ByteArray can not be represented using the default Charset
	 */
	public static String BYTESTOSTRING(byte[] data) throws UnsupportedEncodingException{
		return new String(data, CHARSET);
	}

	/**
	 * calculates the ByteArray which is represented by the String using the default Charset 
	 * @param text the String to be represented as a ByteArray
	 * @return ByteArray representing the String using the default Charset
	 * @throws UnsupportedEncodingException is thrown if the String can not be represented using the default Charset
	 */
	public static byte[] STRINGTOBYTES(String text) throws UnsupportedEncodingException {
		return text.getBytes(CHARSET);
	}

}
