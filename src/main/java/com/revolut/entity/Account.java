package com.revolut.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.revolut.util.AppConstants;
import com.revolut.util.StringPrefixSequenceIdGenerator;

import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "account")
public class Account implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_number_seq")
    @GenericGenerator(
        name = "account_number_seq", 
        strategy = "com.revolut.util.StringPrefixSequenceIdGenerator", 
        parameters = {
            @Parameter(name = StringPrefixSequenceIdGenerator.INCREMENT_PARAM, value = "1"),
            @Parameter(name = StringPrefixSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "REVO_"),
            @Parameter(name = StringPrefixSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d") })
	@Column(name = "account_number")
	@ApiModelProperty(hidden = true)
	private String accountNumber;
	
	@NotBlank(message = AppConstants.FIELD_NOT_EMPTY_MESSAGE)
	@Column(name = "account_holder_name")
	private String accountHolderName;
	
	@NotNull(message = AppConstants.FIELD_NOT_NULL_MESSAGE)
	@Column(name = "balance")
	private BigDecimal balanceAmount;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "account_opening_date")
	private Date accountOpeningDate = new Date();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.EXTRA)
	@JsonIgnore
	private List<Transaction> transactions;
	
	public Account() {
	}

	public Account(String accountNumber, String accountHolder, BigDecimal balanceAmount) {
		super();
		this.accountNumber = accountNumber;
		this.accountHolderName = accountHolder;
		this.setBalanceAmount(balanceAmount);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
	            .append("accountNumber", this.accountNumber)
	            .append("accountHolderName", this.accountHolderName)
	            .append("balanceAmount", this.balanceAmount)
	            .append("accountOpeningDate", this.getAccountOpeningDate())
	            .toString();
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (!(obj instanceof Account)) {
	        return false;
	    }
	    Account that = (Account) obj;
	    return new EqualsBuilder().append(this.accountNumber, that.accountNumber)
	            .isEquals();
	}
	
	@Override
	public int hashCode() {
	    return new HashCodeBuilder().append(this.accountNumber).toHashCode();
	}
	
	// getter - setters
	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountHolderName() {
		return accountHolderName;
	}

	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public Date getAccountOpeningDate() {
		return accountOpeningDate;
	}

	public void setAccountOpeningDate(Date accountOpeningDate) {
		this.accountOpeningDate = accountOpeningDate;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

}