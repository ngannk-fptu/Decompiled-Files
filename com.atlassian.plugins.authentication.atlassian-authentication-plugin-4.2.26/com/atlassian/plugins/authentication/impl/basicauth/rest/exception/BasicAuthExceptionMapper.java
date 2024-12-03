/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.basicauth.rest.exception;

import com.atlassian.plugins.authentication.impl.rest.model.ErrorEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class BasicAuthExceptionMapper
implements ExceptionMapper<Exception> {
    private static final Logger logger = LoggerFactory.getLogger(BasicAuthExceptionMapper.class);

    public Response toResponse(Exception exception) {
        logger.debug("Got unknown exception: [{}] with message: [{}]", exception.getClass(), (Object)exception.getMessage());
        return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)new ErrorEntity(exception.getMessage())).type("application/json").build();
    }
}

