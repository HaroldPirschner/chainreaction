package de.freewarepoint.cr.ai;

import java.util.concurrent.locks.Condition;

import javax.swing.SwingUtilities;

import de.freewarepoint.cr.swing.UIGame;

public class JABCInteractionController {

	// FIXME: what happens if the game is shut down while the lock is still locked?

	Thread chainreaction;
	static JABCAI currentAI;
	boolean hasTwoAIs = false;
	
	public JABCInteractionController() {
	}

	public void startGame() {
		if (chainreaction != null) {
			throw new IllegalStateException("The game has already been started!");
		}
		chainreaction = new Thread(new GameRunner());
		chainreaction.setDaemon(true);
		
		// let the game run until it gets to a "breakpoint"
		chainreaction.start();

		ThreadLockManager.getLock().lock();
		waitForCallback();
	}

	public void wakeChainreaction() {
		ThreadLockManager.getLock().lock();
		
		try {
			ThreadLockManager.getChainreactionRunCondition().signalAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		waitForCallback();
	}

	/**
	 * Awaits a {@link Condition} that the game must notify in order to express that it has reached a
	 * breakpoint.
	 */
	private void waitForCallback() {
		try {
			ThreadLockManager.getJABCRunCondition().await();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		ThreadLockManager.getLock().unlock();
	}
	
	public static void setAI(JABCAI ai) {
		currentAI = ai;
	}
	
	public static JABCAI getAIInstance() {
		return currentAI;
	}
	
	public static int getRound() {
		return currentAI.getRound();
	}
	
	public void setTwoAIMode(boolean activated) {
		if (chainreaction != null) {
			throw new IllegalStateException("The game has already been started! This modification is not allowed.");
		}
		hasTwoAIs = activated;
	}

	/**
	 * @author Dennis Kuehn
	 */
	private class GameRunner implements Runnable {
		
		@Override
		public void run() {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					new UIGame(hasTwoAIs);
				}
			});
		}
	}
}
