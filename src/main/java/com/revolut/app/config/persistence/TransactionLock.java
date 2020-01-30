package com.revolut.app.config.persistence;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

@Singleton
public class TransactionLock {
	
	private static final Logger logger = LoggerFactory.getLogger(TransactionLock.class);
	
	private static Map<String, AccountLock> accountLockMap = new ConcurrentHashMap<>();
	
	public void lock(String accountNumber) {
		AccountLock accLock = accountLockMap.get(accountNumber);
		// double locking : to initialize the lock object
		if (accLock == null) {
			synchronized (this) {
				accLock = accountLockMap.get(accountNumber);
				if (accLock == null) {
					AccountLock newLock = new AccountLock(new ReentrantLock(), 0);
					accountLockMap.put(accountNumber, newLock);
					accLock = newLock;
				}
			}

		}
		synchronized (this) {
			accLock.lockCount.incrementAndGet();
			// if other concurrent tx has removed from map using unlock- up it back
			if (null == accountLockMap.get(accountNumber)) {
				accountLockMap.put(accountNumber, accLock);
			}
		}
		logger.info(" requested lock on account => {}", accountNumber);
		accLock.lock.lock();
	}
	
	
	public void unlock(String accountNumber) {
		AccountLock accLock = accountLockMap.get(accountNumber);
		if (accLock != null) {
			int lockCount = accLock.lockCount.decrementAndGet();
			accLock.lock.unlock();
			/*
			 * if count zero take lock and insure count is still zero (no one acquire lock after unlock call)
			 */
			if (lockCount == 0) {
				synchronized (this) {
					lockCount = accLock.lockCount.get();
					if (lockCount == 0) {
						accountLockMap.remove(accountNumber);
					}
				}
			}
			logger.info(" lock released for => {}", accountNumber);
		}
	}
	
	public boolean tryLock(String accountNumber) {

		AccountLock accLock = accountLockMap.get(accountNumber);
		if (accLock == null) {
			synchronized (this) {
				accLock = accountLockMap.get(accountNumber);
				if (accLock == null) {
					AccountLock newLock = new AccountLock(new ReentrantLock(), 0);
					accountLockMap.put(accountNumber, newLock);
					accLock = newLock;
				}
			}

		}

		boolean isAquired = accLock.lock.tryLock();
		if (isAquired) {
			synchronized (this) {
				accLock.lockCount.incrementAndGet();
				if (null == accountLockMap.get(accountNumber)) {
					accountLockMap.put(accountNumber, accLock);
				}
			}
		}

		return isAquired;
	}

	private static class AccountLock {
		private ReentrantLock lock;

		private AtomicInteger lockCount;

		public AccountLock(ReentrantLock lock, int initialValue) {
			this.lock = lock;
			lockCount = new AtomicInteger(initialValue);
		}
	}
}