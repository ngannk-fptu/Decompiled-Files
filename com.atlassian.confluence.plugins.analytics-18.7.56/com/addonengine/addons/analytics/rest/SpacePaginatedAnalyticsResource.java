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
import com.addonengine.addons.analytics.rest.dto.ActivityByContentPaginatedDto;
import com.addonengine.addons.analytics.rest.dto.ContentActivityDto;
import com.addonengine.addons.analytics.rest.dto.SpaceLevelActivityByUserDto;
import com.addonengine.addons.analytics.rest.dto.SpaceLevelUserActivityDto;
import com.addonengine.addons.analytics.rest.filter.RateLimitFilter;
import com.addonengine.addons.analytics.rest.filter.UserHasPermissionToViewSpaceAnalyticsFilter;
import com.addonengine.addons.analytics.rest.filter.ValidAddonLicenseFilter;
import com.addonengine.addons.analytics.rest.util.ApiDateTime;
import com.addonengine.addons.analytics.rest.util.ContentSortFieldParam;
import com.addonengine.addons.analytics.rest.util.ContentTypeSetParam;
import com.addonengine.addons.analytics.rest.util.DatePeriodOptionsKt;
import com.addonengine.addons.analytics.rest.util.OffsetDateTimeParam;
import com.addonengine.addons.analytics.rest.util.PeriodOptionParam;
import com.addonengine.addons.analytics.rest.util.SortOrderParam;
import com.addonengine.addons.analytics.rest.util.SpaceLevelUserSortFieldParam;
import com.addonengine.addons.analytics.rest.util.ZoneIdParam;
import com.addonengine.addons.analytics.service.AnonymousHelper;
import com.addonengine.addons.analytics.service.SpacePaginatedAnalyticsService;
import com.addonengine.addons.analytics.service.confluence.model.Content;
import com.addonengine.addons.analytics.service.model.ContentActivity;
import com.addonengine.addons.analytics.service.model.ContentType;
import com.addonengine.addons.analytics.service.model.DatePeriodOptions;
import com.addonengine.addons.analytics.service.model.SpaceLevelUserActivity;
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

@Path(value="/space/paginated")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000n\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004Jn\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\b2\b\b\u0001\u0010\t\u001a\u00020\b2\b\b\u0001\u0010\n\u001a\u00020\u000b2\b\b\u0001\u0010\f\u001a\u00020\r2\b\b\u0001\u0010\u000e\u001a\u00020\u000f2\b\b\u0001\u0010\u0010\u001a\u00020\u00112\n\b\u0001\u0010\u0012\u001a\u0004\u0018\u00010\u000f2\b\b\u0001\u0010\u0013\u001a\u00020\u00142\b\b\u0001\u0010\u0015\u001a\u00020\u00162\b\b\u0001\u0010\u0017\u001a\u00020\u0018H\u0007Jn\u0010\u0019\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\b2\b\b\u0001\u0010\t\u001a\u00020\b2\b\b\u0001\u0010\n\u001a\u00020\u000b2\b\b\u0001\u0010\f\u001a\u00020\r2\b\b\u0001\u0010\u000e\u001a\u00020\u000f2\b\b\u0001\u0010\u0010\u001a\u00020\u00112\n\b\u0001\u0010\u0012\u001a\u0004\u0018\u00010\u000f2\b\b\u0001\u0010\u0013\u001a\u00020\u00142\b\b\u0001\u0010\u0015\u001a\u00020\u001a2\b\b\u0001\u0010\u0017\u001a\u00020\u0018H\u0007J\u001e\u0010\u001b\u001a\u00020\u001c2\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001f0\u001e2\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\u001e\u0010 \u001a\u00020!2\f\u0010\"\u001a\b\u0012\u0004\u0012\u00020#0\u001e2\u0006\u0010\u0013\u001a\u00020\u0014H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006$"}, d2={"Lcom/addonengine/addons/analytics/rest/SpacePaginatedAnalyticsResource;", "", "spaceAnalyticsService", "Lcom/addonengine/addons/analytics/service/SpacePaginatedAnalyticsService;", "(Lcom/addonengine/addons/analytics/service/SpacePaginatedAnalyticsService;)V", "getActivityByContent", "Ljavax/ws/rs/core/Response;", "fromDate", "Lcom/addonengine/addons/analytics/rest/util/OffsetDateTimeParam;", "toDate", "period", "Lcom/addonengine/addons/analytics/rest/util/PeriodOptionParam;", "timezone", "Lcom/addonengine/addons/analytics/rest/util/ZoneIdParam;", "spaceKey", "", "contentTypeSet", "Lcom/addonengine/addons/analytics/rest/util/ContentTypeSetParam;", "pageToken", "limit", "", "sortField", "Lcom/addonengine/addons/analytics/rest/util/ContentSortFieldParam;", "sortOrder", "Lcom/addonengine/addons/analytics/rest/util/SortOrderParam;", "getActivityByUser", "Lcom/addonengine/addons/analytics/rest/util/SpaceLevelUserSortFieldParam;", "mapActivityByContentToDto", "Lcom/addonengine/addons/analytics/rest/dto/ActivityByContentPaginatedDto;", "extendedActivityByContent", "", "Lcom/addonengine/addons/analytics/service/model/ContentActivity;", "mapActivityByUserToDto", "Lcom/addonengine/addons/analytics/rest/dto/SpaceLevelActivityByUserDto;", "extendedActivityByUser", "Lcom/addonengine/addons/analytics/service/model/SpaceLevelUserActivity;", "analytics"})
@SourceDebugExtension(value={"SMAP\nSpacePaginatedAnalyticsResource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SpacePaginatedAnalyticsResource.kt\ncom/addonengine/addons/analytics/rest/SpacePaginatedAnalyticsResource\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,158:1\n1549#2:159\n1620#2,3:160\n1549#2:163\n1620#2,3:164\n*S KotlinDebug\n*F\n+ 1 SpacePaginatedAnalyticsResource.kt\ncom/addonengine/addons/analytics/rest/SpacePaginatedAnalyticsResource\n*L\n118#1:159\n118#1:160,3\n141#1:163\n141#1:164,3\n*E\n"})
public final class SpacePaginatedAnalyticsResource {
    @NotNull
    private final SpacePaginatedAnalyticsService spaceAnalyticsService;

    public SpacePaginatedAnalyticsResource(@NotNull SpacePaginatedAnalyticsService spaceAnalyticsService) {
        Intrinsics.checkNotNullParameter((Object)spaceAnalyticsService, (String)"spaceAnalyticsService");
        this.spaceAnalyticsService = spaceAnalyticsService;
    }

    @GET
    @Path(value="activityByUser")
    @ResourceFilters(value={ValidAddonLicenseFilter.class, UserHasPermissionToViewSpaceAnalyticsFilter.class, RateLimitFilter.class})
    @NotNull
    public final Response getActivityByUser(@QueryParam(value="fromDate") @NotNull OffsetDateTimeParam fromDate, @QueryParam(value="toDate") @NotNull OffsetDateTimeParam toDate, @QueryParam(value="period") @NotNull PeriodOptionParam period, @QueryParam(value="timezone") @NotNull ZoneIdParam timezone, @QueryParam(value="spaceKey") @NotNull String spaceKey, @QueryParam(value="content") @NotNull ContentTypeSetParam contentTypeSet, @QueryParam(value="pageToken") @Nullable String pageToken, @QueryParam(value="limit") int limit, @QueryParam(value="sortField") @NotNull SpaceLevelUserSortFieldParam sortField, @QueryParam(value="sortOrder") @NotNull SortOrderParam sortOrder) {
        Intrinsics.checkNotNullParameter((Object)fromDate, (String)"fromDate");
        Intrinsics.checkNotNullParameter((Object)toDate, (String)"toDate");
        Intrinsics.checkNotNullParameter((Object)period, (String)"period");
        Intrinsics.checkNotNullParameter((Object)timezone, (String)"timezone");
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        Intrinsics.checkNotNullParameter((Object)contentTypeSet, (String)"contentTypeSet");
        Intrinsics.checkNotNullParameter((Object)sortField, (String)"sortField");
        Intrinsics.checkNotNullParameter((Object)sortOrder, (String)"sortOrder");
        if (LimitValidator.Companion.isInvalid(limit)) {
            Response response = Response.status((Response.Status)Response.Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE).entity((Object)LimitValidator.Companion.errorMessage()).build();
            Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
            return response;
        }
        DatePeriodOptions datePeriodOptions = DatePeriodOptionsKt.buildDatePeriodOptionsFromParams(fromDate, toDate, period, timezone);
        List<SpaceLevelUserActivity> activityByUser2 = this.spaceAnalyticsService.getActivityByUser(datePeriodOptions, spaceKey, (Set<? extends ContentType>)contentTypeSet.getValue(), pageToken, limit + 1, sortField.getValue(), sortOrder.getValue());
        Response response = Response.ok((Object)this.mapActivityByUserToDto(activityByUser2, limit)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    @GET
    @Path(value="contentViews")
    @ResourceFilters(value={ValidAddonLicenseFilter.class, UserHasPermissionToViewSpaceAnalyticsFilter.class, RateLimitFilter.class})
    @NotNull
    public final Response getActivityByContent(@QueryParam(value="fromDate") @NotNull OffsetDateTimeParam fromDate, @QueryParam(value="toDate") @NotNull OffsetDateTimeParam toDate, @QueryParam(value="period") @NotNull PeriodOptionParam period, @QueryParam(value="timezone") @NotNull ZoneIdParam timezone, @QueryParam(value="spaceKey") @NotNull String spaceKey, @QueryParam(value="content") @NotNull ContentTypeSetParam contentTypeSet, @QueryParam(value="pageToken") @Nullable String pageToken, @QueryParam(value="limit") int limit, @QueryParam(value="sortField") @NotNull ContentSortFieldParam sortField, @QueryParam(value="sortOrder") @NotNull SortOrderParam sortOrder) {
        Intrinsics.checkNotNullParameter((Object)fromDate, (String)"fromDate");
        Intrinsics.checkNotNullParameter((Object)toDate, (String)"toDate");
        Intrinsics.checkNotNullParameter((Object)period, (String)"period");
        Intrinsics.checkNotNullParameter((Object)timezone, (String)"timezone");
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        Intrinsics.checkNotNullParameter((Object)contentTypeSet, (String)"contentTypeSet");
        Intrinsics.checkNotNullParameter((Object)sortField, (String)"sortField");
        Intrinsics.checkNotNullParameter((Object)sortOrder, (String)"sortOrder");
        if (LimitValidator.Companion.isInvalid(limit)) {
            Response response = Response.status((Response.Status)Response.Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE).entity((Object)LimitValidator.Companion.errorMessage()).build();
            Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
            return response;
        }
        DatePeriodOptions datePeriodOptions = DatePeriodOptionsKt.buildDatePeriodOptionsFromParams(fromDate, toDate, period, timezone);
        List<ContentActivity> activityByContent2 = this.spaceAnalyticsService.getActivityByContent(datePeriodOptions, spaceKey, (Set<? extends ContentType>)contentTypeSet.getValue(), pageToken, limit + 1, sortField.getValue(), sortOrder.getValue());
        Response response = Response.ok((Object)this.mapActivityByContentToDto(activityByContent2, limit)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    /*
     * WARNING - void declaration
     */
    private final SpaceLevelActivityByUserDto mapActivityByUserToDto(List<SpaceLevelUserActivity> extendedActivityByUser, int limit) {
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        String string;
        if (extendedActivityByUser.size() == limit + 1) {
            SpaceLevelUserActivity spaceLevelUserActivity = (SpaceLevelUserActivity)CollectionsKt.getOrNull(extendedActivityByUser, (int)(limit - 1));
            string = spaceLevelUserActivity != null ? spaceLevelUserActivity.getUserKey() : null;
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
            SpaceLevelUserActivity spaceLevelUserActivity = (SpaceLevelUserActivity)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            String string2 = it.getUserType().toString();
            String string3 = AnonymousHelper.INSTANCE.userKeyToFrontendFormat(it.getUserKey());
            if (string3 == null) {
                string3 = "";
            }
            collection.add(new SpaceLevelUserActivityDto(string2, string3, it.getViewedCount(), it.getCreatedCount(), it.getUpdatedCount(), it.getCommentsCount(), it.getContributorScore()));
        }
        String string4 = nextPageToken;
        List list = (List)destination$iv$iv;
        return new SpaceLevelActivityByUserDto(list, string4);
    }

    /*
     * WARNING - void declaration
     */
    private final ActivityByContentPaginatedDto mapActivityByContentToDto(List<ContentActivity> extendedActivityByContent, int limit) {
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        Object object;
        Long nextPageToken = extendedActivityByContent.size() == limit + 1 ? ((object = (ContentActivity)CollectionsKt.getOrNull(extendedActivityByContent, (int)(limit - 1))) != null && (object = ((ContentActivity)object).getContent()) != null ? Long.valueOf(((Content)object).getId()) : null) : null;
        Iterable iterable = CollectionsKt.take((Iterable)extendedActivityByContent, (int)limit);
        boolean $i$f$map = false;
        void var6_6 = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            ContentActivity contentActivity = (ContentActivity)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            long l = it.getContent().getId();
            String string = it.getContent().getType().toString().toLowerCase();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"this as java.lang.String).toLowerCase()");
            collection.add(new ContentActivityDto(l, string, it.getContent().getTitle(), it.getContent().getLink(), new ApiDateTime(it.getContent().getCreatedAt()), new ApiDateTime(it.getContent().getLastModifiedAt()), new ApiDateTime(it.getLastViewedAt()), it.getCommentActivityCount(), it.getUsersViewed(), it.getViews()));
        }
        Long l = nextPageToken;
        String string = l != null ? l.toString() : null;
        List list = (List)destination$iv$iv;
        return new ActivityByContentPaginatedDto(list, string);
    }
}

