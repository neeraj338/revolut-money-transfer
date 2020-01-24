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

import com.revolut.entity.Account;
import com.revolut.service.AccountService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/accounts")
@Produces( value = {MediaType.APPLICATION_JSON})
@Api(value = "accounts", description = "Endpoint for 'Accounts' specific operations")
public class AccountResource {
	
	@Inject
	private AccountService accountService;

    @GET
    @Path("/{accountNumber}")
    @ApiOperation(value = "get account by accountNumber", notes = "get account by accountNumber", response = Account.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of account", response = Account.class) })
    public Response getAccountById(@PathParam("accountNumber") @NotBlank String accountNumber) {
    	Account dbAccount = accountService.findOne(accountNumber);
        return Response.status(200).entity(dbAccount).build();
    }
    
    @GET
    @Path("")
    @ApiOperation(value = "get all accounts", notes = "get all accounts", response = Account.class, responseContainer = "list")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of accounts", response = Account.class, responseContainer = "list") })
    public Response getAllAccounts() {
    	List<Account> dbAccounts = accountService.findAll();
        return Response.status(200).entity(dbAccounts).build();
    }
    
    @POST
    @Path("")
    @ApiOperation(value = "create an account", notes = "Create an account", response = Account.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful creation of an account", response = Account.class) })
    @Validate(
            groups = { },
            validateReturnedValue = true
    )
    public Response createAccount(@Valid Account account) {
    	Account dbAccount = accountService.save(account);
        return Response.status(200).entity(dbAccount).build();
    }
    
}