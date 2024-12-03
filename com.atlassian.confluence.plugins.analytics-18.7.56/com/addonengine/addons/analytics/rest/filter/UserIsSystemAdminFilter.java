/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserManager
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
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.sun.jersey.spi.container.ContainerRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Provider
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2={"Lcom/addonengine/addons/analytics/rest/filter/UserIsSystemAdminFilter;", "Lcom/addonengine/addons/analytics/rest/filter/PrerequestFilter;", "userManager", "Lcom/atlassian/sal/api/user/UserManager;", "(Lcom/atlassian/sal/api/user/UserManager;)V", "filter", "Lcom/sun/jersey/spi/container/ContainerRequest;", "containerRequest", "analytics"})
public final class UserIsSystemAdminFilter
extends PrerequestFilter {
    @NotNull
    private final UserManager userManager;

    public UserIsSystemAdminFilter(@ComponentImport @NotNull UserManager userManager) {
        Intrinsics.checkNotNullParameter((Object)userManager, (String)"userManager");
        this.userManager = userManager;
    }

    @Override
    @NotNull
    public ContainerRequest filter(@NotNull ContainerRequest containerRequest) {
        Intrinsics.checkNotNullParameter((Object)containerRequest, (String)"containerRequest");
        if (this.userManager.isSystemAdmin(this.userManager.getRemoteUserKey())) {
            return containerRequest;
        }
        Response.ResponseBuilder response = Response.status((Response.Status)Response.Status.FORBIDDEN).type("application/json").entity((Object)new ErrorResponseDto(new ErrorDto("UserIsNotSystemAdmin", "The requesting user must be a Confluence system administrator.")));
        throw new WebApplicationException(response.build());
    }
}

