/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 *  org.springframework.stereotype.Component
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.migration.agent.rest.ErrorResponse;
import com.atlassian.migration.agent.rest.ErrorResponseCode;
import com.atlassian.migration.agent.service.impl.InvalidPlanException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.springframework.stereotype.Component;

@Provider
@Component
public class InvalidPlanExceptionMapper
implements ExceptionMapper<InvalidPlanException> {
    public Response toResponse(InvalidPlanException ex) {
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity((Object)new ErrorResponse(ErrorResponseCode.GENERIC, ex.getMessage())).build();
    }
}

