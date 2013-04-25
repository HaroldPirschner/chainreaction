package de.freewarepoint.cr.ai;

import java.util.concurrent.locks.Condition;

import de.freewarepoint.cr.swing.UIGame;


public class JABCInteractionController {

	// FIXME: what happens if the game is shut down while the lock is still locked?
	
	Thread chainreaction;
	
	public JABCInteractionController() {
		chainreaction = new Thread(new GameRunner());
	}
	
	public void startGame() {
		// let the game run until it gets to a "breakpoint"
		chainreaction.start();
		
		waitForCallback();
	}

	public void wakeChainreaction() {
		// XXX reihenfolge?
		ThreadLockManager.getLock().unlock();
		ThreadLockManager.getChainreactionRunCondition().signalAll();

		waitForCallback();
	}
	
	/**
	 * Wait for 10ms and awaits a {@link Condition} that the game must notify in order to express that it has reached a
	 * breakpoint.
	 */
	private void waitForCallback() {
		// give the game enough time to lock the lock...
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) { e.printStackTrace(); }
		
		// ...after that wait until the lock is available again
		try {
			ThreadLockManager.getJABCRunCondition().await(); 
		} catch (InterruptedException e) { e.printStackTrace(); }
		
		// when it is available, lock it and return
		ThreadLockManager.getLock().lock();
	}
	
	/**
	 * @author Dennis Kuehn
	 */
	private class GameRunner implements Runnable {
		@Override
		public void run() {
			ThreadLockManager.getLock().lock();
			UIGame.main(null);
		}
	}
}

