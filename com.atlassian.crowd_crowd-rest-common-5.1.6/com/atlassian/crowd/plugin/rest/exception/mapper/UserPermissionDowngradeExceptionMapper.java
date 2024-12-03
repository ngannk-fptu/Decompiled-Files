/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.permission.UserPermissionDowngradeException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.base.Preconditions
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.crowd.plugin.rest.exception.mapper;

import com.atlassian.crowd.manager.permission.UserPermissionDowngradeException;
import com.atlassian.crowd.plugin.rest.entity.ErrorEntity;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UserPermissionDowngradeExceptionMapper
implements ExceptionMapper<UserPermissionDowngradeException> {
    private I18nResolver i18nResolver;

    public UserPermissionDowngradeExceptionMapper(@ComponentImport I18nResolver i18nResolver) {
        this.i18nResolver = (I18nResolver)Preconditions.checkNotNull((Object)i18nResolver);
    }

    public Response toResponse(UserPermissionDowngradeException e) {
        String message = this.i18nResolver.getText("crowd.administrators.downgrade.exception", new Serializable[]{e.getGroupName()});
        ErrorEntity errorEntity = new ErrorEntity(ErrorEntity.ErrorReason.of((Throwable)e), message);
        return Response.status((Response.Status)Response.Status.UNAUTHORIZED).entity((Object)errorEntity).build();
    }
}

