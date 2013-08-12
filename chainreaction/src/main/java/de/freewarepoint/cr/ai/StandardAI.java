package de.freewarepoint.cr.ai;

import de.freewarepoint.cr.Field;
import de.freewarepoint.cr.Game;
import de.freewarepoint.cr.Player;
import de.freewarepoint.cr.UtilMethods;

import java.util.Random;

/**
 *
 * @author maik
 */
public class StandardAI implements AI {

	private Game game;
	private UtilMethods util = new UtilMethods();

	private int[] think(Field f, Player playerAI, Player playerOpposing) {
		Random r = new Random();
		int opposingAtoms = UtilMethods.countOwnedAtoms(f, playerOpposing);
		int score = Integer.MIN_VALUE;
		int[] coords = new int[2];
		for(int x = 0; x < f.getWidth(); ++x) {
			for(int y = 0; y < f.getHeight(); ++y) {
				int cellvalue = calculateCellValue(f, x, y, playerAI, playerOpposing, opposingAtoms);
				if(cellvalue > score || (r.nextBoolean() && cellvalue >= score)) {
					score = cellvalue;
					coords[0] = x;
					coords[1] = y;
				}
			}
		}
		return coords;
	}
	
	private int calculateCellValue(Field f, int x, int y, Player playerAI, Player playerOpposing, int opposingAtoms) {
		Player owner = f.getOwnerOfCellAtPosition(x, y);
		if(owner == Player.NONE || owner == playerAI) {
			Field fieldAI = UtilMethods.getCopyOfField(f);
			UtilMethods.placeAtom(fieldAI, x, y, playerAI);
			UtilMethods.reactField(fieldAI);
			int tmp = UtilMethods.countPlayerCells(fieldAI, playerAI) + 1000; //increase by 1000 to prevent negative values
			tmp += UtilMethods.countOwnedAtoms(fieldAI, playerAI);
			tmp += opposingAtoms - UtilMethods.countOwnedAtoms(fieldAI, playerOpposing);
			tmp += UtilMethods.isCornerCell(fieldAI, x, y) ? 1 : 0;
			tmp += UtilMethods.countCriticalFieldsForPlayer(fieldAI, playerAI) * 2;
			tmp -= UtilMethods.computeDangerForCell(fieldAI, x, y, playerAI) * 4;
			tmp -= UtilMethods.countEndangeredFields(fieldAI, playerAI);
			return tmp;
		}
		return 0;
	}

	public void doMove() {
		Field field = game.getField();
		Player playerAI = game.getCurrentPlayer();
		Player playerOpposing = playerAI == Player.SECOND ? Player.FIRST : Player.SECOND;
		
		int[] coords = think(field, playerAI, playerOpposing);
		game.selectMove(coords[0], coords[1]);
	}

	@Override
	public void setGame(Game game) {
		this.game = game;
	}

	@Override
	public String getName() {
		return "Standard AI";
	}
}
