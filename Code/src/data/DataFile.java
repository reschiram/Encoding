package data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class DataFile {
	
	public static String BaseSaveDirections = "./data/files/";
	public static String DataType = "data";
	
	private UUID name;
	private byte[] data;
	
	private WorkFile file_import_export;
	private WorkFile file_save;
	
	/**
	 * loads data from a file specified by its path
	 * @param path the absolute path of the file
	 */
	public DataFile(String path, UUID name){
		this.name = name;
		this.file_import_export = new WorkFile(path);
		this.file_save = new WorkFile(BaseSaveDirections+name.toString()+"."+DataType);
		//read data
		try {
			this.data = Files.readAllBytes(Paths.get(this.file_import_export.getFile().getCanonicalPath()));
		} catch (IOException e) {e.printStackTrace();}
	}
	
	/**
	 * creates a new DataFile
	 * @param path the path were the DataFile is linked to
	 * @param data the data of the DataFile (decoded/plain) 
	 */
	public DataFile(String path, byte[] data, UUID name){
		this.name = name;
		this.data = data;
		this.file_import_export = new WorkFile(path);
		this.file_save = new WorkFile(BaseSaveDirections+name.toString()+"."+DataType); 
	}
	
	/**
	 * the data to be saved
	 * @return byte[0] is byte array from path String, byte[1] is byte array representing the data
	 */
	public byte[][] getSaveData(){
		try {
			return new byte[][]{DeEnCode.STRINGTOBYTES(file_import_export.getPath()), data};
		} catch (UnsupportedEncodingException e) {e.printStackTrace();}
		return null;
	}
	
	public File getSaveFile(){
		return this.file_save.getFile();
	}
	
	/**
	 * returns the File this DataFile is linked to
	 * @return the FIle where this DataFile is linked to
	 */
	public File getImportExportFile(){
		return file_import_export.getFile();
	}

	/**
	 * saves the DataFile as plain data in the file which is linked with this DataFile
	 */
	public void export() {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(this.file_import_export.getFile());
			fos.write(this.data);
			fos.close();
		} catch (IOException e) {e.printStackTrace();}
	}

	public void setPath(String path) {
		this.file_import_export.setPath(path);
	}

	public String getPath() {
		return this.file_import_export.getPath();
	}
	
	public String getName() {
		String path = getPath();
		for(int i = path.length()-1; i>=0; i--){
			if(path.charAt(i) == '/' || path.charAt(i) == '\\'){
				return path.substring(i+1, path.length());
			}
		}
		return path;
	}

	public UUID getUUID() {
		return name;
	}
	
}
