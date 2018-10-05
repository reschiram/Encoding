package visulas;

import main.Main;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import data.DataFile;

public class GUI {
	
	public static Font DEFAULT_FONT = new Font("TimesNewRoman", Font.BOLD, 20);
	
	private JFrame window;
	private JPasswordField passwordInput;
	private JTextArea text;
	private MessageBar message;
	
	private Button encode;
	private Button decode;
	private Button load;
	private Button save;
	private Button openFileManager;
	
	private Button exportFile;
	private Button importFile;
	private Button removeFile;
	private Button changePath;
	private Button browsePath;
	private JTextField keyInput;
	private JTextField path;
	private JComboBox<String> fileSlection;
	
	private Main main;
	
	private Dimension size = new Dimension(1050, 550);
	
	public GUI(Main main){					
		createVisuals();
		update();
		
		this.main = main;
		createFunctions();
		
		setKillAble();
		update();
	}

	/**
	 * updates the window graphics
	 */
	private void update() {
		this.window.setSize(this.window.getWidth()-1, this.window.getHeight());
		this.window.setSize(this.window.getWidth()+1, this.window.getHeight());
	}

	/**
	 * loads functions for buttons used in the GUI
	 */
	private void createFunctions() {
		
		decode.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				//tasks the main thread with the decode task
				main.addTask("decode");
			}
		});
		
		encode.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				//tasks the main thread with the decode task	
				main.addTask("encode");	
			}
		});
		
		load.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				//tasks the main thread with the load_text task
				main.addTask("load_text");
			}
		});

		save.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				//tasks the main thread with the save_text task
				main.addTask("save_text");
			}
		});		
		
		openFileManager.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//tasks the main thread to load all saved files
				main.addTask("create_file-loader");
				//shows the fileManager visuals
				if(size.getHeight()==550.0)showFileMenu();
			}
		});
		
		importFile.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				//if no key is specified do nothing
				if(keyInput.getText().length()==0)return;
				//create FileSelector-Window
				try {
					   UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}catch (Exception ea) {System.out.println(ea);} 
				JFileChooser fc = new JFileChooser();
				fc.showOpenDialog(window);
				File f = fc.getSelectedFile();
				//only if selected file exists and it is a file
				if(f!=null && f.exists() && f.isFile()){
					//tasks the main thread with an import of the file
					main.addTask("import_file", f.getAbsolutePath(), keyInput.getText());
				}
			}
		});
		
		exportFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//if a key is selected tasks the main thread with the export of the file
				if(fileSlection.getSelectedItem()!=null){
					main.addTask("export_file", (String) fileSlection.getSelectedItem());
				}
			}
		});
		
		removeFile.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				//if a key is selected tasks the main thread with the removal of the file from the dataBank
				if(fileSlection.getSelectedItem()!=null){
					path.setText("");
					if(fileSlection.getItemCount()==1)path.setEnabled(false);
					main.addTask("remove_file", (String) fileSlection.getSelectedItem());
				}
			}
		});

		//enable resize
		window.addComponentListener(new ComponentListener() {
			
			private HashMap<Component, int[]> componentData = new HashMap<>();
			
			public void componentShown(ComponentEvent e) {}
			public void componentMoved(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
			
			@Override
			public void componentResized(ComponentEvent e) {
				if(componentData.isEmpty()){
					double w = size.getWidth()/600;
					double h = size.getHeight()/650;
					for(Component comp: window.getContentPane().getComponents()){
						comp.setLocation((int)(comp.getX()*w), (int) (comp.getY()*h));
						comp.setSize((int)(comp.getWidth()*w), (int) (comp.getHeight()*h));
						comp.setFont(DEFAULT_FONT);
						componentData.put(comp, new int[]{comp.getX(), comp.getY(), comp.getWidth(), comp.getHeight()});
					}
					return;
				}
				double w = e.getComponent().getWidth()/size.getWidth();
				double h = e.getComponent().getHeight()/size.getHeight();
				for(Component comp: window.getContentPane().getComponents()){
					int[] data = componentData.get(comp);
					comp.setLocation((int)(data[0]*w), (int) (data[1]*h));
					comp.setSize((int)(data[2]*w), (int) (data[3]*h));
				}
			}
		});
		
		fileSlection.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				DataFile file = main.getFile((String) fileSlection.getSelectedItem());
				if(file==null) return;
				path.setText(file.getPath());
				path.setEnabled(true);
			}
		});
		
		browsePath.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//if no file is selected, do nothing
				if(fileSlection.getSelectedItem()==null)return;
				
				DataFile file = main.getFile((String) fileSlection.getSelectedItem());
				
				//create FileSelector-Window
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}catch (Exception ea) {System.out.println(ea);} 
				JFileChooser fc = new JFileChooser(file.getPath());
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				fc.showOpenDialog(window);
				
				File f = fc.getSelectedFile();
				//only if selected file or folder exists
				if(f!=null){
					String p = f.getAbsolutePath();
					if(f.isFile()){
						p = f.getParent();
					}
					path.setText(p+"\\"+file.getName());
				}
			}
		});
		
		changePath.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//if no file is selected, do nothing
				if(fileSlection.getSelectedItem()==null)return;
				
				//task main with change of filePath
				main.addTask("change_file_path", (String) fileSlection.getSelectedItem(), path.getText());
			}
		});
	}
	
	/**
	 * reads the password from the password field
	 * @return String the String inputed in the password field
	 */
	public String getPassword(){
		String p = "p"+new String(passwordInput.getPassword());
		return p;
	}
	
	/**
	 * reads the visual text
	 * @return the text displayed
	 */
	public String getText(){
		return this.text.getText();
	}
	
	/**
	 * displays the text
	 * @param text the text to be displayed
	 */
	public void setText(String text){
		this.text.setText(text);
	}
	
	/**
	 * displays the message
	 * @param message the message to be displayed
	 */
	public void setMessage(String message){
		this.message.setText(message);
	}
	

	/**
	 * displays the progress of the de- or encode-task
	 * @param the progress to be displayed
	 */
	public void setProgress(double progress){
		this.message.setProgress(progress);
	}
	
	/**
	 * creates visuals for the GUI	
	 */
	private void createVisuals() {
		//create window
		this.window = new JFrame();
		this.window.setSize(this.size);
		this.window.setLayout(null);
		this.window.setVisible(true);
		
		//create password input
		this.passwordInput = new JPasswordField();
		this.passwordInput.setBounds(100, 10, 384, 40);
		this.window.add(new InputHint("Enter Password", passwordInput.getX()+5, passwordInput.getY(), passwordInput.getWidth(), passwordInput.getHeight()).getGraphics());
		this.window.add(this.passwordInput);
		
		//create visuals for messages from the program
		this.message = new MessageBar(10, 550, 565, 40);
		this.message.show(this.window);
		
		//create visuals for text input and text visualizations 
		this.text = new JTextArea();
		this.text.setBounds(10, 70, 564, 400);

		//create ScrollPane to enable the ability to scroll in the text editor
		JScrollPane scroll = new JScrollPane(text);
		scroll.setBounds(10, 70, 564, 400);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.window.add(scroll);
		
		//create the visuals of the encode-button
		this.encode = new Button("Encode");
		this.encode.setBounds(10, 10, 80, 40);
		this.window.add(this.encode);

		//create the visuals of the decode-button
		this.decode = new Button("Decode");
		this.decode.setBounds(passwordInput.getX()+passwordInput.getWidth()+10, 10, 80, 40);
		this.window.add(this.decode);

		//create the visuals of the load_text-button
		this.load = new Button("Load / Import");
		this.load.setBounds(10, text.getY()+text.getHeight()+20, 120, 40);
		this.window.add(this.load);

		//create the visuals of the save_text-button
		this.save = new Button("Save / Export");
		this.save.setBounds(600-(120+10*2+5), load.getY(), 120, 40);
		this.window.add(this.save);
		
		//creates Visuals for open-File-Manager-Button
		this.openFileManager = new Button("Open File Manager (with current password)");
		this.openFileManager.setLocation(load.getWidth()+load.getX()+10, this.load.getY());
		this.openFileManager.setSize(save.getX()-openFileManager.getX()-10, 40);
		this.window.add(this.openFileManager);
		
		//<--creates FileManager-Visuals-->
		
		//creates Visuals for file-import-Button
		this.importFile = new Button("Import File");
		this.importFile.setBounds(10, message.getY()+message.getHeight()+20, 150, 40);
		this.window.add(this.importFile);

		//creates Visuals for file-export-Button
		this.exportFile = new Button("Export File");
		this.exportFile.setBounds(importFile.getWidth()+importFile.getX()+30, importFile.getY(), 120, 40);
		this.window.add(this.exportFile);

		//creates Visuals for file-remove-Button
		this.removeFile = new Button("Remove File");
		this.removeFile.setBounds(exportFile.getX()+exportFile.getWidth()+10, importFile.getY(), 120, 40);
		this.window.add(this.removeFile);
		
		this.changePath = new Button("Change Path");
		this.changePath.setFont(DEFAULT_FONT);
		this.changePath.setBounds(removeFile.getX()+removeFile.getWidth()+10, importFile.getY(), 120, 40);
		this.window.add(changePath);
		
		//creates Visuals for keyInput
		this.keyInput = new JTextField();
		this.keyInput.setOpaque(true);
		this.keyInput.setBackground(Color.white);
		this.keyInput.setSize(100, 40);
		this.keyInput.setFont(DEFAULT_FONT);
		this.keyInput.setLocation(importFile.getX()+(importFile.getWidth()-keyInput.getWidth())/2, importFile.getY()+importFile.getHeight()+10);
		this.window.add(new InputHint("Enter File Key", keyInput.getX()+5, keyInput.getY(), keyInput.getWidth(), keyInput.getHeight()).getGraphics());
		this.window.add(keyInput);
		
		//creates Visuals for FileSelector
		this.fileSlection = new JComboBox<>();
		this.fileSlection.setSize(100, 40);
		this.fileSlection.setFont(DEFAULT_FONT);
		this.fileSlection.setLocation(exportFile.getX(), exportFile.getY()+exportFile.getHeight()+10);
		this.window.add(fileSlection);
		
		this.path = new JTextField();
		this.path.setEnabled(false);
		this.path.setOpaque(true);
		this.path.setBackground(Color.white);
		this.path.setFont(DEFAULT_FONT);
		this.path.setSize(200, 40);
		this.path.setLocation(fileSlection.getX()+fileSlection.getWidth()+10, fileSlection.getY());
		this.path.setForeground(Color.black);
		this.window.add(path);
		
		this.browsePath = new Button("Browse");
		this.browsePath.setFont(DEFAULT_FONT);
		this.browsePath.setBounds(path.getX()+path.getWidth()+10, fileSlection.getY(), 60, 40);
		this.window.add(browsePath);
	}


	/**
	 * enables the program to kill every thread ones the window is closed
	 */
	private void setKillAble(){
		this.window.addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
			public void windowClosed(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
		});
	}

	/**
	 * returns the JComboBox which is used as a fileSlector
	 * @return the JComboBox which is used as a fileSlector
	 */
	public JComboBox<String> getFileSelector() {
		return fileSlection;
	}

	/**
	 * hides the fileMenu and resizes the Window
	 */
	public void hideFileMenu() {		
		//set default_size
		this.size = new Dimension(1050, 550);
		
		//translate size difference to current scale
		int heigth = (int) ((window.getHeight()/650.0)*100);
		this.window.setSize(window.getWidth(), window.getHeight()-heigth);
		
		//hide FileMenu
		this.importFile		.setVisible(false);
		this.exportFile		.setVisible(false);
		this.keyInput		.setVisible(false);
		this.fileSlection	.setVisible(false);
		this.removeFile		.setVisible(false);
		this.path			.setVisible(false);
		this.changePath		.setVisible(false);
		this.browsePath		.setVisible(false);
	}

	/**
	 * shows the fileMenu and resizes the Window
	 */
	public void showFileMenu(){		
		//set default_size
		this.size = new Dimension(1050, 650);

		//translate size difference to current scale
		int heigth = (int) ((window.getHeight()/550.0)*100);
		this.window.setSize(window.getWidth(), window.getHeight()+heigth);

		//shows FileMenu
		this.importFile		.setVisible(true);
		this.exportFile		.setVisible(true);
		this.keyInput		.setVisible(true);
		this.fileSlection	.setVisible(true);
		this.removeFile		.setVisible(true);
		this.path			.setVisible(true);
		this.changePath		.setVisible(true);
		this.browsePath		.setVisible(true);
	}

}
