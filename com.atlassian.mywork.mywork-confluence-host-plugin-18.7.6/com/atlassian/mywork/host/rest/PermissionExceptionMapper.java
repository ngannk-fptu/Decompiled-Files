/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mywork.service.PermissionException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.mywork.host.rest;

import com.atlassian.mywork.service.PermissionException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class PermissionExceptionMapper
implements ExceptionMapper<PermissionException> {
    public Response toResponse(PermissionException exception) {
        return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)exception.getMessage()).build();
    }
}

