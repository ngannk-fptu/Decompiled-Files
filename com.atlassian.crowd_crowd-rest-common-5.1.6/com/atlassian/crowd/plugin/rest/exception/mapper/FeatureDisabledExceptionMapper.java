/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.FeatureDisabledException
 *  com.sun.jersey.api.client.ClientResponse$Status
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$StatusType
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.crowd.plugin.rest.exception.mapper;

import com.atlassian.crowd.exception.FeatureDisabledException;
import com.atlassian.crowd.plugin.rest.entity.ErrorEntity;
import com.sun.jersey.api.client.ClientResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class FeatureDisabledExceptionMapper
implements ExceptionMapper<FeatureDisabledException> {
    public Response toResponse(FeatureDisabledException e) {
        return Response.status((Response.StatusType)ClientResponse.Status.PRECONDITION_FAILED).entity((Object)ErrorEntity.of((Exception)e)).build();
    }
}

