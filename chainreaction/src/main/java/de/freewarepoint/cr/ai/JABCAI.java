package de.freewarepoint.cr.ai;

import de.freewarepoint.cr.EvalField;
import de.freewarepoint.cr.Field;
import de.freewarepoint.cr.Game;
import de.freewarepoint.cr.Player;
import de.freewarepoint.cr.UtilMethods;

import java.util.Random;

/**
 *
 * @author maik
 */
public class JABCAI implements AI {

	private Game game;
	private UtilMethods util = new UtilMethods();
	
	private int cellXCoord;
	private int cellYCoord;
	private EvalField evaluationResult;
	
	private Thread myself;

	@Override
	public void doMove() {
		
		Field field = game.getField();
		Player playerAI = game.getCurrentPlayer();
		Player playerOpposing = playerAI == Player.SECOND ? Player.FIRST : Player.SECOND;
		
		
		// let the jABC calculate the cell values and write them into this.evaluationResult
		sleep();
		
		// TODO: display evalField
		
		// let the jABC choose the best cell and write its coordinates into this.cellXCoord and this.cellYCoord
		sleep();
		
		// the jABC waked the AI and has 
		game.selectMove(cellXCoord, cellYCoord);
	}
	
	/**
	 * Makes the thread wait until it is interrupted.
	 * 
	 * @see #wake()
	 */
	private void sleep() {
		myself = new Thread();
		try {
			myself.wait();
		}
		catch (InterruptedException e) {
			// jABC wakes AI
		}
	}
	
	/**
	 * Interrupts the thread.
	 */
	public void wake() {
		myself.interrupt();
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
}
