package de.freewarepoint.cr.ai;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.freewarepoint.cr.CellCoordinateTuple;
import de.freewarepoint.cr.EvalField;
import de.freewarepoint.cr.Field;
import de.freewarepoint.cr.Game;
import de.freewarepoint.cr.Player;
import de.freewarepoint.cr.swing.UIGame;

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

		// let the jABC calculate the cell values and write them into this.evaluationResult
		sleep();

/// Kann ab hier auch in Game verlegt werden
		
		UIGame.displayEvalField(evaluationResult);

		// let the jABC choose the best cell and write its coordinates into this.cellXCoord and this.cellYCoord
		//sleep();

		setBestCell();
		UIGame.hideEvalField();
		
		// the jABC waked the AI and has
		game.selectMove(cellXCoord, cellYCoord);
		
		ThreadLockManager.getLock().unlock();
	}
	
	private void setBestCell() {
		List<CellCoordinateTuple> bestCells = new LinkedList<>();
		for (int x = 0; x < evaluationResult.getWidth(); x++) {
			for (int y = 0; y < evaluationResult.getHeight(); y++) {
				CellCoordinateTuple firstCell = bestCells.get(0);
				CellCoordinateTuple thisCell = new CellCoordinateTuple(x, y);
				if (firstCell == null) {
					bestCells.add(new CellCoordinateTuple(x, y));
				} else if (evaluationResult.getValueAt(firstCell) == (evaluationResult.getValueAt(thisCell))) {
					bestCells.add(thisCell);
				} else if (evaluationResult.getValueAt(firstCell) < (evaluationResult.getValueAt(thisCell))) {
					bestCells = new LinkedList<>();
					bestCells.add(thisCell);
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
			e.printStackTrace();
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
