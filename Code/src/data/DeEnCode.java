package data;

import java.math.BigInteger;
import java.util.HashMap;

import visulas.GUI;

public class DeEnCode {

	//ISO_8859_1-Format used char-collection
	private char[] Alphabet;
	private HashMap<Character, Integer> AlphanumericCode = new HashMap<>();
	
	//Maximum amount of binaries per char of decoded text
	private int CodeSize;
	
	//Maximum amount of random editing
	private int codeRandom;
	
	private GUI gui;
	
	public DeEnCode(GUI gui){		
		this.gui = gui;
		
		//Fill alphabet with ISO_8859_1-Charset from 0 to 256
		this.Alphabet = new char[2*2*2*2*2*2*2*2];
		for (int i=0; i<Alphabet.length; i++) {
			Alphabet[i] = (char)i;
			this.AlphanumericCode.put(this.Alphabet[i], i);
		} 
		
		//set maximum amount of binaries per char of decoded text
		this.CodeSize = Integer.toBinaryString(Alphabet.length-1).length();
	}
	
	/**
	 * encodes the input text with One-Time-Pad logic
	 * @param text the String that will be encoded
	 * @param password the String that will be used to generate the code  
	 * @return String based on Alphabet(ISO_8859_1 0 to 256) from encoded binaries
	*/
	public String encode(String text, String password){		
		//set Maximum amount of random from password
		setCodeRandom(password);
		
		//initialize encoded binary
		String binary = "";
		
		//estimated Progress
		gui.setProgress(0.01);
		
		//load decoded binaries
		String bin = "";
		for(int i = 0; i<text.length(); i++){
			bin+=getBinaryfromAlphabet(text.charAt(i));

			//addProgress
			gui.setProgress(0.01+(0.3*((double)i/(double)text.length())));
		}
		
		//generate encoding code from password
		String code = getCode(password);
		while(code.length()<bin.length()){
			code = new BigInteger("1"+code, 2).multiply(new BigInteger("1"+code, 2)).toString(2);
		}
		
		//estimated progress
		gui.setProgress(0.4);
			
		//encode with One-Time-Pad logic
		for(int i = 0; i<bin.length();i++){
			if		(bin.charAt(i) =='0' && code.charAt(i) =='0')binary+= "0";
			else if	(bin.charAt(i) =='1' && code.charAt(i) =='1')binary+= "0";
			else if	(bin.charAt(i) =='0' && code.charAt(i) =='1')binary+= "1";
			else if	(bin.charAt(i) =='1' && code.charAt(i) =='0')binary+= "1";			

			//addProgress
			gui.setProgress(0.4+(0.5*((double)i/(double)bin.length())));
		}
			
		//addProgress
		gui.setProgress(0.9);
		
		//stores encoded binaries as decimal numbers (must add a "1" so the zero's at the beginning don't get lost)
		BigInteger l = new BigInteger("1"+binary, 2);		
		String newNumber = DezToNewNumber(l,0);
		
		//end of encoding process
		gui.setProgress(1.0);
		//return the newNumber value of the encoded decimal number 
		return newNumber;
	}

	/**
	 * sets the de- or encode random settings
	 * @param password the String that will be used to generate the amount of random editing  
	*/
	public void setCodeRandom(String password) {				
		//calculates the amount of random editing for this password, which must be less then 10 
		int count = 1;
		int max = Math.abs((int) Math.pow(this.Alphabet.length-1, password.length()));
		for(int i = 0; i<password.length(); i++){
			count*=(this.Alphabet.length-1)-this.AlphanumericCode.get(password.charAt(i))+1;
		}
		double d = (double)count/(double)max;
		this.codeRandom = (int) (d*10);
		
		//calculates the maximum amount of binaries a char can produce by maximum random
		this.CodeSize=Integer.toBinaryString((int)(codeRandom*this.Alphabet.length+this.Alphabet.length-1)).length();
	}

	/**
	 * decode the input text with One-Time-Pad logic
	 * @param text the String that will be decoded
	 * @param password the String that will be used to generate the code  
	 * @return String based on Alphabet(ISO_8859_1 0 to 256) from decoded binaries
	*/
	public String decode(String text, String passwort){
		//set Maximum amount of random from password
		setCodeRandom(passwort);
		
		//initialize decoded binary
		String binary = "";

		//estimated Progress
		gui.setProgress(0.01);
		
		//store encoded text as binaries (remove first char, which was only needed to store the binary as a  decimal number)
		String zwischenspeicher = NewNumberToDez(text).toString(2);		
		String bin = zwischenspeicher.substring(1, zwischenspeicher.length());

		//generate encoding code from password
		String code = getCode(passwort);
		while(code.length()<bin.length()){
			code = new BigInteger("1"+code, 2).multiply(new BigInteger("1"+code, 2)).toString(2);
		}

		//estimated Progress
		gui.setProgress(0.1);
		
		//decode using One-Time-Pad-Logic
		for(int i = 0; i<bin.length();i++){		
			if		(bin.charAt(i) =='0' && code.charAt(i) =='0')binary += "0";
			else if	(bin.charAt(i) =='1' && code.charAt(i) =='1')binary += "0";
			else if	(bin.charAt(i) =='0' && code.charAt(i) =='1')binary += "1";
			else if	(bin.charAt(i) =='1' && code.charAt(i) =='0')binary += "1";
			

			//addProgress
			gui.setProgress(0.1+(0.8*((double)i/(double)bin.length())));
		}
			
		//initialize decoded String  
		String decode = "";
		//get decoded String from decoded binaries by going through each collection of binaries used for a char
		while(binary.length()>0){
			String zeichen = binary.substring(0, this.CodeSize);
			int zeichenCode = Integer.parseInt(zeichen,2);
			//remove random added
			while(zeichenCode>this.Alphabet.length-1){
				zeichenCode-=this.Alphabet.length;
			}
			decode+=this.Alphabet[zeichenCode];
			binary = binary.substring(this.CodeSize,binary.length());
		}

		//end of decoding process
		gui.setProgress(1.0);
		
		//return decoded String
		return decode;
	}

	/**
	 * translates a 256 based number to a 10 based number
	 * @param newNumber the 256 based number to be translated 
	 * @return BigInteger the translated 256 based number to 10 based number
	*/
	private BigInteger NewNumberToDez(String newNumber) {
		BigInteger size = new BigInteger(this.Alphabet.length+"");
		BigInteger l = new BigInteger("0");
		for(int i = 0; i<newNumber.length(); i++){
			l = l.multiply(size).add(new BigInteger(this.AlphanumericCode.get(newNumber.charAt(i))+""));
		}
		return l;
	}

	/**
	 * translates a char to binaries representing the number in the ISO_8859_1-Table
	 * @param c the char to be translated 
	 * @return String of binaries
	*/
	private String getBinaryfromAlphabet(char c) {
		//code based on 256 ACII-Table
		int code = this.AlphanumericCode.get(c);
		
		//add random
		if(codeRandom>0)code+=((int)(Math.random()*codeRandom))*Alphabet.length;
		
		//get binaries from code with the maximum amount of binaries per char under current settings 
		String binary = Integer.toBinaryString(code);
		while(binary.length()<this.CodeSize)binary="0"+binary;
		return binary;
	}

	/**
	 * calculates a unique binary from password
 	 * @param password the password to be used
	 * @return String of binaries
	*/
	private String getCode(String password) {
		long l = 0;
		for(int i = 0; i<password.length();i++){
			Long d = this.AlphanumericCode.get(password.charAt(i))*(long)this.AlphanumericCode.get(password.charAt(password.length()-(i+1)));
			l = (long)Math.sqrt(l*l+d*d);
		}
		return Long.toBinaryString(l);
	}

	/**
	 * translates a 10 based number to a 256 based number
	 * @param bi the 10 based number to be translated 
	 * @param minLnegth the minimum amount of chars used to represent the 256 based number 
	 * @return BigInteger the translated 210 based number to a 256 based number
	*/
	private String DezToNewNumber(BigInteger bi, int minLength) {
		String newNumber = "";
		while(bi.doubleValue()>0){
			BigInteger[] dr = bi.divideAndRemainder(new BigInteger(this.Alphabet.length + ""));
			bi = dr[0];
			newNumber=this.Alphabet[dr[1].intValue()]+newNumber;
		}
		while(newNumber.length()<minLength)newNumber=this.Alphabet[0]+newNumber;
		return newNumber;
	}

	/**
	 * translates from decimal number to char using ISO_8859_1 table
	 * @param i the decimal number of used as ISO_8859_1 table index 
	 * @return char at the input position in used ISO_8859_1 table
	*/
	public char getChar(int i){
		return this.Alphabet[i];
	}

	/**
	 * translates from char to a decimal using ISO_8859_1 table
	 * @param c the char of the ISO_8859_1 table
	 * @return int the position of input char in ISO_8859_1 table
	*/
	public int getInteger(char c){
		return this.AlphanumericCode.get(c);
	}
}
