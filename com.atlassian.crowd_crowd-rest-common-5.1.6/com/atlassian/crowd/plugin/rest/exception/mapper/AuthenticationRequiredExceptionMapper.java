/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AuthenticationRequiredException
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.base.Preconditions
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.crowd.plugin.rest.exception.mapper;

import com.atlassian.crowd.plugin.rest.entity.ErrorEntity;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AuthenticationRequiredException;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.base.Preconditions;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticationRequiredExceptionMapper
implements ExceptionMapper<AuthenticationRequiredException> {
    private I18nResolver i18nResolver;

    public AuthenticationRequiredExceptionMapper(@ComponentImport I18nResolver i18nResolver) {
        this.i18nResolver = (I18nResolver)Preconditions.checkNotNull((Object)i18nResolver);
    }

    public Response toResponse(AuthenticationRequiredException e) {
        String message = this.i18nResolver.getText("crowd.administrators.anonymous.exception");
        ErrorEntity errorEntity = new ErrorEntity(ErrorEntity.ErrorReason.of((Throwable)e), message);
        return Response.status((Response.Status)Response.Status.UNAUTHORIZED).entity((Object)errorEntity).build();
    }
}

