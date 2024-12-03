/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.inject.Inject
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.plugins.authentication.impl.rest.exception;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.rest.model.ErrorEntity;
import com.atlassian.plugins.authentication.impl.web.InvalidLicenseException;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidLicenseExceptionMapper
implements ExceptionMapper<InvalidLicenseException> {
    private final I18nResolver i18nResolver;

    @Inject
    public InvalidLicenseExceptionMapper(@ComponentImport I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    public Response toResponse(InvalidLicenseException exception) {
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)new ErrorEntity(this.i18nResolver.getText("authentication.config.save.fail.license", new Serializable[]{"https://www.atlassian.com/enterprise/data-center"}))).type("application/json").build();
    }
}

