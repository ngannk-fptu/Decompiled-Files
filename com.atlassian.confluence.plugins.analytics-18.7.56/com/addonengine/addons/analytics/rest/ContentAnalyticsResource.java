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
 *  kotlin.collections.CollectionsKt
 *  kotlin.comparisons.ComparisonsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.rest;

import com.addonengine.addons.analytics.rest.dto.AddonDetailsDto;
import com.addonengine.addons.analytics.rest.dto.AttachmentViewsDto;
import com.addonengine.addons.analytics.rest.dto.ContentAnalyticsDetailsDto;
import com.addonengine.addons.analytics.rest.dto.ContentDetailsDto;
import com.addonengine.addons.analytics.rest.dto.ContentUserViewsDto;
import com.addonengine.addons.analytics.rest.dto.ContentViewsByAttachmentDto;
import com.addonengine.addons.analytics.rest.dto.ContentViewsByPeriodDto;
import com.addonengine.addons.analytics.rest.dto.ContentViewsByUserDto;
import com.addonengine.addons.analytics.rest.dto.PeriodActivityDto;
import com.addonengine.addons.analytics.rest.dto.SampleDataDetailsDto;
import com.addonengine.addons.analytics.rest.dto.SpaceDetailsDto;
import com.addonengine.addons.analytics.rest.filter.UserHasPermissionToViewContentAnalyticsFilter;
import com.addonengine.addons.analytics.rest.filter.ValidAddonLicenseFilter;
import com.addonengine.addons.analytics.rest.util.ApiDateTime;
import com.addonengine.addons.analytics.rest.util.ContentTypeParam;
import com.addonengine.addons.analytics.rest.util.CountTypeParam;
import com.addonengine.addons.analytics.rest.util.DatePeriodOptionsKt;
import com.addonengine.addons.analytics.rest.util.OffsetDateTimeParam;
import com.addonengine.addons.analytics.rest.util.PeriodOptionParam;
import com.addonengine.addons.analytics.rest.util.ZoneIdParam;
import com.addonengine.addons.analytics.service.AddonService;
import com.addonengine.addons.analytics.service.ContentAnalyticsService;
import com.addonengine.addons.analytics.service.SettingsService;
import com.addonengine.addons.analytics.service.confluence.ContentService;
import com.addonengine.addons.analytics.service.confluence.model.Content;
import com.addonengine.addons.analytics.service.confluence.model.ContentVersion;
import com.addonengine.addons.analytics.service.model.AddonDetails;
import com.addonengine.addons.analytics.service.model.AttachmentViews;
import com.addonengine.addons.analytics.service.model.ContentRef;
import com.addonengine.addons.analytics.service.model.DatePeriodOptions;
import com.addonengine.addons.analytics.service.model.PeriodActivity;
import com.addonengine.addons.analytics.service.model.SampleDataDetails;
import com.addonengine.addons.analytics.service.model.UserViews;
import com.addonengine.addons.analytics.service.model.settings.DataRetentionSettings;
import com.sun.jersey.spi.container.ResourceFilters;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Path(value="/content")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@ResourceFilters(value={ValidAddonLicenseFilter.class, UserHasPermissionToViewContentAnalyticsFilter.class})
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u0012\u0010\u000b\u001a\u00020\f2\b\b\u0001\u0010\r\u001a\u00020\u000eH\u0007J\u0012\u0010\u000f\u001a\u00020\f2\b\b\u0001\u0010\r\u001a\u00020\u000eH\u0007JN\u0010\u0010\u001a\u00020\f2\b\b\u0001\u0010\u0011\u001a\u00020\u00122\b\b\u0001\u0010\u0013\u001a\u00020\u00122\b\b\u0001\u0010\u0014\u001a\u00020\u00152\b\b\u0001\u0010\u0016\u001a\u00020\u00172\b\b\u0001\u0010\u0018\u001a\u00020\u00192\b\b\u0001\u0010\u001a\u001a\u00020\u001b2\b\b\u0001\u0010\r\u001a\u00020\u000eH\u0007J\u001c\u0010\u001c\u001a\u00020\f2\b\b\u0001\u0010\u001a\u001a\u00020\u001b2\b\b\u0001\u0010\r\u001a\u00020\u000eH\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001d"}, d2={"Lcom/addonengine/addons/analytics/rest/ContentAnalyticsResource;", "", "contentAnalyticsService", "Lcom/addonengine/addons/analytics/service/ContentAnalyticsService;", "addonService", "Lcom/addonengine/addons/analytics/service/AddonService;", "contentService", "Lcom/addonengine/addons/analytics/service/confluence/ContentService;", "settingsService", "Lcom/addonengine/addons/analytics/service/SettingsService;", "(Lcom/addonengine/addons/analytics/service/ContentAnalyticsService;Lcom/addonengine/addons/analytics/service/AddonService;Lcom/addonengine/addons/analytics/service/confluence/ContentService;Lcom/addonengine/addons/analytics/service/SettingsService;)V", "getDetails", "Ljavax/ws/rs/core/Response;", "contentId", "", "getViewsByAttachment", "getViewsByPeriod", "fromDate", "Lcom/addonengine/addons/analytics/rest/util/OffsetDateTimeParam;", "toDate", "period", "Lcom/addonengine/addons/analytics/rest/util/PeriodOptionParam;", "timezone", "Lcom/addonengine/addons/analytics/rest/util/ZoneIdParam;", "countType", "Lcom/addonengine/addons/analytics/rest/util/CountTypeParam;", "contentType", "Lcom/addonengine/addons/analytics/rest/util/ContentTypeParam;", "getViewsByUser", "analytics"})
@SourceDebugExtension(value={"SMAP\nContentAnalyticsResource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ContentAnalyticsResource.kt\ncom/addonengine/addons/analytics/rest/ContentAnalyticsResource\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,157:1\n1549#2:158\n1620#2,3:159\n1549#2:162\n1620#2,3:163\n1549#2:166\n1620#2,3:167\n*S KotlinDebug\n*F\n+ 1 ContentAnalyticsResource.kt\ncom/addonengine/addons/analytics/rest/ContentAnalyticsResource\n*L\n62#1:158\n62#1:159,3\n92#1:162\n92#1:163,3\n106#1:166\n106#1:167,3\n*E\n"})
public final class ContentAnalyticsResource {
    @NotNull
    private final ContentAnalyticsService contentAnalyticsService;
    @NotNull
    private final AddonService addonService;
    @NotNull
    private final ContentService contentService;
    @NotNull
    private final SettingsService settingsService;

    public ContentAnalyticsResource(@NotNull ContentAnalyticsService contentAnalyticsService, @NotNull AddonService addonService, @NotNull ContentService contentService, @NotNull SettingsService settingsService) {
        Intrinsics.checkNotNullParameter((Object)contentAnalyticsService, (String)"contentAnalyticsService");
        Intrinsics.checkNotNullParameter((Object)addonService, (String)"addonService");
        Intrinsics.checkNotNullParameter((Object)contentService, (String)"contentService");
        Intrinsics.checkNotNullParameter((Object)settingsService, (String)"settingsService");
        this.contentAnalyticsService = contentAnalyticsService;
        this.addonService = addonService;
        this.contentService = contentService;
        this.settingsService = settingsService;
    }

    /*
     * WARNING - void declaration
     */
    @GET
    @Path(value="viewsByUser")
    @NotNull
    public final Response getViewsByUser(@QueryParam(value="contentType") @NotNull ContentTypeParam contentType, @QueryParam(value="contentId") long contentId) {
        void $this$mapTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)contentType, (String)"contentType");
        ContentRef contentRef = new ContentRef(contentType.getValue(), contentId);
        List<UserViews> viewsByUser2 = this.contentAnalyticsService.getViewsByUser(contentRef);
        Iterable $this$map$iv = viewsByUser2;
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            UserViews userViews = (UserViews)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            String string = it.getUserType().toString();
            String string2 = it.getUserKey();
            if (string2 == null) {
                string2 = "";
            }
            ContentVersion contentVersion = it.getLastVersionViewed();
            ContentVersion contentVersion2 = it.getLastVersionViewed();
            collection.add(new ContentUserViewsDto(string, string2, contentVersion != null ? Integer.valueOf(contentVersion.getVersion()) : null, contentVersion2 != null ? contentVersion2.getVersionUrl() : null, new ApiDateTime(it.getLastViewedAt()), it.getViews()));
        }
        List list = (List)destination$iv$iv;
        Response response = Response.ok((Object)new ContentViewsByUserDto(list)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    /*
     * WARNING - void declaration
     */
    @GET
    @Path(value="viewsByDate")
    @NotNull
    public final Response getViewsByPeriod(@QueryParam(value="fromDate") @NotNull OffsetDateTimeParam fromDate, @QueryParam(value="toDate") @NotNull OffsetDateTimeParam toDate, @QueryParam(value="period") @NotNull PeriodOptionParam period, @QueryParam(value="timezone") @NotNull ZoneIdParam timezone, @QueryParam(value="type") @NotNull CountTypeParam countType, @QueryParam(value="contentType") @NotNull ContentTypeParam contentType, @QueryParam(value="contentId") long contentId) {
        void $this$mapTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)fromDate, (String)"fromDate");
        Intrinsics.checkNotNullParameter((Object)toDate, (String)"toDate");
        Intrinsics.checkNotNullParameter((Object)period, (String)"period");
        Intrinsics.checkNotNullParameter((Object)timezone, (String)"timezone");
        Intrinsics.checkNotNullParameter((Object)countType, (String)"countType");
        Intrinsics.checkNotNullParameter((Object)contentType, (String)"contentType");
        ContentRef contentRef = new ContentRef(contentType.getValue(), contentId);
        DatePeriodOptions datePeriodOptions = DatePeriodOptionsKt.buildDatePeriodOptionsFromParams(fromDate, toDate, period, timezone);
        List<PeriodActivity> viewsByPeriod2 = this.contentAnalyticsService.getViewsByPeriod(datePeriodOptions, contentRef, countType.getValue());
        Iterable $this$map$iv = viewsByPeriod2;
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            PeriodActivity periodActivity = (PeriodActivity)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(new PeriodActivityDto(new ApiDateTime(it.getDate()), it.getTotal()));
        }
        List list = (List)destination$iv$iv;
        Response response = Response.ok((Object)new ContentViewsByPeriodDto(list)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    /*
     * WARNING - void declaration
     */
    @GET
    @Path(value="attachments/views")
    @NotNull
    public final Response getViewsByAttachment(@QueryParam(value="contentId") long contentId) {
        void $this$mapTo$iv$iv;
        List<AttachmentViews> viewsByAttachment2 = this.contentAnalyticsService.getViewsByAttachment(contentId);
        Iterable $this$map$iv = viewsByAttachment2;
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            AttachmentViews attachmentViews = (AttachmentViews)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(new AttachmentViewsDto(it.getId(), it.getName(), it.getLink(), new ApiDateTime(it.getLastViewedAt()), it.getViews()));
        }
        List list = (List)destination$iv$iv;
        Response response = Response.ok((Object)new ContentViewsByAttachmentDto(list)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    @GET
    @Path(value="details")
    @NotNull
    public final Response getDetails(@QueryParam(value="contentId") long contentId) {
        SampleDataDetailsDto sampleDataDetailsDto;
        Content content = this.contentService.getById(contentId);
        AddonDetails addonDetails = this.addonService.getDetails();
        DataRetentionSettings dataRetentionSettings = this.settingsService.getDataRetentionSettings();
        Instant minDate = (Instant)ComparisonsKt.maxOf((Comparable)addonDetails.getFirstEventAt(), (Comparable)dataRetentionSettings.getMinDate());
        SampleDataDetails sampleDataDetails = addonDetails.getSampleDataDetails();
        if (sampleDataDetails != null) {
            SampleDataDetails it = sampleDataDetails;
            boolean bl = false;
            sampleDataDetailsDto = new SampleDataDetailsDto(new ApiDateTime(it.getMinDate()), new ApiDateTime(it.getMaxDate()), new ApiDateTime(it.getLastUpdatedAt()));
        } else {
            sampleDataDetailsDto = null;
        }
        SampleDataDetailsDto sampleDataDetailsDto2 = sampleDataDetailsDto;
        AddonDetailsDto addonDetailsDto = new AddonDetailsDto(new ApiDateTime(minDate), sampleDataDetailsDto2);
        long l = content.getId();
        String string = content.getType().toString().toLowerCase();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"this as java.lang.String).toLowerCase()");
        Response response = Response.ok((Object)new ContentAnalyticsDetailsDto(addonDetailsDto, new ContentDetailsDto(l, string, content.getTitle(), new ApiDateTime(content.getCreatedAt()), content.getLink()), new SpaceDetailsDto(content.getSpace().getKey(), content.getSpace().getName(), new ApiDateTime(content.getSpace().getCreatedAt()), content.getSpace().getLink()))).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }
}

