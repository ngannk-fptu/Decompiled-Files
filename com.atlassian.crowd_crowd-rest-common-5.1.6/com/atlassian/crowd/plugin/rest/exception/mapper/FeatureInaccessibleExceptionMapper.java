/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.FeatureInaccessibleException
 *  com.sun.jersey.api.client.ClientResponse$Status
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$StatusType
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.crowd.plugin.rest.exception.mapper;

import com.atlassian.crowd.exception.FeatureInaccessibleException;
import com.atlassian.crowd.plugin.rest.entity.ErrorEntity;
import com.sun.jersey.api.client.ClientResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class FeatureInaccessibleExceptionMapper
implements ExceptionMapper<FeatureInaccessibleException> {
    public Response toResponse(FeatureInaccessibleException e) {
        return Response.status((Response.StatusType)ClientResponse.Status.PRECONDITION_FAILED).entity((Object)ErrorEntity.of((Exception)e)).build();
    }
}

