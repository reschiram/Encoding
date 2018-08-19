package main;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import data.DataManager;
import data.DeEnCode;
import data.FileManager;
import data.Queue;
import visulas.GUI;

public class Main {
	
	public static void main(String args[]){
		new Main();
	}
	
	private DeEnCode deEnCode;
	private DataManager dataManager;
	private FileManager fileManager;
	private GUI gui;
	
	private Queue<String[][]> tasks = new Queue<>();
	
	public Main(){		
		gui = new GUI(this);
		
		deEnCode = new DeEnCode(gui);
		dataManager = new DataManager();
		
		//start a tick method
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				if(!tasks.isEmpty()){
					String task = tasks.get()[0][0];
					String[] args = tasks.get()[1];
					tasks.remove();
					
					if(task.equals("decode")){
						//decodes the text and displays it
						gui.setMessage("Decoding...");
						try{
							gui.setText(deEnCode.decode(gui.getText(), gui.getPassword())); 
							gui.setMessage("Decoded");
						}catch (UnsupportedEncodingException e) {
							gui.setMessage("Unsupported Text-Input detected");
						}
					}else if(task.equals("encode")){
						//encodes the text and displays it
						gui.setMessage("Encoding...");
						try{
							gui.setText(deEnCode.encode(gui.getText(), gui.getPassword())); 
							gui.setMessage("Encoded");		
						}catch (UnsupportedEncodingException e) {
							gui.setMessage("Unsupported Text-Input detected");
						}
					}else if(task.equals("load_text")){
						//loads the encoded text from a file and visualizes the decoded in the GUI
						gui.setMessage("Loading text...");
						try{
							gui.setText(DeEnCode.BYTESTOSTRING(deEnCode.decode(dataManager.loadText(), gui.getPassword())));
							gui.setMessage("Text loaded");
						}catch (UnsupportedEncodingException e) {
							gui.setMessage("Unsupported Text-Input detected");
						}
					}else if(task.equals("save_text")){
						//saves the visual text as encoded Text in a file
						gui.setMessage("Saving text");
						try {
							dataManager.saveText(DeEnCode.STRINGTOBYTES(deEnCode.encode(gui.getText(), gui.getPassword())));
						} catch (UnsupportedEncodingException e) {
							gui.setMessage("Unsupported Text-Input detected");
						}
						gui.setMessage("Text saved");
					}else if(task.equals("create_file-loader")){
						//creates a FileLoader using the currently inputed password
						gui.setMessage("loading files");
						try{
							fileManager = new FileManager(gui.getPassword(), deEnCode, gui);
							gui.setMessage("files loaded");
						}catch (Exception e) {
							gui.setMessage("Could not read Files. Can be caused by a damaged save-file or a wrong password input");
							gui.hideFileMenu();
						}
					}else if(fileManager != null){
						if(task.equals("import_file")){
							//imports a File using the key which is given by args[1] and the path which is given by args[0]
							gui.setMessage("Importing file");
							try {
								fileManager.importFile(args[0], args[1]);
								gui.setMessage("File imported");
							} catch (Exception e) {
								gui.setMessage("File: \"" + args[1] + "\" is already known. Please choose a unique key");
							}
						}else if(task.equals("export_file")){
							//exports the file using the key which is specified by args[0]
							gui.setMessage("Exporting file");
							fileManager.exportFile(args[0]);
							gui.setMessage("File exported");
						}else if(task.equals("remove_file")){
							//removes the file using the key which is specified by args[0]
							fileManager.removeFile(args[0]);
							gui.setMessage("File removed");
						}
					}
				}
				gui.setProgress(0.0);
			}
			
		}, 10, 10);
	}

	/**
	 * adds a task, so the tick method works on it
	 * @param task the task the tick method should be working on. Must be "decode", "encode", "save_text", "load_text", "create_file-loader" , "import_file", "export_file", or "remove_file" 
	 * @param the args to be given to do the task
	 */
	public void addTask(String task, String... args) {
		tasks.add(new String[][]{new String[]{task}, args});
	}
}
