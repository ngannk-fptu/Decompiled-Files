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

import com.atlassian.oauth2.provider.rest.exception.ValidationException;
import com.atlassian.oauth2.provider.rest.exception.mapper.AbstractExceptionMapper;
import com.sun.jersey.spi.resource.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@Singleton
public class ValidationExceptionMapper
extends AbstractExceptionMapper<ValidationException> {
    public Response toResponse(ValidationException exception) {
        return this.response(Response.Status.BAD_REQUEST, exception.getErrorCollection());
    }
}

