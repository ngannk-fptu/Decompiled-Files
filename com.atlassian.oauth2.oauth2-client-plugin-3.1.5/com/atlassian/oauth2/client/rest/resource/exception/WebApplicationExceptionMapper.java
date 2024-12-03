/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.oauth2.client.rest.resource.exception;

import com.atlassian.oauth2.client.rest.resource.exception.AbstractExceptionMapper;
import com.sun.jersey.spi.resource.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@Singleton
public class WebApplicationExceptionMapper
extends AbstractExceptionMapper<WebApplicationException> {
    public Response toResponse(WebApplicationException exception) {
        return exception.getResponse();
    }
}

