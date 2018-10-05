package data;

import java.io.File;
import java.io.IOException;

public class WorkFile {

	private String path;
	private File file;
	
	public WorkFile(String path){
		this.path = path;
	}
	
	public File getFile(){
		if(file==null || !file.exists()){
			this.file = new File(this.path);
			try {
				//read folder path
				String folderpath = this.file.getCanonicalPath();
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
		return file;
	}
	
	public void setPath(String path){
		this.path = path;
		this.file = null;
	}

	public String getPath() {
		return this.path;
	}
}
