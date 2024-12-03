/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 *  org.codehaus.jackson.map.JsonMappingException
 */
package com.atlassian.webresource.plugin.rest.two.zero.exception;

import com.google.gson.JsonObject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.map.JsonMappingException;

@Provider
public class JsonMappingExceptionMapper
extends Throwable
implements ExceptionMapper<JsonMappingException> {
    public Response toResponse(JsonMappingException e) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("errorMessage", e.getMessage());
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)jsonObject.toString()).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}

