package com.revolut.app.config.guiceconfig;

import com.google.inject.AbstractModule;
import com.revolut.app.config.servletconfig.JacksonObjectMapperResolver;
import com.revolut.exceptionmapper.EntityNotFoundMapper;
import com.revolut.exceptionmapper.JsonParseExceptionMapper;
import com.revolut.exceptionmapper.ViolationExceptionMapper;
import com.revolut.resource.AccountResource;
import com.revolut.resource.TransactionResource;

public class GuiceModule extends AbstractModule {

	public GuiceModule() {
	}
	
	@Override
	protected void configure() {
		
		bind(AccountResource.class);
		bind(TransactionResource.class);
		
		//jackson object-mapper
		bind(JacksonObjectMapperResolver.class);
		
		//exception mapper
		bind(EntityNotFoundMapper.class);
		bind(ViolationExceptionMapper.class);
		bind(JsonParseExceptionMapper.class);
	}
	
}