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
	
	private static final String ACCOUNT_JOINER = "\u00B6";

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
	
	public void lock(String accountOne, String accountTwo) {
		AccountLock accLock = accountLockMap.get(getAccountLockKey(accountOne, accountTwo));
		accLock = (accLock == null ? accountLockMap.get(getAccountLockKey(accountTwo, accountOne)) : accLock);
		// double locking : to initialize the lock object
		if (accLock == null) {
			synchronized (this) {
				accLock = accountLockMap.get(getAccountLockKey(accountOne, accountTwo));
				accLock = (accLock == null ? accountLockMap.get(getAccountLockKey(accountTwo, accountOne)) : accLock);
				if (accLock == null) {
					AccountLock newLock = new AccountLock(new ReentrantLock(), 0);
					accountLockMap.put(getAccountLockKey(accountOne, accountTwo), newLock);
					accountLockMap.put(getAccountLockKey(accountTwo, accountOne), newLock);
					accLock = newLock;
				}
			}

		}
		synchronized (this) {
			accLock.lockCount.incrementAndGet();
			// if other concurrent tx has removed from map using unlock- up it back
			if (null == accountLockMap.get(getAccountLockKey(accountOne, accountTwo))
					|| null == accountLockMap.get(getAccountLockKey(accountTwo, accountOne)) ) {
				accountLockMap.put(getAccountLockKey(accountOne, accountTwo), accLock);
				accountLockMap.put(getAccountLockKey(accountTwo, accountOne), accLock);
			}
		}
		logger.info(" requested lock on accounts => {}, {}", accountOne, accountTwo);
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
	
	public void unlock(String accountOne, String accountTwo) {
		AccountLock accLock = accountLockMap.get(getAccountLockKey(accountOne, accountTwo));
		accLock =  (accLock == null ? accountLockMap.get(getAccountLockKey(accountTwo, accountOne)) : accLock );
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
						accountLockMap.remove(getAccountLockKey(accountOne, accountTwo));
						accountLockMap.remove(getAccountLockKey(accountTwo, accountOne));
					}
				}
			}
			logger.info(" lock released for => {}, {}", accountOne, accountTwo);
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

	private  String getAccountLockKey(String accountOne, String accountTwo) {
		return String.format("%s%s%s", accountOne, ACCOUNT_JOINER, accountTwo);
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