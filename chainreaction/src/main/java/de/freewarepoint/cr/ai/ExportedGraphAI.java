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

	private Set<CellCoordinateTuple> bestCells;

	protected abstract String execute(Field fieldcopy, int x, int y, Player player);

	protected abstract Integer getResult();
	
	@Override
	public void doMove() {
		if (game == null) {
            throw new IllegalStateException("Current game has not been set for AI!");
        }
        final Player player = game.getCurrentPlayer();
		final Field field = game.getField();
		final int width = field.getWidth();
		final int height = field.getHeight();
		int maxEval = -1;
		bestCells.clear();

		for (int i = 0; i < width*height; i++) {
            Field fieldcopy = UtilMethods.getCopyOfField(field);
            int x = i%width;
			int y = i/width;
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
					bestCells.clear();
					bestCells.add(new CellCoordinateTuple(x, y));
					maxEval = cellValue;
				}
				else if(cellValue == maxEval) {
					bestCells.add(new CellCoordinateTuple(x, y));
				}
			}
		}

		final CellCoordinateTuple bestCell = bestCells.iterator().next();
		game.selectMove(bestCell.x, bestCell.y);
	}

	@Override
	public void setGame(Game game) {
		this.game = game;
		final Field field = game.getField();
		this.bestCells = new HashSet<CellCoordinateTuple>((int)1.5*(field.getWidth() * field.getHeight()));
	}

	@Override
	public String getName() {
		return "replace this string in ExportedGraphAI!";
	}
}
