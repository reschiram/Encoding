package visulas;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

public class InputHint {
	
	private JLabel label;

	/**
	 * creates a hint field which disappears after first click
	 * @param text the String to be displays
	 * @param x the X-Coordinate of the hint field
	 * @param y the Y-Coordinate of the hint field
	 * @param w the width of the hint field
	 * @param h the height of the hint field
	 */
	public InputHint(String text, int x, int y, int w, int h){
		//create visuals
		label = new JLabel(text);
		label.setBounds(x, y+5, w, h);
		label.setForeground(Color.GRAY);
		
		//enable the label to disappear after first click
		label.addMouseListener(new MouseListener() {			
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				Point p = e.getPoint();
				p.y-=5;
				if(label.contains(p)){
					label.setText("");
					label.removeMouseListener(this);
				}
			}
			
		});
	}
	
	/**
	 * the graphic component used for the hint display
	 * @return JLabel
	 */
	public JLabel getGraphics(){
		return label;
	}
}
