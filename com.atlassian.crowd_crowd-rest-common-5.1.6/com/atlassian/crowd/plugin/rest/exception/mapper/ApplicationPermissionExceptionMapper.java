/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ApplicationPermissionException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.crowd.plugin.rest.exception.mapper;

import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.plugin.rest.entity.ErrorEntity;
import com.atlassian.crowd.plugin.rest.exception.mapper.ExceptionMapperUtil;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ApplicationPermissionExceptionMapper
implements ExceptionMapper<ApplicationPermissionException> {
    public Response toResponse(ApplicationPermissionException exception) {
        ErrorEntity errorEntity = new ErrorEntity(ErrorEntity.ErrorReason.of((Throwable)exception), ExceptionMapperUtil.stripNonValidXMLCharacters(exception.getMessage()));
        return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)errorEntity).build();
    }
}

