/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.sun.jersey.spi.container.ContainerRequest
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.Provider
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.rest.filter;

import com.addonengine.addons.analytics.rest.dto.ErrorDto;
import com.addonengine.addons.analytics.rest.dto.ErrorResponseDto;
import com.addonengine.addons.analytics.rest.filter.PrerequestFilter;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.sun.jersey.spi.container.ContainerRequest;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Provider
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0019\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\b\u0010\u0007\u001a\u00020\bH\u0002J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\nH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2={"Lcom/addonengine/addons/analytics/rest/filter/UserIsSpaceAdminFilter;", "Lcom/addonengine/addons/analytics/rest/filter/PrerequestFilter;", "permissionManager", "Lcom/atlassian/confluence/security/PermissionManager;", "spaceManager", "Lcom/atlassian/confluence/spaces/SpaceManager;", "(Lcom/atlassian/confluence/security/PermissionManager;Lcom/atlassian/confluence/spaces/SpaceManager;)V", "buildBadRequestException", "Ljavax/ws/rs/WebApplicationException;", "filter", "Lcom/sun/jersey/spi/container/ContainerRequest;", "containerRequest", "analytics"})
public final class UserIsSpaceAdminFilter
extends PrerequestFilter {
    @NotNull
    private final PermissionManager permissionManager;
    @NotNull
    private final SpaceManager spaceManager;

    public UserIsSpaceAdminFilter(@ComponentImport @NotNull PermissionManager permissionManager, @ComponentImport @NotNull SpaceManager spaceManager) {
        Intrinsics.checkNotNullParameter((Object)permissionManager, (String)"permissionManager");
        Intrinsics.checkNotNullParameter((Object)spaceManager, (String)"spaceManager");
        this.permissionManager = permissionManager;
        this.spaceManager = spaceManager;
    }

    @Override
    @NotNull
    public ContainerRequest filter(@NotNull ContainerRequest containerRequest) {
        Intrinsics.checkNotNullParameter((Object)containerRequest, (String)"containerRequest");
        List list = (List)containerRequest.getQueryParameters().get((Object)"spaceKey");
        String string = list != null ? (String)list.get(0) : null;
        if (string == null) {
            throw this.buildBadRequestException();
        }
        String spaceKey = string;
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space != null && this.permissionManager.hasPermission((User)user, Permission.ADMINISTER, (Object)space)) {
            return containerRequest;
        }
        Response.ResponseBuilder response = Response.status((Response.Status)Response.Status.FORBIDDEN).type("application/json").entity((Object)new ErrorResponseDto(new ErrorDto("UserIsNotSpaceAdmin", "The requesting user must be a Space Admin of the '" + spaceKey + "' space.")));
        throw new WebApplicationException(response.build());
    }

    private final WebApplicationException buildBadRequestException() {
        Response.ResponseBuilder response = Response.status((Response.Status)Response.Status.BAD_REQUEST).type("application/json").entity((Object)new ErrorResponseDto(new ErrorDto("MissingParameter", "Missing 'spaceKey' query string parameter.")));
        return new WebApplicationException(response.build());
    }
}

