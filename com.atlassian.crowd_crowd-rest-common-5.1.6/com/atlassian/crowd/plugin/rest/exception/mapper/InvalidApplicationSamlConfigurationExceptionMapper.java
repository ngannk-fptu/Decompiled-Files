/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.sso.InvalidApplicationSamlConfigurationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.crowd.plugin.rest.exception.mapper;

import com.atlassian.crowd.manager.sso.InvalidApplicationSamlConfigurationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidApplicationSamlConfigurationExceptionMapper
implements ExceptionMapper<InvalidApplicationSamlConfigurationException> {
    public Response toResponse(InvalidApplicationSamlConfigurationException exception) {
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)exception.getErrors()).build();
    }
}

