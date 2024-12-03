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
import com.atlassian.confluence.plugins.conluenceview.rest.exception.CacheTokenNotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class CacheTokenNotFoundExceptionMapper
implements ExceptionMapper<CacheTokenNotFoundException> {
    public Response toResponse(CacheTokenNotFoundException exception) {
        return Response.ok((Object)new GenericResponseDto.Builder().withStatus(404).withErrorMessage("Cache token not found").build()).build();
    }
}

