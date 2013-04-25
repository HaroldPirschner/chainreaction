package de.freewarepoint.cr.ai;

import de.freewarepoint.cr.EvalField;
import de.freewarepoint.cr.Field;
import de.freewarepoint.cr.Game;
import de.freewarepoint.cr.Player;

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
		
		field = game.getField();
		playerAI = game.getCurrentPlayer();
		playerOpposing = playerAI == Player.SECOND ? Player.FIRST : Player.SECOND;
		
		
		// let the jABC calculate the cell values and write them into this.evaluationResult
		sleep();
		
		// TODO: display evalField
		
		// let the jABC choose the best cell and write its coordinates into this.cellXCoord and this.cellYCoord
		sleep();
		
		// the jABC waked the AI and has 
		game.selectMove(cellXCoord, cellYCoord);
	}
	
	/**
	 * Lets the game wait until the JABC gives its okay to proceed.
	 */
	private void sleep() {
		ThreadLockManager.getJABCRunCondition().signalAll();
		ThreadLockManager.getLock().unlock();

		try {
			Thread.sleep(10);
		} catch (InterruptedException e) { e.printStackTrace(); }
		
		try {
			ThreadLockManager.getChainreactionRunCondition().await();
		} catch (InterruptedException e) { e.printStackTrace(); }
		ThreadLockManager.getLock().lock();
	}

	public void wake() {}
	
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
}
