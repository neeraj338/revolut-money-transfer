package com.revolut.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.revolut.app.config.persistence.TransactionLock;
import com.revolut.entity.Account;
import com.revolut.entity.Transaction;
import com.revolut.repository.TransactionRepository;

@Singleton
public class TransactionService {
	
	private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
	
	@Inject
	private AccountService accountService;
	
	@Inject
	private TransactionRepository txRepository;
	
	@Inject
	private TransactionLock txLock;
	
	
	public void depositTo(String accountNumber, BigDecimal amount) {
		try {
			//Acquire a lock
			txLock.lock(accountNumber);
			logger.info(" A/C Deposit, lock aquired for account => {}", accountNumber);
			Account account = accountService.findOne(accountNumber);
			
			account.setBalanceAmount(amount.add(account.getBalanceAmount()));
			Transaction tx = new Transaction(amount, Transaction.TransactionType.CREDIT, account);
			account.getTransactions().add(tx);
			txRepository.persist(tx);
		}finally {
			txLock.unlock(accountNumber);
		}
	}
	
	public void withdraw(String accountNumber, BigDecimal withdrawlAmount) {
		try {
			// Acquire a lock
			txLock.lock(accountNumber);
			logger.info(" A/C Withdrawal, lock aquired for account => {}", accountNumber);
			Account account = accountService.findOne(accountNumber);
			
			ensureSufficientBalance(account, withdrawlAmount);
			
			account.setBalanceAmount(account.getBalanceAmount().subtract(withdrawlAmount));
			Transaction tx = new Transaction(withdrawlAmount, Transaction.TransactionType.DEBIT, account);
			account.getTransactions().add(tx);
			txRepository.persist(tx);
		} finally {
			txLock.unlock(accountNumber);
		}
	}
	
	public void transfer(String fromAccount, String toAccount, BigDecimal amount) {
		try {
			/* Acquire a global lock to prevent deadlock :
			*  in case Thread1 -> transfer (a, b) and Thread2 -->transfer (b,a)
			*/
			txLock.lock(fromAccount, toAccount);
			//once global lock is acquired lock both participant accounts 
			txLock.lock(fromAccount);
			txLock.lock(toAccount);
			logger.info(" A/C Transfer, full lock aquired for accounts => {}, {} ", fromAccount, toAccount);
			List<Transaction> txListToSave = new ArrayList<>();
			Account fromAccountEntity = accountService.findOne(fromAccount);
			Account toAccountEntity = accountService.findOne(toAccount);
			
			//insure balance 
			ensureSufficientBalance(fromAccountEntity, amount);
			
			//step-1: debit
			fromAccountEntity.setBalanceAmount(fromAccountEntity.getBalanceAmount().subtract(amount));
			Transaction txDebit = new Transaction(amount, Transaction.TransactionType.DEBIT, fromAccountEntity);
			fromAccountEntity.getTransactions().add(txDebit);
			txListToSave.add(txDebit);
			
			//step-2: credit
			toAccountEntity.setBalanceAmount(amount.add(toAccountEntity.getBalanceAmount()));
			Transaction txCredit = new Transaction(amount, Transaction.TransactionType.CREDIT, toAccountEntity);
			toAccountEntity.getTransactions().add(txCredit);
			txListToSave.add(txCredit);
			
			txRepository.saveAll(txListToSave);
			
		}finally {
			txLock.unlock(toAccount);
			txLock.unlock(fromAccount);
			//unlock global at the end
			txLock.unlock(fromAccount, toAccount);
		}
		
	}
	
	public List<Transaction> getAccountTransactionOrderByDateDesc(String pk) throws EntityNotFoundException{
		Account account = accountService.findOne(pk);
		
		List<Transaction> accTransactions = account.getTransactions()
		.stream().sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
		.collect(Collectors.toList());
		return accTransactions;
	}
	
	private boolean ensureSufficientBalance(Account account
			, BigDecimal withdrawlAmount) throws ValidationException{
		
		if(account.getBalanceAmount().compareTo(withdrawlAmount) < 0) {
			String message = String.format(" A/C Insufficient fund validation error for Account No. %s, available: %s, withdrawl: %s "
					, account.getAccountNumber()
					, account.getBalanceAmount(), withdrawlAmount);
			
			logger.error(message);
			throw new ValidationException(message);
		}
		return true;
	}
}
