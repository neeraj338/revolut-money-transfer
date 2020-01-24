package com.revolut.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.revolut.util.AppConstants;

import io.swagger.annotations.ApiModel;

@ApiModel
public class DepositModel implements Serializable{
	
	
	private static final long serialVersionUID = 1L;

	@NotBlank(message = AppConstants.FIELD_NOT_EMPTY_MESSAGE)
	private String accountNumber;
	
	@NotNull(message = AppConstants.FIELD_NOT_NULL_MESSAGE)
	private BigDecimal amount;

	
	//getter -setter
	public String getAccountNumber() {
		return this.accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
