/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.rest;

import com.addonengine.addons.analytics.rest.filter.RateLimitFilter;
import com.addonengine.addons.analytics.rest.filter.UserHasPermissionToViewInstanceAnalyticsFilter;
import com.addonengine.addons.analytics.rest.filter.ValidAddonLicenseFilter;
import com.addonengine.addons.analytics.rest.util.OffsetDateTimeParam;
import com.addonengine.addons.analytics.service.EstimationService;
import com.sun.jersey.spi.container.ResourceFilters;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Path(value="/estimation")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001c\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\b2\b\b\u0001\u0010\t\u001a\u00020\bH\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2={"Lcom/addonengine/addons/analytics/rest/EstimationResource;", "", "estimationService", "Lcom/addonengine/addons/analytics/service/EstimationService;", "(Lcom/addonengine/addons/analytics/service/EstimationService;)V", "getActivityByUser", "Ljavax/ws/rs/core/Response;", "fromDate", "Lcom/addonengine/addons/analytics/rest/util/OffsetDateTimeParam;", "toDate", "analytics"})
public final class EstimationResource {
    @NotNull
    private final EstimationService estimationService;

    public EstimationResource(@NotNull EstimationService estimationService) {
        Intrinsics.checkNotNullParameter((Object)estimationService, (String)"estimationService");
        this.estimationService = estimationService;
    }

    @GET
    @Path(value="reportTiming")
    @ResourceFilters(value={ValidAddonLicenseFilter.class, UserHasPermissionToViewInstanceAnalyticsFilter.class, RateLimitFilter.class})
    @NotNull
    public final Response getActivityByUser(@QueryParam(value="fromDate") @NotNull OffsetDateTimeParam fromDate, @QueryParam(value="toDate") @NotNull OffsetDateTimeParam toDate) {
        Intrinsics.checkNotNullParameter((Object)fromDate, (String)"fromDate");
        Intrinsics.checkNotNullParameter((Object)toDate, (String)"toDate");
        Response response = Response.ok((Object)this.estimationService.estimateReportTiming(fromDate.getValue(), toDate.getValue())).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }
}

