/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBException
 */
package com.sun.jersey.server.impl.ejb;

import com.sun.jersey.api.container.MappableContainerException;
import javax.ejb.EJBException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Providers;

public class EJBExceptionMapper
implements ExceptionMapper<EJBException> {
    private final Providers providers;

    public EJBExceptionMapper(@Context Providers providers) {
        this.providers = providers;
    }

    @Override
    public Response toResponse(EJBException exception) {
        Exception cause = exception.getCausedByException();
        if (cause != null) {
            ExceptionMapper<?> mapper = this.providers.getExceptionMapper(cause.getClass());
            if (mapper != null) {
                return mapper.toResponse(cause);
            }
            if (cause instanceof WebApplicationException) {
                return ((WebApplicationException)cause).getResponse();
            }
        }
        throw new MappableContainerException((Throwable)(cause == null ? exception : cause));
    }
}

