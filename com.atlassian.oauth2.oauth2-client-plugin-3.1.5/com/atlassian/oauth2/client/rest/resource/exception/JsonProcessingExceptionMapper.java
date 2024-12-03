/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.Provider
 *  org.codehaus.jackson.JsonProcessingException
 */
package com.atlassian.oauth2.client.rest.resource.exception;

import com.atlassian.oauth2.client.rest.resource.exception.AbstractExceptionMapper;
import com.sun.jersey.spi.resource.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.JsonProcessingException;

@Provider
@Singleton
public class JsonProcessingExceptionMapper
extends AbstractExceptionMapper<JsonProcessingException> {
    public Response toResponse(JsonProcessingException exception) {
        return this.response(Response.Status.BAD_REQUEST, exception.getLocalizedMessage());
    }
}

