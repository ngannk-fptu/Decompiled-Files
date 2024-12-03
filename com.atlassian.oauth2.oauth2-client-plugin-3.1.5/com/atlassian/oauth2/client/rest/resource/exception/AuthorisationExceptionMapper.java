/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.permission.AuthorisationException
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.oauth2.client.rest.resource.exception;

import com.atlassian.oauth2.client.rest.resource.exception.AbstractExceptionMapper;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.permission.AuthorisationException;
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

