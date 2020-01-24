package com.revolut.resource;

import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.bval.guice.Validate;
import org.hibernate.validator.constraints.NotBlank;

import com.revolut.entity.Transaction;
import com.revolut.model.AccountTransferModel;
import com.revolut.model.AccountWithdrawalModel;
import com.revolut.model.DepositModel;
import com.revolut.service.TransactionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("")
@Produces( value = {MediaType.APPLICATION_JSON})
@Api(value = "transactions", description = "Endpoint for 'account-transaction' specific operations")
public class TransactionResource {
	
	@Inject
	private TransactionService txService;

	
	@POST
    @Path("/deposits")
    @ApiOperation(value = "deposit to account", notes = "deposit to account", response = TransactionResource.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful depost of an account", response = TransactionResource.class) })
	@Validate(
            groups = { },
            validateReturnedValue = true
    )
	public Response deposit(@Valid DepositModel model) {
    	this.txService.depositTo(model.getAccountNumber(), model.getAmount());
        return Response.status(Response.Status.OK).build();
    }
	
	@POST
    @Path("/withdrawals")
    @ApiOperation(value = "withdraw amount from account", notes = "withdraw amount from account", response = TransactionResource.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful withdrawl", response = TransactionResource.class) })
	@Validate(
            groups = { },
            validateReturnedValue = true
    )
	public Response withdraw(@Valid AccountWithdrawalModel model) {
    	this.txService.withdraw(model.getAccountNumber(), model.getAmount());
        return Response.status(Response.Status.OK).build();
    }
	
	@POST
    @Path("/transfers")
    @ApiOperation(value = "account to account transfer", notes = "account to account transfer", response = TransactionResource.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful transfer", response = TransactionResource.class) })
	@Validate(
            groups = { },
            validateReturnedValue = true
    )
	public Response transfer(@Valid AccountTransferModel model) {
    	this.txService.transfer(model.getFromAccount()
    			, model.getToAccount()
    			, model.getAmount());
        return Response.status(Response.Status.OK).build();
    }
	
	@GET
    @Path("/history/{accountNumber}")
    @ApiOperation(value = "transaction hostory for accountNumber", notes = "transaction history for accountNumber", response = Transaction.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of account tx-history", response = Transaction.class) })
    public Response txHistoryForAccount(@PathParam("accountNumber") @NotBlank String accountNumber) {
    	List<Transaction> txSortedList = txService.getAccountTransactionOrderByDateDesc(accountNumber);
        return Response.status(200).entity(txSortedList).build();
    }
}
