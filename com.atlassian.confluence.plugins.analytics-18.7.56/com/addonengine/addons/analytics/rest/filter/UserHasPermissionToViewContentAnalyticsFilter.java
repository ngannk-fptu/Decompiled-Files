/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
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
import com.addonengine.addons.analytics.service.RestrictionsService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.sun.jersey.spi.container.ContainerRequest;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Provider
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u0005\u001a\u00020\u0006H\u0002J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\bH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2={"Lcom/addonengine/addons/analytics/rest/filter/UserHasPermissionToViewContentAnalyticsFilter;", "Lcom/addonengine/addons/analytics/rest/filter/PrerequestFilter;", "restrictionsService", "Lcom/addonengine/addons/analytics/service/RestrictionsService;", "(Lcom/addonengine/addons/analytics/service/RestrictionsService;)V", "buildBadRequestException", "Ljavax/ws/rs/WebApplicationException;", "filter", "Lcom/sun/jersey/spi/container/ContainerRequest;", "containerRequest", "analytics"})
public final class UserHasPermissionToViewContentAnalyticsFilter
extends PrerequestFilter {
    @NotNull
    private final RestrictionsService restrictionsService;

    public UserHasPermissionToViewContentAnalyticsFilter(@NotNull RestrictionsService restrictionsService) {
        Intrinsics.checkNotNullParameter((Object)restrictionsService, (String)"restrictionsService");
        this.restrictionsService = restrictionsService;
    }

    @Override
    @NotNull
    public ContainerRequest filter(@NotNull ContainerRequest containerRequest) {
        long l;
        Intrinsics.checkNotNullParameter((Object)containerRequest, (String)"containerRequest");
        String string = AuthenticatedUserThreadLocal.get().getKey().getStringValue();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getStringValue(...)");
        String userKey = string;
        List list = (List)containerRequest.getQueryParameters().get((Object)"contentId");
        String contentIdString = list != null ? (String)list.get(0) : null;
        try {
            String string2 = contentIdString;
            Intrinsics.checkNotNull((Object)string2);
            l = Long.parseLong(string2);
        }
        catch (Exception e) {
            throw this.buildBadRequestException();
        }
        long contentId = l;
        if (this.restrictionsService.isUserAllowedToViewContentAnalytics(userKey, contentId)) {
            return containerRequest;
        }
        Response.ResponseBuilder response = Response.status((Response.Status)Response.Status.FORBIDDEN).type("application/json").entity((Object)new ErrorResponseDto(new ErrorDto("ViewAnalyticsRestricted", "The requesting user does not have permission to view analytics.")));
        throw new WebApplicationException(response.build());
    }

    private final WebApplicationException buildBadRequestException() {
        Response.ResponseBuilder response = Response.status((Response.Status)Response.Status.BAD_REQUEST).type("application/json").entity((Object)new ErrorResponseDto(new ErrorDto("InvalidParameterType", "Query string parameter 'contentId' isn't a number.")));
        return new WebApplicationException(response.build());
    }
}

