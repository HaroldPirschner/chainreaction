package de.freewarepoint.cr.ai;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;

import javax.swing.SwingUtilities;

import de.freewarepoint.cr.swing.UIGame;

public class JABCInteractionController {

	// FIXME: what happens if the game is shut down while the lock is still locked?

	Runnable chainreaction;
	static JABCAI currentAI;
	boolean hasTwoAIs = false;
	private static ExecutorService executer = Executors.newCachedThreadPool();
	public static Thread me;
	
	public JABCInteractionController() {
	}

	public static void shutdownChainreaction() {
		System.out.println("Shutting down Chainreaction!");
		me.interrupt();
		executer.shutdownNow();
	}

	public void startGame() {
		if (chainreaction != null) {
			throw new IllegalStateException("The game has already been started!");
		}
		chainreaction = new GameRunner();
		executer.execute(chainreaction);
		
		me = Thread.currentThread();

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
			ThreadLockManager.getLock().unlock();
			throw new ExecutionTerminatedException();
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
	
	private class ExecutionTerminatedException extends RuntimeException {
		public String toString() {
			return "Das Spiel wurde beendet.";
		}
	}
}
