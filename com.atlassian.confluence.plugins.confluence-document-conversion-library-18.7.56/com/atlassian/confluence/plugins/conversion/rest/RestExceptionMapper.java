/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.confluence.plugins.conversion.rest;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RestExceptionMapper
implements ExceptionMapper<Exception> {
    public Response toResponse(Exception exception) {
        if (exception instanceof WebApplicationException) {
            WebApplicationException web = (WebApplicationException)exception;
            return web.getResponse();
        }
        return Response.status((int)500).type(MediaType.APPLICATION_JSON_TYPE).entity(RestExceptionMapper.createJson(exception)).build();
    }

    private static Map<String, Object> createJson(Exception exception) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("message", exception.getMessage());
        return result;
    }
}

