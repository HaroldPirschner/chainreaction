package de.freewarepoint.cr.ai;

import java.util.*;

import de.freewarepoint.cr.CellCoordinateTuple;
import de.freewarepoint.cr.Field;
import de.freewarepoint.cr.Game;
import de.freewarepoint.cr.Player;
import de.freewarepoint.cr.UtilMethods;

/**
 * An AI that is used in the process of exporting a jABC KI Graph into a standalone AI jar. This AI is used to interact
 * with the generated graph class and executes the decision of cell values that the exported graph assigns.
 * 
 * @author Hauke Cziollek
 * @author Dennis Kuehn
 * @author Johannes Neubauer
 */
public abstract class ExportedGraphAI implements AI {

	private Game game;

	private int[] bestCells;
	private int bestCellsCursor;
	private Random random;
	private int width;
	private int height;

	protected abstract String execute(Field fieldcopy, int x, int y, Player player);

	protected abstract Integer getResult();
	
	@Override
	public void doMove() {
		if (game == null) {
            throw new IllegalStateException("Current game has not been set for AI!");
        }
        final Player player = game.getCurrentPlayer();
		final Field field = game.getField();

		int maxEval = -1;

		bestCellsCursor = 0;

		for (int i = 0; i < width*height; i++) {
            final Field fieldcopy = UtilMethods.getCopyOfField(field);
            final int x = i%width;
			final int y = i/width;
			if (UtilMethods.isPlacementPossible(fieldcopy, x, y, player)) {
				// let AI graph decide how valuable placement would be
				String result = execute(fieldcopy, x, y, player);
				Integer cellValue = null;
				
				if (result.equals("erfolgreich")) {
					// took the branch erfolgreich, so access the calculated cell value
					cellValue = getResult();
				}
				
				if (cellValue == null || cellValue < 0) {
					// in case the erfolgreich branch has not been taken or a value <0 has been assigned
					cellValue = 0;
				}
				
				if (cellValue > maxEval) {
					bestCellsCursor = 0;
					maxEval = cellValue;
				}
				bestCells[bestCellsCursor++] = i;
			}
		}

		final int bestCell = bestCells[random.nextInt(bestCellsCursor)];
		game.selectMove(bestCell%width, bestCell/width);
	}

	@Override
	public void setGame(Game game) {
		this.game = game;
		final Field field = game.getField();
		this.bestCells = new int[field.getWidth() * field.getHeight()];
		this.bestCellsCursor = 0;
		this.random = new Random();
		this.width = field.getWidth();
		this.height = field.getHeight();
	}

	@Override
	public String getName() {
		return "replace this string in ExportedGraphAI!";
	}
}
