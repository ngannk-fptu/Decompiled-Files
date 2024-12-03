/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.applinks.core.rest.exceptionmapper;

import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class TypeNotInstalledExceptionMapper
implements ExceptionMapper<TypeNotInstalledException> {
    private I18nResolver i18nResolver;

    public TypeNotInstalledExceptionMapper(@Context I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    public Response toResponse(TypeNotInstalledException e) {
        return RestUtil.badRequest(this.i18nResolver.getText("applinks.type.not.installed", new Serializable[]{e.getType()}));
    }
}

