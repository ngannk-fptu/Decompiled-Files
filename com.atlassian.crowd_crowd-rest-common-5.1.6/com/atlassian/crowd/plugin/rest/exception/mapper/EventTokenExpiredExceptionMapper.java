/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.event.EventTokenExpiredException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.crowd.plugin.rest.exception.mapper;

import com.atlassian.crowd.event.EventTokenExpiredException;
import com.atlassian.crowd.plugin.rest.entity.ErrorEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EventTokenExpiredExceptionMapper
implements ExceptionMapper<EventTokenExpiredException> {
    public Response toResponse(EventTokenExpiredException exception) {
        ErrorEntity errorEntity = new ErrorEntity(ErrorEntity.ErrorReason.of((Throwable)exception), exception.getMessage());
        return Response.status((Response.Status)Response.Status.GONE).entity((Object)errorEntity).build();
    }
}

