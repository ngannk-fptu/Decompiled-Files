/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.error.jersey;

import com.atlassian.plugins.rest.common.Status;
import com.sun.jersey.api.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class NotFoundExceptionMapper
implements ExceptionMapper<NotFoundException> {
    @Context
    Request request;

    @Override
    public Response toResponse(NotFoundException exception) {
        return Status.notFound().message(exception.getMessage()).responseBuilder().type(Status.variantFor(this.request)).build();
    }
}

