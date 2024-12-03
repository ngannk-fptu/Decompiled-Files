/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.permission.AuthorisationException
 *  com.google.common.collect.ImmutableMap
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.diagnostics.internal.rest;

import com.atlassian.sal.api.permission.AuthorisationException;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.spi.resource.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Singleton
public class AuthorisationExceptionMapper
implements ExceptionMapper<AuthorisationException> {
    public Response toResponse(AuthorisationException exception) {
        return Response.status((Response.Status)Response.Status.UNAUTHORIZED).entity((Object)ImmutableMap.of((Object)"exception", (Object)exception.getClass().getCanonicalName(), (Object)"message", (Object)exception.getLocalizedMessage())).type("application/json;charset=UTF-8").build();
    }
}

