package de.freewarepoint.cr.swing;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.freewarepoint.cr.EvalField;
import de.freewarepoint.retrofont.RetroFont;

/**
 *
 * @author jonny
 */
public class UIEvalFieldDisplay extends JPanel {
	
	private static final long serialVersionUID = -5501029406858034003L;

	public UIEvalFieldDisplay(final EvalField evalField) {
		super();
		setDoubleBuffered(true);
		
		final BorderLayout layout = new BorderLayout(16, 16);
		this.setLayout(layout);

	    final RetroFont retroFont = new RetroFont();
		
	    final JLabel[][] evalFieldLabels = new JLabel[evalField.getWidth()][evalField.getHeight()];
		
	    for (int x = 0; x < evalFieldLabels.length; x++) {
	    	for (int y = 0; y < evalFieldLabels[0].length; y++) {
	    		final JLabel label = new JLabel();
	    		label.setIcon(new ImageIcon(retroFont.getRetroString(""+evalField.getValueAt(x, y), Color.CYAN, 32)));
	    		this.add(label, BorderLayout.NORTH);
	    		evalFieldLabels[x][y] = label;
		    }
	    }
	}
}
