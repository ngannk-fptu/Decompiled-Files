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
import com.atlassian.crowd.plugin.rest.exception.DirectoryMappingConstraintException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DirectoryMappingConstraintExceptionMapper
implements ExceptionMapper<DirectoryMappingConstraintException> {
    public Response toResponse(DirectoryMappingConstraintException e) {
        return Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)new ErrorEntity(ErrorEntity.ErrorReason.of((Throwable)((Object)e)), e.getMessage())).build();
    }
}

