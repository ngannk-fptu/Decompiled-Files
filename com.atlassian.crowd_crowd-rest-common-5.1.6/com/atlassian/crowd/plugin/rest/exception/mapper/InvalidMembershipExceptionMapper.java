/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.InvalidMembershipException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.crowd.plugin.rest.exception.mapper;

import com.atlassian.crowd.exception.InvalidMembershipException;
import com.atlassian.crowd.plugin.rest.entity.ErrorEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidMembershipExceptionMapper
implements ExceptionMapper<InvalidMembershipException> {
    public Response toResponse(InvalidMembershipException exception) {
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)ErrorEntity.of((Exception)exception)).build();
    }
}

