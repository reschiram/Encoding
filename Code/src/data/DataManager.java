package data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import file.File;
import file.FileManager;
import data.DeEnCode;

public class DataManager {
	
	private FileManager fileManager;
	private DeEnCode deEnCode;
	
	private File data;
	
	public DataManager(DeEnCode deEnCode){
		this.deEnCode = deEnCode;
		
		this.fileManager = new FileManager(false);
		
		this.data = fileManager.createNewFile("data");
	}

	/**
	 * loads text from a file (load from UTF-8 format)
	 * @return String the text saved in the file
	 */
	public String loadText() {
		
		return loadString("Text");
	}

	/**
	 * saves Text in a file (saves as UTF-8 format)
	 * @param text the text to be saved
	 */
	public void saveText(String text) {
		//enable overwrite
		data.remove("Text");
		
		saveString(text, "Text");
	}		
	
	/**
	 * saves String at the path (encoded) in the data file
	 * @param text the text to be saved
	 * @param path the path were to save
	 */
	private void saveString(String text, String path){			
		//get line-separators chars
		String lineSeperators = System.getProperty("line.separator");
			
		int last = 0;
		for(int i = 0; i<text.length(); i++){
			//break text into lines using line-separators
			if(lineSeperators.contains(text.charAt(i)+"")){
				//ad line with line separators saved as integer value at the end 
				data.add(path, text.substring(last, i) + "->" + deEnCode.getInteger(text.charAt(i)));
				last = i+1;
			}
		}
		//if last character was not a line-separator, add last characters as last line of saved text
		if(last<text.length()-1){
			data.add(path, text.substring(last, text.length()));		
		}
			
		//save the file
		fileManager.saveFile(data);
	}
	
	/**
	 * loads String from the path (encoded) in the data file
	 * @param path the path to the String
	 * @return the entire String
	 */
	private String loadString(String path){	
		String text = "";
		
		//get all lines from the file that belongs to the saved Text
		ArrayList<String> textLines = data.get(path);
		
		if(textLines != null ){
			//go through each line
			for(int i = 0; i<textLines.size()-1; i++){
				String line = textLines.get(i);
				//remove last characters which resembles the line break and add them as the line break char
				for(int a = line.length()-1; a>0; a--){
					if(line.charAt(a) == '>' && line.charAt(a-1) == '-'){
						text += line.substring(0, a-1) + deEnCode.getChar(Integer.parseInt(line.substring(a+1)));
						a = 0;
					}
				}
			}
			//add last line (without a line break)
			text += textLines.get(textLines.size()-1);
		}
		
		//return complete saved text
		return text;
	}
	
	/**
	 * saves all files in the /data folder as encoded text in a file
	 * @param password the password used for encoding
	 */
	public void saveFolder(String password){
		//enable overwrite
		data.remove("Data");
		
		//go through every file in the /data folder
		ArrayList<java.io.File> files = fileManager.getAllFillesInFolderData("data");
		for(java.io.File file: files){
			try {
				//reads the files bytes
				byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
				
				//store the bytes as a String
				String text = new String(data, StandardCharsets.ISO_8859_1);
				
				//get the path of the file and (encoded) 
				String path = file.getPath();
				path = path.substring("./data/data/".length());
				int last = 0;
				for(int i = 0; i<path.length(); i++){
					if(path.charAt(i) == '/' || path.charAt(i) == '\\'){
						path = path.substring(0, last)+this.deEnCode.encode(path.substring(last, i), password)+"."+path.substring(i+1);
						last = i+1;
					}
				}
				path = path.substring(0, last)+this.deEnCode.encode(path.substring(last, path.lastIndexOf(".")), password)+path.substring(path.lastIndexOf("."));
				
				//get the format of the file
				String end = "";
				for(int i = path.length()-1; i>0; i--){
					if(path.charAt(i) == '.'){
						path = path.substring(0, i);
						i = 0;
					}else end=path.charAt(i) +end;
				}
				
				//save file format and file content (encoded)
				saveString(deEnCode.encode(end , password), "Data."+path+".format");
				saveString(deEnCode.encode(text, password), "Data."+path);
				
			} catch (IOException e) {
				System.out.println("Error");
			}
		}
	}

	/**
	 * loads all encoded files from the data file and stores them in the /data folder
	 * @param password the password used for decoding
	 */
	public void loadFolder(String password) {
		loadFiles("Data", password);
	}

	/**
	 * loads all encoded files from the data file and stores them in the /data folder
	 * @param key the current key in the data file
	 * @param password the password used for decoding
	 */
	private void loadFiles(String key, String password) {
		//go through all subKeys in the data file
		for(String subKey : this.data.getSubkey(key)){
			//if a file was found (must have a format)
			if(subKey.equals("format")){
				
				//get the path were the decoded data will be stored
				String path = key.substring("Data.".length());
				for(int i = 0; i<path.length(); i++){
					if(path.charAt(i)=='.')path = path.substring(0, i)+"/"+path.substring(i+1);
				}
				java.io.File file = fileManager.createFile("data/"+this.deEnCode.decode(path, password), this.deEnCode.decode(this.data.get(key+".format").get(0), password));
				
				//get decoded data
				String data = deEnCode.decode(loadString(key), password);
				try {
					//store decoded data in file
					Files.write(Paths.get(file.getAbsolutePath()), data.getBytes(StandardCharsets.ISO_8859_1));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				//goto files in subfolder
				loadFiles(key+"."+subKey, password);
			}
		}
	}
}
