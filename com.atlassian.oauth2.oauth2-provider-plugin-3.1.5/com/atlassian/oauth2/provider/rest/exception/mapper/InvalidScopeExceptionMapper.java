/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.InvalidScopeException
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.oauth2.provider.rest.exception.mapper;

import com.atlassian.oauth2.provider.rest.exception.mapper.AbstractExceptionMapper;
import com.atlassian.oauth2.scopes.api.InvalidScopeException;
import com.sun.jersey.spi.resource.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@Singleton
public class InvalidScopeExceptionMapper
extends AbstractExceptionMapper<InvalidScopeException> {
    public Response toResponse(InvalidScopeException exception) {
        return this.response(Response.Status.BAD_REQUEST, "invalid scope", exception.getMessage());
    }
}

