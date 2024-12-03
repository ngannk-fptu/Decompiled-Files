/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.plugins.authentication.impl.rest.exception;

import com.atlassian.plugins.authentication.impl.rest.model.ErrorEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper
implements ExceptionMapper<IllegalArgumentException> {
    public Response toResponse(IllegalArgumentException exception) {
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)new ErrorEntity(exception.getMessage())).build();
    }
}

