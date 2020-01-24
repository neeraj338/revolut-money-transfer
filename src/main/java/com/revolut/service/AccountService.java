package com.revolut.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.revolut.entity.Account;
import com.revolut.repository.AccountRepository;

@Singleton
public class AccountService {
	
	private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
	
	@Inject
	private AccountRepository accountRepository;
	
	public Account save(Account entity) {
		if (entity.getTransactions() == null) {
			entity.setTransactions(new ArrayList<>());
		}
		Account accountWithId = accountRepository.persist(entity);
		return accountWithId;
	}
	
	public Optional<Account> findById(String pk) {
		return Optional.ofNullable(accountRepository.find(pk));
	}
	
	public Account findOne(String pk) throws EntityNotFoundException{
		Account account = accountRepository.find(pk);
		if(null == account) {
			logger.error("EntityNotFoundException :: => No account found for the primery_key [{}]", pk);
			throw new EntityNotFoundException(String.format("No entity found ! Account# %s", pk));
		}
		return account;
	}
	
	public List<Account> findAll() {
		return accountRepository.findAll();
	}
	
}