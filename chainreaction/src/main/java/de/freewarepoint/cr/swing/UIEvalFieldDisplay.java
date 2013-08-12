package de.freewarepoint.cr.swing;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.freewarepoint.cr.EvalField;
import de.freewarepoint.retrofont.RetroFont;

/**
 * @author Dennis Kuehn
 */
public class UIEvalFieldDisplay extends JPanel {
	
	private static final long serialVersionUID = -5501029406858034003L;

	public UIEvalFieldDisplay(final EvalField evalField) {
		super();
		
		setDoubleBuffered(true);
		
		this.setLayout(new GridLayout(evalField.getHeight(), evalField.getWidth(), 15, 15));

	    final RetroFont retroFont = new RetroFont();
		
	    final JLabel[][] evalFieldLabels = new JLabel[evalField.getWidth()][evalField.getHeight()];
	   
	    for (int y = 0; y < evalFieldLabels[0].length; y++) {
	    	for (int x = 0; x < evalFieldLabels.length; x++) {
	    		final JLabel label = new JLabel();
	    		label.setIcon(new ImageIcon(retroFont.getRetroString(evalField.getValueAt(x, y)+"", Color.BLACK, 32)));
	    		this.add(label);
	    		evalFieldLabels[x][y] = label;
		    }
	    }
	}
}
