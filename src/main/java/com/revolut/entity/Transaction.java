package com.revolut.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.revolut.util.StringPrefixSequenceIdGenerator;

@Entity
@Table(name = "transaction")
public class Transaction implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static enum TransactionType{
		DEBIT,
		CREDIT,
		;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_seq")
    @GenericGenerator(
        name = "transaction_seq", 
        strategy = "com.revolut.util.StringPrefixSequenceIdGenerator", 
        parameters = {
            @Parameter(name = StringPrefixSequenceIdGenerator.INCREMENT_PARAM, value = "1"),
            @Parameter(name = StringPrefixSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "TX_"),
            @Parameter(name = StringPrefixSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d") })
	@Column(name = "transaction_number")
	private String transactionNumber;
	
	@Column(name = "amount")
	private BigDecimal amount;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "discriminator", nullable = false)
	private TransactionType discriminator;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "transaction_date")
	private Date transactionDate = new Date();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "account_number", updatable = false)
	@JsonIgnore
	private Account account;

	

	public Transaction() {
	}


	public Transaction(BigDecimal amount, TransactionType discriminator, Account account) {
		super();
		this.amount = amount;
		this.discriminator = discriminator;
		this.account = account;
	}



	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
	            .append("transactionNumber", this.transactionNumber)
	            .append("transactionDate", this.transactionDate)
	            .append("transactionAmount", this.amount)
	            .append("discriminator", this.discriminator)
	            .toString();
	}

	
	@Override
	public boolean equals(Object obj) {
	    if (!(obj instanceof Transaction)) {
	        return false;
	    }
	    Transaction that = (Transaction) obj;
	    return new EqualsBuilder().append(this.transactionNumber, that.transactionNumber)
	            .isEquals();
	}
	
	@Override
	public int hashCode() {
	    return new HashCodeBuilder().append(this.transactionNumber).toHashCode();
	}
	
	// getter - setters
	
	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public TransactionType getDiscriminator() {
		return discriminator;
	}

	public void setDiscriminator(TransactionType discriminator) {
		this.discriminator = discriminator;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
}