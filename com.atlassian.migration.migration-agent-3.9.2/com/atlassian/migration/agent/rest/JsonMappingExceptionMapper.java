/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 *  org.codehaus.jackson.map.JsonMappingException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.migration.agent.rest.ErrorResponse;
import com.atlassian.migration.agent.rest.ErrorResponseCode;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Provider
@Component
public class JsonMappingExceptionMapper
implements ExceptionMapper<JsonMappingException> {
    private static final Logger log = LoggerFactory.getLogger(JsonMappingExceptionMapper.class);

    public Response toResponse(JsonMappingException ex) {
        log.error("Failed to process request because of unhandled error", (Throwable)ex);
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity((Object)new ErrorResponse(ErrorResponseCode.INVALID_PARAMETER, ErrorResponseCode.INVALID_PARAMETER.getMessage())).build();
    }
}

