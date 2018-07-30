package visulas;

import main.Main;

import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class GUI {
	
	private JFrame window;
	private JPasswordField passwordInput;
	private JTextArea text;
	private MessageBar message;
	
	private Button encode;
	private Button decode;
	private Button load;
	private Button save;
	private Button decodeData;
	private Button encodeData;
	
	private Main main;
	
	public GUI(Main main){					
		createVisuals();
		update();
		
		this.main = main;
		createFunctions();
		
		setKillAble();
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

		encodeData.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				//tasks the main thread with the save_folder task
				main.addTask("save_folder");
			}
		});

		decodeData.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				//tasks the main thread with the load_folder task
				main.addTask("load_folder");
			}
		});
		
		//Todo: add folder encoding / decoding functions
	}
	
	/**
	 * reads the password from the password field
	 * @return String the String inputed in the password field
	 */
	public String getPassword(){
		String p = new String(passwordInput.getPassword());
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
	 * displays the progress of the de- or encodetask
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
		this.window.setSize(600, 650);
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

		//create the visuals of the decode_folder-button
		this.decodeData = new Button("Decode Data");
		this.decodeData.setBounds(load.getX()+load.getWidth()+30, load.getY(), 120, 40);
		this.window.add(this.decodeData);
		
		//create the visuals of the encode_folder-button
		this.encodeData = new Button("Encode Data");
		this.encodeData.setBounds(save.getX()-(120+10*2+5), load.getY(), 120, 40);
		this.window.add(this.encodeData);
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

}
