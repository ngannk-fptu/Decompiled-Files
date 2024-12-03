/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.audit.rest.v1.validation.mapper;

import com.atlassian.audit.rest.model.ResponseErrorJson;
import com.atlassian.audit.rest.v1.validation.exception.InvalidQueryException;
import java.time.Instant;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidQueryExceptionMapper
implements ExceptionMapper<InvalidQueryException> {
    public Response toResponse(InvalidQueryException e) {
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)new ResponseErrorJson(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage(), e.getValidationResult().getErrors(), Instant.now().toString())).build();
    }
}

