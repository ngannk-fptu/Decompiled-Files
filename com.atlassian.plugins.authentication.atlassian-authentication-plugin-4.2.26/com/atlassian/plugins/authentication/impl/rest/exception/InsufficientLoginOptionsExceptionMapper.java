/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.plugins.authentication.impl.rest.exception;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.api.exception.CannotDisableIdpException;
import com.atlassian.plugins.authentication.api.exception.CannotDisableLoginFormException;
import com.atlassian.plugins.authentication.api.exception.InsufficientLoginOptionsException;
import com.atlassian.plugins.authentication.impl.rest.model.ErrorEntity;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InsufficientLoginOptionsExceptionMapper
implements ExceptionMapper<InsufficientLoginOptionsException> {
    private final I18nResolver i18nResolver;

    public InsufficientLoginOptionsExceptionMapper(@ComponentImport I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    public Response toResponse(InsufficientLoginOptionsException exception) {
        String errorMessage = exception.getMessage();
        if (exception instanceof CannotDisableLoginFormException) {
            errorMessage = this.i18nResolver.getText("authentication.config.save.fail.insufficient.login.options.form", new Serializable[]{Integer.valueOf(1)});
        } else if (exception instanceof CannotDisableIdpException) {
            errorMessage = this.i18nResolver.getText("authentication.config.save.fail.insufficient.login.options.idp", new Serializable[]{Integer.valueOf(1)});
        }
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)new ErrorEntity(errorMessage)).type("application/json").build();
    }
}

