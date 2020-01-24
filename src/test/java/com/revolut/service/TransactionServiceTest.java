package com.revolut.service;
import java.math.BigDecimal;
import java.util.List;

import javax.validation.ValidationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import org.junit.runner.JUnitCore;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.revolut.app.config.guiceconfig.GuiceModule;
import com.revolut.app.config.persistence.PersistenceModule;
import com.revolut.entity.Account;
import com.revolut.entity.Transaction;
import com.revolut.entity.Transaction.TransactionType;

public class TransactionServiceTest {
    private static final String TEST_PERSISTENCE_UNIT_NAME = "testDB";

    private static TransactionService txService;
    private AccountService accountService;
    
    private static Account account1 = null;
    private static Account account2 = null;
    private static Account account3 = null;
    public TransactionServiceTest() {
        Injector injector = createInjector();
        accountService = injector.getInstance(AccountService.class);
        txService = injector.getInstance(TransactionService.class);
    }

    private static Injector createInjector() {
        return Guice.createInjector(Stage.DEVELOPMENT
                , new GuiceModule()
                , new PersistenceModule(TEST_PERSISTENCE_UNIT_NAME));
    }
    
    
    @Before
    public void setup() {
    	account1 = accountService.save(new Account(null, "account1", new BigDecimal("30")));
    	account2 = accountService.save(new Account(null, "account2", new BigDecimal("15")));
    	account3 = accountService.save(new Account(null, "account2", new BigDecimal("10")));
    }
    
    @Test
    public void testTransactionHistoryofAccount() {
    	Account a1 = accountService.save(new Account(null, "account_A1", new BigDecimal("10")));
    	Account a2 = accountService.save(new Account(null, "account_A1", new BigDecimal("10")));
    	txService.transfer(a1.getAccountNumber()
    			, a2.getAccountNumber(), new BigDecimal("5"));
    	
    	List<Transaction> txHistoryOfA1 = txService.getAccountTransactionOrderByDateDesc(a1.getAccountNumber());
    	List<Transaction> txHistoryOfA2 = txService.getAccountTransactionOrderByDateDesc(a2.getAccountNumber());
    	
    	//verify for A1
    	Assert.assertTrue(!txHistoryOfA1.isEmpty());
    	Assert.assertEquals(1, txHistoryOfA1.size());
    	Assert.assertEquals(TransactionType.DEBIT, txHistoryOfA1.get(0).getDiscriminator());
    	Assert.assertEquals(new BigDecimal("5"), txHistoryOfA1.get(0).getAmount());
    	
    	//verify for A2
    	Assert.assertTrue(!txHistoryOfA2.isEmpty());
    	Assert.assertEquals(1, txHistoryOfA2.size());
    	Assert.assertEquals(TransactionType.CREDIT, txHistoryOfA2.get(0).getDiscriminator());
    	Assert.assertEquals(new BigDecimal("5"), txHistoryOfA2.get(0).getAmount());
    }
    
    @Test
    public void runTransferTestInParllel() {
        Class<?>[] classes = { ParallelTest1.class, ParallelTest2.class };

        // ParallelComputer(true,true) will run all classes and methods 
        // in parallel.  (First arg for classes, second arg for methods)
        JUnitCore.runClasses(new ParallelComputer(true, true), classes);
        Assert.assertEquals(new BigDecimal("30")
        		, accountService.findOne(account1.getAccountNumber()).getBalanceAmount());
        Assert.assertEquals(new BigDecimal("15")
        		, accountService.findOne(account2.getAccountNumber()).getBalanceAmount());
        Assert.assertEquals(new BigDecimal("10")
        		, accountService.findOne(account3.getAccountNumber()).getBalanceAmount());
    }
    public static class ParallelTest1 {
        @Test
        public void testAccountTransferFromA1ToA2() {
        	txService.transfer(account1.getAccountNumber()
        			, account2.getAccountNumber(), new BigDecimal("5"));
        }

        @Test
        public void testAccountTransferFromA2ToA1() {
        	txService.transfer(account2.getAccountNumber()
        			, account1.getAccountNumber(), new BigDecimal("5"));
        }
        
        @Test
        public void testWithdraw() {
        	txService.withdraw(account3.getAccountNumber(), new BigDecimal("10.00"));
        }
        
        @Test
        public void testDeposit() {
        	txService.depositTo(account3.getAccountNumber(), new BigDecimal("10.00"));
        }
        
        @Test(expected = ValidationException.class)
        public void testWithdrawInsufficientBalance() {
        	txService.withdraw(account3.getAccountNumber(), new BigDecimal("90.00"));
        }
    }
    
    public static class ParallelTest2 {
    	@Test
        public void testAccountTransferFromA1ToA2() {
        	txService.transfer(account1.getAccountNumber()
        			, account2.getAccountNumber(), new BigDecimal("5"));
        }

        @Test
        public void testAccountTransferFromA2ToA1() {
        	txService.transfer(account2.getAccountNumber()
        			, account1.getAccountNumber(), new BigDecimal("5"));
        }
        
        @Test
        public void testWithdraw() {
        	txService.withdraw(account3.getAccountNumber(), new BigDecimal("10.00"));
        }
        
        @Test
        public void testDeposit() {
        	txService.depositTo(account3.getAccountNumber(), new BigDecimal("10.00"));
        }
    }
    
    
   
}