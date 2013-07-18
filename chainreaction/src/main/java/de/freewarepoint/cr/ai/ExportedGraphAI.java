package de.freewarepoint.cr.ai;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.freewarepoint.cr.CellCoordinateTuple;
import de.freewarepoint.cr.EvalField;
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
 */
public class ExportedGraphAI implements AI {

	private Game game;

	private EvalField evaluationResult;

	private Player player;
	
	private Field field;
	private Field fieldcopy;
	
	int bestXCoord;
	int bestYCoord;

	protected berechneZellenbewertung implementation;
	
	public interface berechneZellenbewertung {

		public String execute(
				final de.freewarepoint.cr.Field Spielfeld, 
				final java.lang.Integer xKoordinate,
				final java.lang.Integer yKoordinate, 
				final de.freewarepoint.cr.Player Spieler);

		public ErfolgreichReturn getErfolgreichReturn();

		public interface ErfolgreichReturn {
			public java.lang.Integer getZellenbewertung();
		}
	}
	
	
	
	public void setGraph(berechneZellenbewertung implementation) {
		this.implementation = implementation;
	}
	
	@Override
	public void doMove() {
		field = game.getField();
		player = game.getCurrentPlayer();
		fieldcopy = UtilMethods.getCopyOfField(field);
		int width = field.getWidth();
		int height = field.getHeight();
		evaluationResult = new EvalField(width, height);
		
		for (int i = 0; i < width*height; i++) {
			int x = i%width;
			int y = i/width;
			if (UtilMethods.isPlacementPossible(fieldcopy, x, y, player)) {
				// let AI graph decide how valuable placement would be
				String result = implementation.execute(fieldcopy, x, y, player);
				Integer cellValue = null;
				
				if (result.equals("erfolgreich")) {
					cellValue = implementation.getErfolgreichReturn().getZellenbewertung();
				}
				
				if (cellValue == null || cellValue < 0) {
					// in case the Erfolgreich branch has not been taken or a value <0 has been assigned
					cellValue = 0;
				}
				
				evaluationResult.setValueAt(x, y, cellValue);
			} else {
				// invalid move, forbid placement
				evaluationResult.setValueAt(x, y, -1);
			}
		}
		
		chooseBestCell();
		
		game.selectMove(bestXCoord, bestYCoord);
	}

	private void chooseBestCell() {
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
		bestXCoord = bestCells.get(0).x;
		bestYCoord = bestCells.get(0).y;
	}

	@Override
	public void setGame(Game game) {
		this.game = game;
	}

	@Override
	public String getName() {
		return "replace this string in ExportedGraphAI!";
	}
}
