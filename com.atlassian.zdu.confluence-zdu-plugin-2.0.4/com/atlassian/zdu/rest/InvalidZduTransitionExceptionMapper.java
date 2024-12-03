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
package com.atlassian.zdu.rest;

import com.atlassian.zdu.exception.InvalidStateTransitionException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class InvalidZduTransitionExceptionMapper
implements ExceptionMapper<InvalidStateTransitionException> {
    private static final Logger logger = LoggerFactory.getLogger(InvalidZduTransitionExceptionMapper.class);

    public Response toResponse(InvalidStateTransitionException exception) {
        logger.warn("Server error in REST", (Throwable)exception);
        return Response.status((Response.Status)Response.Status.CONFLICT).build();
    }
}

