package com.revolut.exceptionmapper;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Singleton;
import com.revolut.util.AppUtill;

@Provider
@Singleton
public class ViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
	
	@Context
    private static HttpServletRequest request;
	
    @Context
    private static HttpHeaders headers;


    
	public Response toResponse(ConstraintViolationException e) {
		
		MediaType mediaType = AppUtill.determineMediaTypeElseGetDefault(request.getHeader("accept"), headers.getMediaType());
		
		
		List<ObjectNode> filedLevelErrors = e.getConstraintViolations()
				.stream()
				.map(x -> this.createJsonObjNode(x.getPropertyPath().toString(), x.getMessage()))
				.collect(Collectors.toList())
				;
		
		ObjectNode jsonObj = AppUtill.createErrorJsonNode(Response.Status.CONFLICT
				 , filedLevelErrors
				 , request.getRequestURI());
		
		return Response.status(Response.Status.CONFLICT).entity(jsonObj).type(mediaType).build();
	}
	
	private  ObjectNode createJsonObjNode(String filed, String message) {
    	ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();
    	jsonNode.put("field", filed);
    	jsonNode.put("message", message);
    	return jsonNode;
    }
}