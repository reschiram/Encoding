package visulas;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class MessageBar {

	private JLabel text;
	private JLabel progress;
	private JLabel background;
	
	/**
	 * creates a message field where the message can be displayed and progress can be shown
	 * @param x the X-Coordinate of the hint field
	 * @param y the Y-Coordinate of the hint field
	 * @param w the width of the hint field
	 * @param h the height of the hint field
	 */
	public MessageBar(int x, int y, int w, int h){
		text = new JLabel();
		text.setBounds(x, y, w, h);
		text.setForeground(Color.WHITE);
		
		progress = new JLabel();
		progress.setBounds(x, y, 1, h);
		progress.setBackground(Color.GREEN);
		
		background = new JLabel();
		background.setBounds(x, y, w, h);
		background.setBackground(Color.black);
		background.setOpaque(true);
	}
	
	/**
	 * displays the progress
	 * @param progress the progress to be displayed (must be between 0 and 1)
	 */
	public void setProgress(double progress){
		if(progress>0){
			this.progress.setOpaque(true);
			this.progress.setSize((int) ((progress*(background.getWidth()-1))+1), this.progress.getHeight());
		}else this.progress.setOpaque(false);
	}
	
	/**
	 * displays the messageBar
	 * @param window the window in which the messageBar is to be displayed
	 */
	public void show(JFrame window){
		window.add(text);
		window.add(progress);
		window.add(background);
	}

	/**
	 * displays the message
	 * @param message the message to be displayed
	 */
	public void setText(String message) {
		this.text.setText(message);
	}

	public int getY() {
		return background.getY();
	}
	
	public int getHeight(){
		return background.getHeight();
	}
	
}
