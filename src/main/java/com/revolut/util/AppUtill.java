package com.revolut.util;

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AppUtill {
	
	public static ObjectNode createErrorJsonNode(Response.Status status, String message, String uri) {
		ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();
		jsonNode.put("statusCode",status.getStatusCode());
		jsonNode.put("reasonPhrase", status.getReasonPhrase());
		jsonNode.put("uri", uri);
		jsonNode.put("message", message);
        
        return jsonNode;
	}
	
	
	public static ObjectNode createErrorJsonNode(Response.Status status, List<ObjectNode> messages, String uri) {
		ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();
		jsonNode.put("statusCode",status.getStatusCode());
		jsonNode.put("reasonPhrase", status.getReasonPhrase());
		jsonNode.put("uri", uri);
		ArrayNode arrayJsonNode = jsonNode.putArray("messages");
		messages.stream().forEach(x->arrayJsonNode.add(x));
        
        return jsonNode;
	}
	
	public static MediaType determineMediaTypeElseGetDefault(String acceptHeader, MediaType headerMediaType) {
		
		MediaType  mediaType = MediaType.APPLICATION_JSON_TYPE; //default
		
        if (MediaType.APPLICATION_XML.equals(acceptHeader)) {
            mediaType = MediaType.APPLICATION_XML_TYPE;
        }else {

            if (headerMediaType != null) {
                mediaType = headerMediaType;
            }
        }
        return mediaType;
	}
}
