/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.rest.common.error.jersey;

import com.atlassian.plugins.rest.common.error.jersey.UncaughtExceptionEntity;
import java.util.UUID;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ThrowableExceptionMapper
implements ExceptionMapper<Throwable> {
    private static final Logger log = LoggerFactory.getLogger(ThrowableExceptionMapper.class);
    @Context
    Request request;

    @Override
    public Response toResponse(Throwable t) {
        if (t instanceof WebApplicationException) {
            WebApplicationException webEx = (WebApplicationException)t;
            if (webEx.getResponse().getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
                log.error("Server Error in REST: " + webEx.getResponse().getStatus() + ": " + webEx.getResponse(), t);
            } else {
                log.debug("REST response: {}: {}", (Object)webEx.getResponse().getStatus(), (Object)webEx.getResponse());
            }
            return webEx.getResponse();
        }
        String errorId = UUID.randomUUID().toString();
        log.error("Uncaught exception " + errorId + " thrown by REST service: " + t.getMessage(), t);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new UncaughtExceptionEntity(t, errorId)).type(UncaughtExceptionEntity.variantFor(this.request)).build();
    }
}

