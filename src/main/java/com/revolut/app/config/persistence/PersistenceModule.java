package com.revolut.app.config.persistence;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.revolut.entity.Account;
import com.revolut.entity.Transaction;
import com.revolut.repository.AccountRepository;
import com.revolut.repository.JpaRepository;
import com.revolut.repository.TransactionRepository;

public class PersistenceModule extends AbstractModule {
	
	private String persistenceUnitName;
	
	public PersistenceModule(String persistenceUnitName) {
		this.persistenceUnitName = persistenceUnitName;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		install(new JpaPersistModule(persistenceUnitName));
		bind(JPAInitializer.class).asEagerSingleton();
		
		bind(new TypeLiteral<JpaRepository<Account, String>>() {
		}).to(new TypeLiteral<AccountRepository>() {
		}).asEagerSingleton();
		
		bind(new TypeLiteral<JpaRepository<Transaction, String>>() {
		}).to(new TypeLiteral<TransactionRepository>() {
		}).asEagerSingleton();

	}

}