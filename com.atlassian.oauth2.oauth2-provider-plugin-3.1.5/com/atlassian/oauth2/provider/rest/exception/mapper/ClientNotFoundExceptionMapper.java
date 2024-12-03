/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.oauth2.provider.rest.exception.mapper;

import com.atlassian.oauth2.provider.rest.exception.ClientNotFoundException;
import com.atlassian.oauth2.provider.rest.exception.mapper.AbstractExceptionMapper;
import com.sun.jersey.spi.resource.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@Singleton
public class ClientNotFoundExceptionMapper
extends AbstractExceptionMapper<ClientNotFoundException> {
    public Response toResponse(ClientNotFoundException exception) {
        return this.response(Response.Status.NOT_FOUND, exception.getLocalizedMessage());
    }
}

