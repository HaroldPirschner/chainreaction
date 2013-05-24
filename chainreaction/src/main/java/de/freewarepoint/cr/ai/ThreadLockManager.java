package de.freewarepoint.cr.ai;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class ThreadLockManager {
	static ReentrantLock lock = new ReentrantLock();
	static Condition jABCMayRun = lock.newCondition();
	static Condition chainreactionMayRun = lock.newCondition();
	
	public static ReentrantLock getLock() {
		return lock;
	}
	
	public static Condition getJABCRunCondition() {
		return jABCMayRun;
	}
	
	public static Condition getChainreactionRunCondition() {
		return chainreactionMayRun;
	}
}
