/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.crowd.plugin.rest.exception.mapper;

import com.atlassian.crowd.exception.ApplicationNotFoundException;
import com.atlassian.crowd.plugin.rest.entity.ErrorEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ApplicationNotFoundExceptionMapper
implements ExceptionMapper<ApplicationNotFoundException> {
    public Response toResponse(ApplicationNotFoundException exception) {
        ErrorEntity errorEntity = new ErrorEntity(ErrorEntity.ErrorReason.of(exception), exception.getMessage());
        return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)errorEntity).build();
    }
}

