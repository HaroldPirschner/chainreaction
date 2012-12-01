package de.maikmerten.chainreaction.swing;

import de.maikmerten.chainreaction.AI;
import de.maikmerten.chainreaction.Game;
import de.maikmerten.chainreaction.MoveListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author maik
 */
public class UIGame implements MoveListener {

	private JFrame frame;
	private JLabel status;
	private Game game;
	private UIField uifield;
	private UISettings uisettings;
	private AI ai = new AI();

	public UIGame() {
		game = new Game(6, 5);
		frame = new JFrame("Chain reaction");
		frame.setMinimumSize(new Dimension(640, 480));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		uifield = new UIField(this, game);
		frame.add(uifield, BorderLayout.CENTER);
		status = new JLabel();
		frame.add(status, BorderLayout.SOUTH);
		uisettings = new UISettings();
		frame.add(uisettings, BorderLayout.NORTH);

		startNewGame();
		
		frame.setVisible(true);
	}

	private void startNewGame() {
		game = new Game(6,5);
		uifield.setGame(game);
		updateStatus();
	}
	

	public void onMoveSelected(int x, int y) {
	
		if(game.getWinner() != 0) {
			startNewGame();
			return;
		}
		
		game.onMoveSelected(x, y);
		updateStatus();
	
		
		if(game.getWinner() == 0 && game.getCurrentPlayer() == 2 && uisettings.againstAI()) {
			int[] aicoords = ai.thinkAI(game.getField(), (byte)2, (byte)1);
			onMoveSelected(aicoords[0], aicoords[1]);
		}
	}
	
	
	private void updateStatus() {
		if(game.getWinner() != 0) {
			status.setText("Player " + game.getWinner() + " won!");
		} else {
			status.setText("Active player: " + game.getCurrentPlayer());
		}
	}

	public static void main(String[] args) {
		UIGame g = new UIGame();
	}

}
