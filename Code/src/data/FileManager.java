package data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

import visulas.GUI;

public class FileManager {

	private File file_data;	
	private HashMap<String, DataFile> data_files = new HashMap<>();
	
	private String password;
	private DeEnCode deEnCode;
	
	private GUI gui;
	
	/**
	 * creates a new FileManager Object
	 * @param password the password used in this fileManager-Object
	 * @param deEnCode the deEncode-Context
	 * @param gui the gui currently used by the user
	 */
	public FileManager(String password, DeEnCode deEnCode, GUI gui){
		this.password = password;
		this.deEnCode = deEnCode;
		this.gui = gui;
		//read saved files
		this.file_data = new DataFile("./data/File_Data.data").getFile();
		LoadFiles();
	}

	/**
	 * reads saved files and stores them in the dat_files HashMap in form of DataFiles
	 */
	private void LoadFiles() {
		//remove all selectable File-Options from the GUI
		gui.getFileSelector().removeAllItems();
		try {
			int maxIntegerStringLength = (Integer.MAX_VALUE+"").length(); //the maximum length a Integer can have in a String
			int maxIntegerLength = DeEnCode.STRINGTOBYTES((Integer.MAX_VALUE+"")).length*2; // the maximum amount of bytes a encoded Integer can have
			int length = maxIntegerLength*3; // the amount of bytes used for the length-parameter of the DataFileObject (0:keyLength,1:pathLength,2:dataLength)
			//read all bytes and read DataFiles out of the data
			byte[] data = Files.readAllBytes(Paths.get(file_data.getAbsolutePath()));
			for(int i = 0; i<data.length;){
				//read length-parameter of the current DataFile
				String lengthOfFileData = DeEnCode.BYTESTOSTRING(deEnCode.decode(Arrays.copyOfRange(data, i, length+i), password));
				int keyLength  = new BigInteger(lengthOfFileData.substring(0				 		, maxIntegerStringLength	)).intValue();
				int pathLength = new BigInteger(lengthOfFileData.substring(maxIntegerStringLength  	, maxIntegerStringLength*2	)).intValue();
				int dataLength = new BigInteger(lengthOfFileData.substring(maxIntegerStringLength*2 					 		)).intValue();
				
				//read DataFile content
				String key  		= DeEnCode.BYTESTOSTRING(deEnCode.decode(Arrays.copyOfRange(data, i+length						, i+length+keyLength						), password));				
				String path 		= DeEnCode.BYTESTOSTRING(deEnCode.decode(Arrays.copyOfRange(data, i+length+keyLength			, i+length+keyLength+pathLength				), password));
				byte[] file_data 	= 						 deEnCode.decode(Arrays.copyOfRange(data, i+length+keyLength+pathLength	, i+length+keyLength+pathLength+dataLength	), password);		
				
				//save DatFile
				data_files.put(key, new DataFile(path, file_data));
				gui.getFileSelector().addItem(key);
				
				//try to read next DataFile
				i=i+length+keyLength+pathLength+dataLength;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * imports a File to the program's dataBank
	 * @param path the path where the DataFile will be linked to
	 * @param key the key representing the DataFile
	 * @throws Exception is thrown if a DataFile with the key is already known
	 */
	public void importFile(String path, String key) throws Exception{	
		//if a DataFile with the key is already known throw an Exception
		if(this.data_files.containsKey(key))throw new Exception();
		//import new DataFile to dataBase
		DataFile dataFile = new DataFile(path);
		this.data_files.put(key, dataFile);
		//save dataBase
		try {
			saveData_Files();
		} catch (IOException e) {e.printStackTrace();}
	}

	/**
	 * saves the DatFiles in the current dataBank
	 * @throws IOException
	 */
	private void saveData_Files() throws IOException {
		FileOutputStream fos = new FileOutputStream(file_data);
		//remove all selectable File-Options from the GUI
		gui.getFileSelector().removeAllItems();
		//go throw every known DataFile and save it
		for(String key: data_files.keySet()){
			//getDataFile
			DataFile dataFile = data_files.get(key);
			//add DataFileOption to User-GUI-Menu
			gui.getFileSelector().addItem(key);
			//read data which is to be saved from the DataFile
			byte[][] saveData = dataFile.getSaveData(); 
			
			//encode data
			byte[] dataBytes = deEnCode.encode(saveData[1]					, password);				
			byte[] pathBytes = deEnCode.encode(saveData[0]					, password);
			byte[] keyBytes  = deEnCode.encode(DeEnCode.STRINGTOBYTES(key)	, password);
				
			//generate length object
			String length = "";
			
			String partLength = (keyBytes.length)+"";
			while(partLength.length()<(Integer.MAX_VALUE+"").length())partLength = "0"+partLength;
			length += partLength;
			
			partLength = (pathBytes.length)+"";
			while(partLength.length()<(Integer.MAX_VALUE+"").length())partLength = "0"+partLength;
			length += partLength;
			
			partLength = (dataBytes.length)+"";
			while(partLength.length()<(Integer.MAX_VALUE+"").length())partLength = "0"+partLength;
			length += partLength;
			
			//save DataFile-Data
			fos.write(deEnCode.encode(DeEnCode.STRINGTOBYTES(length), this.password));
			fos.write(keyBytes );
			fos.write(pathBytes);
			fos.write(dataBytes);
		}
		fos.close();
	}

	/**
	 * exports the data from the DataFile specified by the key to the file which it is linked to
	 * @param key the key of the DataFile which is to be exported
	 */
	public void exportFile(String key) {
		//only if a DataFile with the key is known, export it
		if(data_files.containsKey(key)){
			DataFile dataFile = data_files.get(key);
			dataFile.export();
		}
	}

	/**
	 * removes the DataFile which is specified by the key from the dataBank
	 * @param key the key of the DataFile which is to be removed
	 */
	public void removeFile(String key) {
		//only if a DataFile with the key is known, remove it
		this.data_files.remove(key);
		try {
			saveData_Files();
		} catch (IOException e) {e.printStackTrace();}
	}
}
