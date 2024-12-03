/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 *  org.codehaus.jackson.JsonParseException
 */
package com.atlassian.webresource.plugin.rest.one.zero.exception;

import com.google.gson.JsonObject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.JsonParseException;

@Provider
public class JsonParseExceptionMapper
extends Throwable
implements ExceptionMapper<JsonParseException> {
    public Response toResponse(JsonParseException e) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("errorMessage", e.getMessage());
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)jsonObject.toString()).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}

