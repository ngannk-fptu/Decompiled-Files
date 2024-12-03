/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.confluence.extra.masterdetail.rest;

import com.atlassian.confluence.extra.masterdetail.rest.ResourceErrorType;
import com.atlassian.confluence.extra.masterdetail.rest.ResourceException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RestExceptionMapper
implements ExceptionMapper<Exception> {
    public Response toResponse(Exception exception) {
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException)exception).getResponse();
        }
        return ResourceException.makeResponse(exception.getMessage(), Response.Status.INTERNAL_SERVER_ERROR, ResourceErrorType.UNKNOWN, null);
    }
}

