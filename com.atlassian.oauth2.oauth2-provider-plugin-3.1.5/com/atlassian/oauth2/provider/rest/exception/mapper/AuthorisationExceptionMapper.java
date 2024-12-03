/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AuthorisationException
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.oauth2.provider.rest.exception.mapper;

import com.atlassian.oauth2.provider.rest.exception.mapper.AbstractExceptionMapper;
import com.atlassian.plugins.rest.common.security.AuthorisationException;
import com.atlassian.sal.api.message.I18nResolver;
import com.sun.jersey.spi.resource.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@Singleton
public class AuthorisationExceptionMapper
extends AbstractExceptionMapper<AuthorisationException> {
    private final I18nResolver i18nResolver;

    public AuthorisationExceptionMapper(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    public Response toResponse(AuthorisationException exception) {
        return this.response(Response.Status.UNAUTHORIZED, this.i18nResolver.getText("oauth2.rest.error.unauthorized"));
    }
}

