/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.upm.core.rest.resources.permission;

import com.atlassian.upm.core.rest.resources.permission.PermissionException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class PermissionExceptionMapper
implements ExceptionMapper<PermissionException> {
    public Response toResponse(PermissionException exception) {
        return Response.status((Response.Status)exception.getStatus()).entity((Object)exception.getMessage()).type("application/vnd.atl.plugins.error+json").build();
    }
}

