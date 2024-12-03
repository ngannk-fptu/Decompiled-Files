/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.client.api.storage.token.exception.ConfigurationNotFoundException
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.oauth2.client.rest.resource.exception;

import com.atlassian.oauth2.client.api.storage.token.exception.ConfigurationNotFoundException;
import com.atlassian.oauth2.client.rest.resource.exception.AbstractExceptionMapper;
import com.sun.jersey.spi.resource.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@Singleton
public class ConfigurationNotFoundExceptionMapper
extends AbstractExceptionMapper<ConfigurationNotFoundException> {
    public Response toResponse(ConfigurationNotFoundException exception) {
        return this.response(Response.Status.NOT_FOUND, exception.getLocalizedMessage());
    }
}

