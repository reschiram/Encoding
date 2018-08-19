package data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DataManager {
	
	private File text_data;
	
	public DataManager(){		
		//load file where text can be saved
		this.text_data = new DataFile("./data/Text_Data.data").getFile();
	}

	/**
	 * loads byte[] from a file 
	 * @return byte[] representing a String (encoded)
	 */
	public byte[] loadText() throws Error{
		try{
			byte[] data = Files.readAllBytes(Paths.get(text_data.getAbsolutePath()));
			return data;
		}catch (IOException e) {
			e.printStackTrace();
			throw new Error();
		}
	}

	/**
	 * saves the text in the text_data File
	 * @param byte[] of text to be saved (already encoded) 
	 */
	public void saveText(byte[] text) {
		try{
			@SuppressWarnings("resource")
			FileOutputStream fos = new FileOutputStream(text_data);
			fos.write(text);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
