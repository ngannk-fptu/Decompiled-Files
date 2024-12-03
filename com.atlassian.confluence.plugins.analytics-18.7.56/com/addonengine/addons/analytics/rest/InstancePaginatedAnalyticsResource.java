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
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.rest;

import com.addonengine.addons.analytics.rest.LimitValidator;
import com.addonengine.addons.analytics.rest.dto.GlobalActivityByUserDto;
import com.addonengine.addons.analytics.rest.dto.GlobalUserActivityDto;
import com.addonengine.addons.analytics.rest.dto.InstanceActivityBySpacePaginatedDto;
import com.addonengine.addons.analytics.rest.dto.InstanceSpaceActivityDto;
import com.addonengine.addons.analytics.rest.filter.RateLimitFilter;
import com.addonengine.addons.analytics.rest.filter.UserHasPermissionToViewInstanceAnalyticsFilter;
import com.addonengine.addons.analytics.rest.filter.ValidAddonLicenseFilter;
import com.addonengine.addons.analytics.rest.util.ApiDateTime;
import com.addonengine.addons.analytics.rest.util.ContentTypeSetParam;
import com.addonengine.addons.analytics.rest.util.CountTypeSetParam;
import com.addonengine.addons.analytics.rest.util.DatePeriodOptionsKt;
import com.addonengine.addons.analytics.rest.util.GlobalUserSortFieldParam;
import com.addonengine.addons.analytics.rest.util.OffsetDateTimeParam;
import com.addonengine.addons.analytics.rest.util.PeriodOptionParam;
import com.addonengine.addons.analytics.rest.util.SortOrderParam;
import com.addonengine.addons.analytics.rest.util.SpaceSortFieldParam;
import com.addonengine.addons.analytics.rest.util.SpaceTypeParam;
import com.addonengine.addons.analytics.rest.util.ZoneIdParam;
import com.addonengine.addons.analytics.service.AnonymousHelper;
import com.addonengine.addons.analytics.service.InstancePaginatedAnalyticsService;
import com.addonengine.addons.analytics.service.confluence.model.Space;
import com.addonengine.addons.analytics.service.model.ContentType;
import com.addonengine.addons.analytics.service.model.DatePeriodOptions;
import com.addonengine.addons.analytics.service.model.GlobalUserActivity;
import com.addonengine.addons.analytics.service.model.SpaceActivity;
import com.addonengine.addons.analytics.service.model.SpaceType;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Path(value="/instance/paginated")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000x\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004Jx\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\b2\b\b\u0001\u0010\t\u001a\u00020\b2\b\b\u0001\u0010\n\u001a\u00020\u000b2\b\b\u0001\u0010\f\u001a\u00020\r2\b\b\u0001\u0010\u000e\u001a\u00020\u000f2\b\b\u0001\u0010\u0010\u001a\u00020\u00112\b\b\u0001\u0010\u0012\u001a\u00020\u00132\n\b\u0001\u0010\u0014\u001a\u0004\u0018\u00010\u00152\b\b\u0001\u0010\u0016\u001a\u00020\u00172\b\b\u0001\u0010\u0018\u001a\u00020\u00192\b\b\u0001\u0010\u001a\u001a\u00020\u001bH\u0007Jn\u0010\u001c\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\b2\b\b\u0001\u0010\t\u001a\u00020\b2\b\b\u0001\u0010\n\u001a\u00020\u000b2\b\b\u0001\u0010\f\u001a\u00020\r2\b\b\u0001\u0010\u000e\u001a\u00020\u000f2\b\b\u0001\u0010\u0010\u001a\u00020\u00112\n\b\u0001\u0010\u0014\u001a\u0004\u0018\u00010\u00152\b\b\u0001\u0010\u0016\u001a\u00020\u00172\b\b\u0001\u0010\u0018\u001a\u00020\u001d2\b\b\u0001\u0010\u001a\u001a\u00020\u001bH\u0007J\u001e\u0010\u001e\u001a\u00020\u001f2\f\u0010 \u001a\b\u0012\u0004\u0012\u00020\"0!2\u0006\u0010\u0016\u001a\u00020\u0017H\u0002J\u001e\u0010#\u001a\u00020$2\f\u0010%\u001a\b\u0012\u0004\u0012\u00020&0!2\u0006\u0010\u0016\u001a\u00020\u0017H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006'"}, d2={"Lcom/addonengine/addons/analytics/rest/InstancePaginatedAnalyticsResource;", "", "instanceAnalyticsService", "Lcom/addonengine/addons/analytics/service/InstancePaginatedAnalyticsService;", "(Lcom/addonengine/addons/analytics/service/InstancePaginatedAnalyticsService;)V", "getActivityBySpace", "Ljavax/ws/rs/core/Response;", "fromDate", "Lcom/addonengine/addons/analytics/rest/util/OffsetDateTimeParam;", "toDate", "period", "Lcom/addonengine/addons/analytics/rest/util/PeriodOptionParam;", "timezone", "Lcom/addonengine/addons/analytics/rest/util/ZoneIdParam;", "spaceType", "Lcom/addonengine/addons/analytics/rest/util/SpaceTypeParam;", "contentTypeSet", "Lcom/addonengine/addons/analytics/rest/util/ContentTypeSetParam;", "countType", "Lcom/addonengine/addons/analytics/rest/util/CountTypeSetParam;", "pageToken", "", "limit", "", "sortField", "Lcom/addonengine/addons/analytics/rest/util/SpaceSortFieldParam;", "sortOrder", "Lcom/addonengine/addons/analytics/rest/util/SortOrderParam;", "getActivityByUser", "Lcom/addonengine/addons/analytics/rest/util/GlobalUserSortFieldParam;", "mapActivityBySpaceToDto", "Lcom/addonengine/addons/analytics/rest/dto/InstanceActivityBySpacePaginatedDto;", "extendedActivityBySpace", "", "Lcom/addonengine/addons/analytics/service/model/SpaceActivity;", "mapActivityByUserToDto", "Lcom/addonengine/addons/analytics/rest/dto/GlobalActivityByUserDto;", "extendedActivityByUser", "Lcom/addonengine/addons/analytics/service/model/GlobalUserActivity;", "analytics"})
@SourceDebugExtension(value={"SMAP\nInstancePaginatedAnalyticsResource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InstancePaginatedAnalyticsResource.kt\ncom/addonengine/addons/analytics/rest/InstancePaginatedAnalyticsResource\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,172:1\n1549#2:173\n1620#2,3:174\n1549#2:177\n1620#2,3:178\n*S KotlinDebug\n*F\n+ 1 InstancePaginatedAnalyticsResource.kt\ncom/addonengine/addons/analytics/rest/InstancePaginatedAnalyticsResource\n*L\n131#1:173\n131#1:174,3\n155#1:177\n155#1:178,3\n*E\n"})
public final class InstancePaginatedAnalyticsResource {
    @NotNull
    private final InstancePaginatedAnalyticsService instanceAnalyticsService;

    public InstancePaginatedAnalyticsResource(@NotNull InstancePaginatedAnalyticsService instanceAnalyticsService) {
        Intrinsics.checkNotNullParameter((Object)instanceAnalyticsService, (String)"instanceAnalyticsService");
        this.instanceAnalyticsService = instanceAnalyticsService;
    }

    @GET
    @Path(value="activityByUser")
    @ResourceFilters(value={ValidAddonLicenseFilter.class, UserHasPermissionToViewInstanceAnalyticsFilter.class, RateLimitFilter.class})
    @NotNull
    public final Response getActivityByUser(@QueryParam(value="fromDate") @NotNull OffsetDateTimeParam fromDate, @QueryParam(value="toDate") @NotNull OffsetDateTimeParam toDate, @QueryParam(value="period") @NotNull PeriodOptionParam period, @QueryParam(value="timezone") @NotNull ZoneIdParam timezone, @QueryParam(value="spaceType") @NotNull SpaceTypeParam spaceType, @QueryParam(value="content") @NotNull ContentTypeSetParam contentTypeSet, @QueryParam(value="pageToken") @Nullable String pageToken, @QueryParam(value="limit") int limit, @QueryParam(value="sortField") @NotNull GlobalUserSortFieldParam sortField, @QueryParam(value="sortOrder") @NotNull SortOrderParam sortOrder) {
        Intrinsics.checkNotNullParameter((Object)fromDate, (String)"fromDate");
        Intrinsics.checkNotNullParameter((Object)toDate, (String)"toDate");
        Intrinsics.checkNotNullParameter((Object)period, (String)"period");
        Intrinsics.checkNotNullParameter((Object)timezone, (String)"timezone");
        Intrinsics.checkNotNullParameter((Object)spaceType, (String)"spaceType");
        Intrinsics.checkNotNullParameter((Object)contentTypeSet, (String)"contentTypeSet");
        Intrinsics.checkNotNullParameter((Object)sortField, (String)"sortField");
        Intrinsics.checkNotNullParameter((Object)sortOrder, (String)"sortOrder");
        if (LimitValidator.Companion.isInvalid(limit)) {
            Response response = Response.status((Response.Status)Response.Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE).entity((Object)LimitValidator.Companion.errorMessage()).build();
            Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
            return response;
        }
        DatePeriodOptions datePeriodOptions = DatePeriodOptionsKt.buildDatePeriodOptionsFromParams(fromDate, toDate, period, timezone);
        List<GlobalUserActivity> activityByUser2 = this.instanceAnalyticsService.getActivityByUser(datePeriodOptions, (Set<? extends SpaceType>)spaceType.getValue(), (Set<? extends ContentType>)contentTypeSet.getValue(), pageToken, limit + 1, sortField.getValue(), sortOrder.getValue());
        Response response = Response.ok((Object)this.mapActivityByUserToDto(activityByUser2, limit)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    @GET
    @Path(value="activityBySpace")
    @ResourceFilters(value={ValidAddonLicenseFilter.class, UserHasPermissionToViewInstanceAnalyticsFilter.class, RateLimitFilter.class})
    @NotNull
    public final Response getActivityBySpace(@QueryParam(value="fromDate") @NotNull OffsetDateTimeParam fromDate, @QueryParam(value="toDate") @NotNull OffsetDateTimeParam toDate, @QueryParam(value="period") @NotNull PeriodOptionParam period, @QueryParam(value="timezone") @NotNull ZoneIdParam timezone, @QueryParam(value="spaceType") @NotNull SpaceTypeParam spaceType, @QueryParam(value="content") @NotNull ContentTypeSetParam contentTypeSet, @QueryParam(value="type") @NotNull CountTypeSetParam countType, @QueryParam(value="pageToken") @Nullable String pageToken, @QueryParam(value="limit") int limit, @QueryParam(value="sortField") @NotNull SpaceSortFieldParam sortField, @QueryParam(value="sortOrder") @NotNull SortOrderParam sortOrder) {
        Intrinsics.checkNotNullParameter((Object)fromDate, (String)"fromDate");
        Intrinsics.checkNotNullParameter((Object)toDate, (String)"toDate");
        Intrinsics.checkNotNullParameter((Object)period, (String)"period");
        Intrinsics.checkNotNullParameter((Object)timezone, (String)"timezone");
        Intrinsics.checkNotNullParameter((Object)spaceType, (String)"spaceType");
        Intrinsics.checkNotNullParameter((Object)contentTypeSet, (String)"contentTypeSet");
        Intrinsics.checkNotNullParameter((Object)countType, (String)"countType");
        Intrinsics.checkNotNullParameter((Object)sortField, (String)"sortField");
        Intrinsics.checkNotNullParameter((Object)sortOrder, (String)"sortOrder");
        if (LimitValidator.Companion.isInvalid(limit)) {
            Response response = Response.status((Response.Status)Response.Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE).entity((Object)LimitValidator.Companion.errorMessage()).build();
            Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
            return response;
        }
        DatePeriodOptions datePeriodOptions = DatePeriodOptionsKt.buildDatePeriodOptionsFromParams(fromDate, toDate, period, timezone);
        List activityBySpace2 = InstancePaginatedAnalyticsService.DefaultImpls.getActivityBySpace$default(this.instanceAnalyticsService, datePeriodOptions, (Set)spaceType.getValue(), (Set)contentTypeSet.getValue(), pageToken, limit + 1, sortField.getValue(), sortOrder.getValue(), (Set)countType.getValue(), false, 256, null);
        Response response = Response.ok((Object)this.mapActivityBySpaceToDto(activityBySpace2, limit)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    /*
     * WARNING - void declaration
     */
    private final GlobalActivityByUserDto mapActivityByUserToDto(List<GlobalUserActivity> extendedActivityByUser, int limit) {
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        String string;
        if (extendedActivityByUser.size() == limit + 1) {
            GlobalUserActivity globalUserActivity = (GlobalUserActivity)CollectionsKt.getOrNull(extendedActivityByUser, (int)(limit - 1));
            string = globalUserActivity != null ? globalUserActivity.getUserKey() : null;
        } else {
            string = null;
        }
        String nextPageToken = string;
        Iterable iterable = CollectionsKt.take((Iterable)extendedActivityByUser, (int)limit);
        boolean $i$f$map = false;
        void var6_6 = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            GlobalUserActivity globalUserActivity = (GlobalUserActivity)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            String string2 = it.getUserType().toString();
            String string3 = AnonymousHelper.INSTANCE.userKeyToFrontendFormat(it.getUserKey());
            if (string3 == null) {
                string3 = "";
            }
            collection.add(new GlobalUserActivityDto(string2, string3, it.getViewedCount(), it.getCreatedCount(), it.getUpdatedCount(), it.getCommentsCount(), it.getContributorScore()));
        }
        String string4 = nextPageToken;
        List list = (List)destination$iv$iv;
        return new GlobalActivityByUserDto(list, string4);
    }

    /*
     * WARNING - void declaration
     */
    private final InstanceActivityBySpacePaginatedDto mapActivityBySpaceToDto(List<SpaceActivity> extendedActivityBySpace, int limit) {
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        Object object;
        String nextPageToken = extendedActivityBySpace.size() == limit + 1 ? ((object = (SpaceActivity)CollectionsKt.getOrNull(extendedActivityBySpace, (int)(limit - 1))) != null && (object = ((SpaceActivity)object).getSpace()) != null ? ((Space)object).getKey() : null) : null;
        Iterable iterable = CollectionsKt.take((Iterable)extendedActivityBySpace, (int)limit);
        boolean $i$f$map = false;
        void var6_6 = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            SpaceActivity spaceActivity = (SpaceActivity)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            String string = it.getSpace().getKey();
            String string2 = it.getSpace().getType().toString().toLowerCase();
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"this as java.lang.String).toLowerCase()");
            collection.add(new InstanceSpaceActivityDto(string, string2, it.getSpace().getName(), it.getSpace().getLink(), it.getSpace().getLogoUrl(), it.getCreated(), it.getUpdated(), new ApiDateTime(it.getLastViewedAt()), it.getUsersViewed(), it.getViews()));
        }
        String string = nextPageToken;
        List list = (List)destination$iv$iv;
        return new InstanceActivityBySpacePaginatedDto(list, string);
    }
}

