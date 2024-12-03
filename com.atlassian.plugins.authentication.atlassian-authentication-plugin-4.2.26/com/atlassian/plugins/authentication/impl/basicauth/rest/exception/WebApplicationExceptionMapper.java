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
package com.atlassian.plugins.authentication.impl.basicauth.rest.exception;

import com.atlassian.plugins.authentication.impl.rest.model.ErrorEntity;
import java.util.Optional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper
implements ExceptionMapper<WebApplicationException> {
    public Response toResponse(WebApplicationException exception) {
        Integer statusCode = Optional.ofNullable(exception.getResponse()).filter(response -> response.getStatus() > 0).map(Response::getStatus).orElse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        return Response.status((int)statusCode).entity((Object)new ErrorEntity(exception.getMessage())).type("application/json").build();
    }
}

