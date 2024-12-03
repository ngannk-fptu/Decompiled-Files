/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.oauth2.provider.rest.exception.mapper;

import com.atlassian.oauth2.provider.rest.exception.mapper.AbstractExceptionMapper;
import com.atlassian.sal.api.message.I18nResolver;
import com.sun.jersey.spi.resource.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@Singleton
public class AnyExceptionMapper
extends AbstractExceptionMapper<Exception> {
    private final I18nResolver i18nResolver;

    public AnyExceptionMapper(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    public Response toResponse(Exception exception) {
        return this.response(Response.Status.INTERNAL_SERVER_ERROR, this.i18nResolver.getText("oauth2.rest.error.unknown"));
    }
}

