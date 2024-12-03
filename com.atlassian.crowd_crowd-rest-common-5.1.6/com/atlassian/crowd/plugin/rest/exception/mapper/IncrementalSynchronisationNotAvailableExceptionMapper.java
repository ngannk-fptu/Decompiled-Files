/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.event.IncrementalSynchronisationNotAvailableException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.crowd.plugin.rest.exception.mapper;

import com.atlassian.crowd.event.IncrementalSynchronisationNotAvailableException;
import com.atlassian.crowd.plugin.rest.entity.ErrorEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class IncrementalSynchronisationNotAvailableExceptionMapper
implements ExceptionMapper<IncrementalSynchronisationNotAvailableException> {
    public Response toResponse(IncrementalSynchronisationNotAvailableException exception) {
        ErrorEntity errorEntity = new ErrorEntity(ErrorEntity.ErrorReason.of((Throwable)exception), exception.getMessage());
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)errorEntity).build();
    }
}

