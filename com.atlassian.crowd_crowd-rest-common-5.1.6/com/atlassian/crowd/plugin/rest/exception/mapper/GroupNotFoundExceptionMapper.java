/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.crowd.plugin.rest.exception.mapper;

import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.plugin.rest.entity.ErrorEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GroupNotFoundExceptionMapper
implements ExceptionMapper<GroupNotFoundException> {
    public Response toResponse(GroupNotFoundException exception) {
        return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)ErrorEntity.of((Exception)((Object)exception))).build();
    }
}

