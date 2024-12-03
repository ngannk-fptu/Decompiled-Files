/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.ext.ExceptionMapper
 */
package com.atlassian.confluence.rest.api.exception;

import com.atlassian.confluence.rest.api.model.ExceptionConverter;
import com.atlassian.confluence.rest.api.model.RestError;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public abstract class ServiceExceptionMapper
implements ExceptionMapper<Exception> {
    public final Response toResponse(Exception exception) {
        if (exception instanceof WebApplicationException) {
            WebApplicationException web = (WebApplicationException)exception;
            return web.getResponse();
        }
        RestError errorBean = ExceptionConverter.Server.convertServiceException(exception);
        return Response.status((int)errorBean.getStatusCode()).type(MediaType.APPLICATION_JSON_TYPE).entity((Object)errorBean).build();
    }

    protected abstract void _annotateThisClassWithProvider();
}

