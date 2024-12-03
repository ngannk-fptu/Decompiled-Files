/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.ReadOnlyException
 *  com.atlassian.confluence.rest.api.model.ExceptionConverter$AdditionalStatus
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.core.Response$StatusType
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.createcontent.exceptions;

import com.atlassian.confluence.api.service.exceptions.ReadOnlyException;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.ResourceErrorType;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.RestTypedException;
import com.atlassian.confluence.plugins.createcontent.exceptions.ResourceException;
import com.atlassian.confluence.rest.api.model.ExceptionConverter;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class RestExceptionMapper
implements ExceptionMapper<Exception> {
    private static final Logger log = LoggerFactory.getLogger(RestExceptionMapper.class);

    public Response toResponse(Exception exception) {
        if (exception instanceof WebApplicationException) {
            log.error(exception.getMessage(), (Throwable)exception);
            return ((WebApplicationException)exception).getResponse();
        }
        if (exception instanceof RestTypedException) {
            RestTypedException restTypedException = (RestTypedException)((Object)exception);
            return ResourceException.makeResponse(exception.getMessage(), (Response.StatusType)Response.Status.BAD_REQUEST, restTypedException.getErrorType(), restTypedException.getErrorData());
        }
        if (exception instanceof ReadOnlyException) {
            return ResourceException.makeResponse(exception.getMessage(), (Response.StatusType)ExceptionConverter.AdditionalStatus.READ_ONLY_MODE_ENABLED, ResourceErrorType.ACCESS_MODE, null);
        }
        log.error(exception.getMessage(), (Throwable)exception);
        return ResourceException.makeResponse(exception.getMessage(), (Response.StatusType)Response.Status.INTERNAL_SERVER_ERROR, ResourceErrorType.UNKNOWN, null);
    }
}

