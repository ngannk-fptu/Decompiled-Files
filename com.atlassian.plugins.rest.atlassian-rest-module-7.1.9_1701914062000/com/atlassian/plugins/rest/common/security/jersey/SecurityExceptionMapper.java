/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.security.jersey;

import com.atlassian.plugins.rest.common.Status;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SecurityExceptionMapper
implements ExceptionMapper<SecurityException> {
    @Context
    Request request;

    @Override
    public Response toResponse(SecurityException exception) {
        return Status.unauthorized().message(exception.getMessage()).responseBuilder().type(Status.variantFor(this.request)).build();
    }
}

