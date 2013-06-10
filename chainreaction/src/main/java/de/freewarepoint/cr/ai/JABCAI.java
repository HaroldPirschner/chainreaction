package de.freewarepoint.cr.ai;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JFrame;

import de.freewarepoint.cr.CellCoordinateTuple;
import de.freewarepoint.cr.EvalField;
import de.freewarepoint.cr.Field;
import de.freewarepoint.cr.Game;
import de.freewarepoint.cr.Player;
import de.freewarepoint.cr.swing.UIEvalFieldDisplay;

/**
 * @author Dennis Kuehn
 */
public class JABCAI implements AI {

	private Game game;

	private int cellXCoord = 0;

	private int cellYCoord = 0;

	private EvalField evaluationResult;

	private Player playerAI;

	private Player playerOpposing;

	private Field field;

	@Override
	public void doMove() {
		ThreadLockManager.getLock().lock();
		JABCInteractionController.setAI(this);
		
		field = game.getField();
		playerAI = game.getCurrentPlayer();
		playerOpposing = playerAI == Player.SECOND ? Player.FIRST : Player.SECOND;

		try {
			// let the jABC calculate the cell values and write them into this.evaluationResult
			sleep();
	
	/// Kann ab hier auch in Game verlegt werden
			System.out.println(playerAI + ", "+ Thread.currentThread().getName());
			showEvalField();
	
			// let the jABC choose the best cell and write its coordinates into this.cellXCoord and this.cellYCoord
			// TODO: einbauen, dass per settings entschieden wird, wer das auswählen einer zelle bestimmt
			//sleep();
	
			setBestCell();
			
			// evalFiel verstecken TODO
			
			// the jABC waked the AI and has
			game.selectMove(cellXCoord, cellYCoord);
		}
		finally {
			ThreadLockManager.getLock().unlock();
		}
	}
	
	private void showEvalField() {
		final Lock lock = new ReentrantLock();
		final Condition waitForDisposal = lock.newCondition();
		final JFrame evaluation = new JFrame("Bewertung der einzelnen Zellen");
		
		evaluation.getContentPane().add(new UIEvalFieldDisplay(evaluationResult));
		addListeners(evaluation, lock, waitForDisposal);
		evaluation.setBounds(100, 100, 800, 600);
		evaluation.pack();
		evaluation.setAlwaysOnTop(true);
		evaluation.setVisible(true);
		
		lock.lock();
		try {
			waitForDisposal.await();
		}
		catch (InterruptedException e) {
			throw new IllegalStateException();
		}
		finally {
			lock.unlock();
		}
	}

	private void addListeners(final JFrame evaluation, final Lock lock, final Condition waitForDisposal) {
		evaluation.addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			
			public void windowClosing(WindowEvent e) {
				System.out.println("windowClosing");
				lock.lock();
				try {
					waitForDisposal.signalAll();
				}
				finally {
					lock.unlock();
				}
			}
		});
		
		evaluation.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_ENTER) {
					lock.lock();
					try {
						waitForDisposal.signalAll();
					}
					finally {
						lock.unlock();
					}
					System.out.println("key");
					evaluation.dispose();
				}
			}
		});
	}

	private void setBestCell() {
		List<CellCoordinateTuple> bestCells = new LinkedList<>();
		for (int x = 0; x < evaluationResult.getWidth(); x++) {
			for (int y = 0; y < evaluationResult.getHeight(); y++) {
				CellCoordinateTuple thisCell = new CellCoordinateTuple(x, y);
				
				if (bestCells.size() == 0) {
					// there is no other cell so this is the best so far
					bestCells.add(thisCell);
				} else {
					CellCoordinateTuple firstCell = bestCells.get(0);

					if (evaluationResult.getValueAt(firstCell) == evaluationResult.getValueAt(thisCell)) {
						// found another cell with as good rating
						bestCells.add(thisCell);
					}
					else if (evaluationResult.getValueAt(firstCell) < evaluationResult.getValueAt(thisCell)) {
						// found a better cell, forget every other cells
						bestCells = new LinkedList<>();
						bestCells.add(thisCell);
					}
				}
			}
		}
		Collections.shuffle(bestCells);
		setCellXCoord(bestCells.get(0).x);
		setCellYCoord(bestCells.get(0).y);
	}
	
	/**
	 * Lets the game wait until the JABC gives its okay to proceed.
	 */
	private void sleep() {
		ThreadLockManager.getJABCRunCondition().signalAll();
		try {
			ThreadLockManager.getChainreactionRunCondition().await();
		}
		catch (InterruptedException e) {
			throw new IllegalStateException();
		}
	}

	@Override
	public void setGame(Game game) {
		this.game = game;
	}

	@Override
	public String getName() {
		return "jABC AI";
	}

	public void setCellXCoord(int cellXCoord) {
		this.cellXCoord = cellXCoord;
	}

	public void setCellYCoord(int cellYCoord) {
		this.cellYCoord = cellYCoord;
	}

	public void setEvalField(EvalField eval) {
		this.evaluationResult = eval;
	}

	public Player getPlayerOpposing() {
		return playerOpposing;
	}

	public Player getPlayer() {
		return playerAI;
	}

	public Field getField() {
		return field;
	}
	
	/**
	 * Gets the current round in which the game is that this KI takes part in. Before and during the first move, a game is in round 1.
	 * 
	 * @return The current game round.
	 */
	public int getRound() {
		return game.getRound();
	}
}
