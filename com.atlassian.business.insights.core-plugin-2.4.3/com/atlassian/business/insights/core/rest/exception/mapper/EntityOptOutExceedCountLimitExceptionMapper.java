/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.business.insights.core.rest.exception.mapper;

import com.atlassian.business.insights.core.rest.model.ErrorStatusResponse;
import com.atlassian.business.insights.core.service.exception.EntityOptOutExceedCountLimitException;
import java.time.Instant;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EntityOptOutExceedCountLimitExceptionMapper
implements ExceptionMapper<EntityOptOutExceedCountLimitException> {
    public Response toResponse(EntityOptOutExceedCountLimitException e) {
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)new ErrorStatusResponse(Response.Status.BAD_REQUEST.getStatusCode(), e.getLocalizedMessage(), null, Instant.now().toString())).build();
    }
}

