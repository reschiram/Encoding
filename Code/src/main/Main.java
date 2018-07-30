package main;

import java.util.Timer;
import java.util.TimerTask;

import data.DataManager;
import data.DeEnCode;
import data.Queue;
import visulas.GUI;

public class Main {
	
	public static void main(String args[]){
		new Main();
	}
	
	private DeEnCode deEnCode;
	private DataManager dataManager;
	private GUI gui;
	
	private Queue<String> tasks = new Queue<>();
	
	public Main(){		
		gui = new GUI(this);
		
		deEnCode = new DeEnCode(gui);
		dataManager = new DataManager(deEnCode);
		
		//start a tick method
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				if(!tasks.isEmpty()){
					String task = tasks.get();
					tasks.remove();
					
					if(task.equals("decode")){
						//decodes the text and displays it
						gui.setMessage("Decoding...");
						try{
							gui.setText(deEnCode.decode(gui.getText(), gui.getPassword())); 
							gui.setMessage("Decoded");
						}catch (Exception e) {
							gui.setMessage("Wrong password");
						}
					}else if(task.equals("encode")){
						//encodes the text and displays it
						gui.setMessage("Encoding...");
						try{
							gui.setText(deEnCode.encode(gui.getText(), gui.getPassword())); 
							gui.setMessage("Encoded");		
						}catch (Exception e) {
							gui.setMessage("Wrong password");
						}
					}else if(task.equals("load_text")){
						//loads the encoded text from a file and visualizes the decoded in the GUI
						gui.setMessage("Loading text...");
						try{
							gui.setText(deEnCode.decode(dataManager.loadText(), gui.getPassword()));
							gui.setMessage("Text loaded");
						}catch (Exception e) {
							gui.setMessage("Wrong password");
						}
					}else if(task.equals("save_text")){
						//saves the visual text as encoded Text in a file
						gui.setMessage("Saving text");
						dataManager.saveText(deEnCode.encode(gui.getText(), gui.getPassword()));
						gui.setMessage("Text saved");
					}else if(task.equals("save_folder")){
						//saves the files from a filer as encoded Text in a file
						gui.setMessage("Saving folder");
						dataManager.saveFolder(gui.getPassword());
						gui.setMessage("Folder saved");
					}else if(task.equals("load_folder")){
						//saves the visual text as encoded Text in a file
						gui.setMessage("Loading folder");
						dataManager.loadFolder(gui.getPassword());
						gui.setMessage("Folder loaded");
					}
					gui.setProgress(0.0);
				}
			}
			
		}, 10, 10);
	}

	/**
	 * adds a task, so the tick method works on it
	 * @param task the task the tick method should be working on. Must be "decode", "encode", "save_text", "load_text", "load_folder" or "save_folder" 
	 */
	public void addTask(String task) {
		tasks.add(task);
	}
}
