/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.confluence.plugins.conluenceview.rest.exception.mapper;

import com.atlassian.confluence.plugins.conluenceview.rest.dto.GenericResponseDto;
import com.atlassian.confluence.plugins.conluenceview.rest.exception.InvalidRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidRequestExceptionMapper
implements ExceptionMapper<InvalidRequestException> {
    public Response toResponse(InvalidRequestException exception) {
        return Response.ok((Object)new GenericResponseDto.Builder().withStatus(400).withErrorMessage(exception.getMessage()).build()).build();
    }
}

