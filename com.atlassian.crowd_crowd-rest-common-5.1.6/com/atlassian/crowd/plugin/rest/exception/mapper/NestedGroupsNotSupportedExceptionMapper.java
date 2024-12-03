/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.NestedGroupsNotSupportedException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.crowd.plugin.rest.exception.mapper;

import com.atlassian.crowd.exception.NestedGroupsNotSupportedException;
import com.atlassian.crowd.plugin.rest.entity.ErrorEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NestedGroupsNotSupportedExceptionMapper
implements ExceptionMapper<NestedGroupsNotSupportedException> {
    public Response toResponse(NestedGroupsNotSupportedException exception) {
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)ErrorEntity.of((Exception)exception)).build();
    }
}

