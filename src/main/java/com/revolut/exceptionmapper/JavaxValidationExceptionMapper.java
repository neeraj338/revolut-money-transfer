package com.revolut.exceptionmapper;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Singleton;
import com.revolut.util.AppUtill;

@Provider
@Singleton
public class JavaxValidationExceptionMapper implements ExceptionMapper<javax.validation.ValidationException> {
	
	
	@Context
    private static HttpServletRequest request;
	
    @Context
    private static HttpHeaders headers;

    
	public Response toResponse(javax.validation.ValidationException e) {
		
		MediaType mediaType = AppUtill.determineMediaTypeElseGetDefault(request.getHeader("accept"), headers.getMediaType());
		
		ObjectNode jsonObj = AppUtill.createErrorJsonNode(Response.Status.CONFLICT
				 , ExceptionUtils.getRootCauseMessage(e)
				 , request.getRequestURI());
		
		return Response.status(Response.Status.CONFLICT).entity(jsonObj).type(mediaType).build();
	}

}