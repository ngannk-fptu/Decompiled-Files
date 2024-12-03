/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.migration.agent.dto.RequestValidationException;
import com.atlassian.migration.agent.rest.ErrorResponse;
import com.atlassian.migration.agent.rest.ErrorResponseCode;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Provider
@Component
public class RequestValidationExceptionMapper
implements ExceptionMapper<RequestValidationException> {
    private static final Logger log = LoggerFactory.getLogger(RequestValidationExceptionMapper.class);

    public Response toResponse(RequestValidationException ex) {
        log.error("Failed to process request because of invalid parameters in request", (Throwable)ex);
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity((Object)new ErrorResponse(ErrorResponseCode.INVALID_PARAMETER, ex.getMessage())).build();
    }
}

