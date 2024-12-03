/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.exceptions.ReadOnlyException
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.createcontent.rest;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.exceptions.ReadOnlyException;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.ResourceErrorType;
import com.atlassian.confluence.plugins.createcontent.exceptions.ResourceException;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

@Produces(value={"application/json"})
public abstract class AbstractRestResource {
    private final SpaceManager spaceManager;
    protected final PermissionManager permissionManager;
    protected final AccessModeService accessModeService;

    protected AbstractRestResource(@ComponentImport PermissionManager permissionManager, @ComponentImport SpaceManager spaceManager, @ComponentImport AccessModeService accessModeService) {
        this.spaceManager = spaceManager;
        this.permissionManager = permissionManager;
        this.accessModeService = accessModeService;
    }

    protected Space checkSpaceAdminPermission(@Nonnull String spaceKey) {
        if (this.accessModeService.isReadOnlyAccessModeEnabled()) {
            throw new ReadOnlyException();
        }
        Space space = this.getAndCheckSpace(spaceKey);
        if (!this.permissionManager.hasPermission((User)this.getUser(), Permission.ADMINISTER, (Object)space)) {
            throw new ResourceException("Only space administrators for " + spaceKey + " can make this request.", Response.Status.BAD_REQUEST, ResourceErrorType.PERMISSION_USER_ADMIN_SPACE, (Object)spaceKey);
        }
        return space;
    }

    protected void checkAdminPermission() {
        if (!this.permissionManager.hasPermission((User)this.getUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION)) {
            throw new ResourceException("Only site administrators can make this request.", Response.Status.BAD_REQUEST, ResourceErrorType.PERMISSION_USER_ADMIN);
        }
    }

    protected void checkEmptyParameter(String parameter, String parameterName) {
        if (StringUtils.isBlank((CharSequence)parameter)) {
            this.throwMissingParameterError(parameterName);
        }
    }

    protected void checkNullParameter(Object parameter, String parameterName) {
        if (parameter == null) {
            this.throwMissingParameterError(parameterName);
        }
    }

    protected void checkNullEntity(Object entity) {
        if (entity == null) {
            throw new ResourceException("This request requires a body entity with the data of the object to create/update", Response.Status.BAD_REQUEST, ResourceErrorType.PARAMETER_MISSING, (Object)"bodyEntity");
        }
    }

    private void throwMissingParameterError(String parameterName) {
        throw new ResourceException(String.format("Missing '%1$s' parameter", parameterName), Response.Status.BAD_REQUEST, ResourceErrorType.PARAMETER_MISSING, (Object)parameterName);
    }

    @Nonnull
    protected Space getAndCheckSpace(String spaceKey) {
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            throw new ResourceException(String.format("No space found for spaceKey '%1$s'", spaceKey), Response.Status.BAD_REQUEST, ResourceErrorType.NOT_FOUND_SPACE, (Object)spaceKey);
        }
        return space;
    }

    @Nullable
    protected ConfluenceUser getUser() {
        return AuthenticatedUserThreadLocal.get();
    }
}

