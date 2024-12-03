/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.security.jersey;

import com.atlassian.plugins.rest.common.Status;
import com.atlassian.plugins.rest.common.security.AuthorisationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class AuthorisationExceptionMapper
implements ExceptionMapper<AuthorisationException> {
    @Context
    Request request;

    @Override
    public Response toResponse(AuthorisationException exception) {
        return Status.forbidden().message(exception.getMessage()).responseBuilder().type(Status.variantFor(this.request)).build();
    }
}

