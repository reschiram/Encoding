package data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DataFile {
	
	private byte[] data;
	private String path;
	private File file;
	
	/**
	 * loads data from a file specified by its path
	 * @param path the absolute path of the file
	 */
	public DataFile(String path){
		this.path = path;
		LoadFile();
		//read data
		try {
			this.data = Files.readAllBytes(Paths.get(this.file.getCanonicalPath()));
		} catch (IOException e) {e.printStackTrace();}
	}
	
	/**
	 * creates a new DataFile
	 * @param path the path were the DataFile is linked to
	 * @param data the data of the DataFile (decoded/plain) 
	 */
	public DataFile(String path, byte[] data){
		this.path = path;
		this.data = data;
		LoadFile();		
	}

	/**
	 * loads linked file
	 */
	private void LoadFile() {
		this.file = new File(path);
		//if the linked file does not exist: create it
		if(!this.file.exists()){
			try {
				//read folder path
				String folderpath = file.getCanonicalPath();
				if(folderpath.contains('\\'+""))folderpath = folderpath.substring(0, folderpath.lastIndexOf('\\'));
				else folderpath = folderpath.substring(0, folderpath.lastIndexOf('/'));
				//create folders
				new File(folderpath).mkdirs();
				//create File
				this.file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * the data to be saved
	 * @return byte[0] is byte array from path String, byte[1] is byte array representing the data
	 */
	public byte[][] getSaveData(){
		try {
			return new byte[][]{DeEnCode.STRINGTOBYTES(path), data};
		} catch (UnsupportedEncodingException e) {e.printStackTrace();}
		return null;
	}
	
	/**
	 * returns the File this DataFile is linked to
	 * @return the FIle where this DataFile is linked to
	 */
	public File getFile(){
		return this.file;
	}

	/**
	 * saves the DataFile as plain data in the file which is linked with this DataFile
	 */
	public void export() {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(this.file);
			fos.write(this.data);
			fos.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
}
