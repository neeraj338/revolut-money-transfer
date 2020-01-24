package com.revolut.service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.revolut.app.config.guiceconfig.GuiceModule;
import com.revolut.app.config.persistence.PersistenceModule;
import com.revolut.entity.Account;

public class AccountServiceTest {
    private static final String TEST_PERSISTENCE_UNIT_NAME = "testDB";

    private AccountService accountService;
    // private AccountRepository accountRepository;

    public AccountServiceTest() {
        Injector injector = createInjector();
        accountService = injector.getInstance(AccountService.class);
        // accountRepository = injector.getInstance(AccountRepository.class);
    }

    private static Injector createInjector() {
        return Guice.createInjector(Stage.DEVELOPMENT
                , new GuiceModule()
                , new PersistenceModule(TEST_PERSISTENCE_UNIT_NAME));
    }

    @Before
    public void setup() {
    	
    }

    @Test
    public void testCreateAccount() {
    	Account entity = new Account(null, "neeraj", new BigDecimal("10.78"));
		Account savedEntity = accountService.save(entity );
		Assert.assertNotNull(savedEntity.getAccountNumber());
    }
    
    @Test
    public void testFindAccountById() {
    	Account entity = new Account(null, "neeraj", new BigDecimal("10.78"));
		Account savedEntity = accountService.save(entity );
		Optional<Account> findById = accountService.findById(savedEntity.getAccountNumber());
		Assert.assertTrue(findById.isPresent());
		Assert.assertEquals(savedEntity, findById.get());
    }
    
    @Test
    public void testFindOneAccount() {
    	Account entity = new Account(null, "neeraj", new BigDecimal("10.78"));
		Account savedEntity = accountService.save(entity );
		Account acc = accountService.findOne(savedEntity.getAccountNumber());
		Assert.assertEquals(savedEntity, acc);
    }
    
    @Test(expected = EntityNotFoundException.class)
    public void testFindAccountEntityNotFoundException() {
    	Account entity = new Account(null, "neeraj", new BigDecimal("10.78"));
		Account savedEntity = accountService.save(entity );
		accountService.findOne(savedEntity.getAccountNumber() + "879");
    }
    
    @Test
    public void testFindAllAccount() {
    	Account entity = new Account(null, "neeraj", new BigDecimal("10.78"));
		accountService.save(entity);
		accountService.save(new Account(null, "test 2", new BigDecimal("10.78")));
		
		List<Account> accList = accountService.findAll();
		Assert.assertTrue(!accList.isEmpty());
		Assert.assertEquals(2, accList.size());
    }
}