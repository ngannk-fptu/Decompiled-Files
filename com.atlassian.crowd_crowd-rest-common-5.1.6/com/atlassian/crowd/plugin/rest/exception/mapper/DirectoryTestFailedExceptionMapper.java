/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.crowd.plugin.rest.exception.mapper;

import com.atlassian.crowd.plugin.rest.entity.ErrorEntity;
import com.atlassian.crowd.plugin.rest.exception.DirectoryTestFailedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DirectoryTestFailedExceptionMapper
implements ExceptionMapper<DirectoryTestFailedException> {
    public Response toResponse(DirectoryTestFailedException e) {
        ErrorEntity errorEntity = new ErrorEntity(ErrorEntity.ErrorReason.of(e.getCause()), e.getMessage());
        return Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)errorEntity).build();
    }
}

