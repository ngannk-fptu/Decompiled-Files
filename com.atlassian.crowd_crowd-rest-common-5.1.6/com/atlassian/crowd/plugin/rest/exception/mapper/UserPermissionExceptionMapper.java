/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.permission.UserPermissionException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.crowd.plugin.rest.exception.mapper;

import com.atlassian.crowd.manager.permission.UserPermissionException;
import com.atlassian.crowd.plugin.rest.entity.ErrorEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UserPermissionExceptionMapper
implements ExceptionMapper<UserPermissionException> {
    public Response toResponse(UserPermissionException e) {
        return Response.status((Response.Status)Response.Status.UNAUTHORIZED).entity((Object)ErrorEntity.of((Exception)e)).build();
    }
}

