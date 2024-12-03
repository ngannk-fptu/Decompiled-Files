/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.application.AliasAlreadyInUseException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.crowd.plugin.rest.exception.mapper;

import com.atlassian.crowd.manager.application.AliasAlreadyInUseException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AliasAlreadyInUseExceptionMapper
implements ExceptionMapper<AliasAlreadyInUseException> {
    public Response toResponse(AliasAlreadyInUseException exception) {
        Response.ResponseBuilder responseBuilder = Response.status((Response.Status)Response.Status.CONFLICT);
        if (exception.getApplicationName() != null) {
            responseBuilder.entity((Object)exception.getApplicationName());
        }
        return responseBuilder.build();
    }
}

