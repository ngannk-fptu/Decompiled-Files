/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.oauth2.client.rest.resource.exception;

import com.atlassian.oauth2.client.rest.resource.exception.AbstractExceptionMapper;
import com.atlassian.oauth2.client.rest.resource.validator.ValidationException;
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

