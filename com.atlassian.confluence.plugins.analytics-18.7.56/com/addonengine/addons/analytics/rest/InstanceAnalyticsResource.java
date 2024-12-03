/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceSystemProperties
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.comparisons.ComparisonsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.rest;

import com.addonengine.addons.analytics.rest.dto.ActivityByPeriodDto;
import com.addonengine.addons.analytics.rest.dto.ActivityByPeriodEventsDto;
import com.addonengine.addons.analytics.rest.dto.AddonDetailsDto;
import com.addonengine.addons.analytics.rest.dto.InstanceAnalyticsDetailsDto;
import com.addonengine.addons.analytics.rest.dto.PeriodActivityDto;
import com.addonengine.addons.analytics.rest.dto.SampleDataDetailsDto;
import com.addonengine.addons.analytics.rest.filter.RateLimitFilter;
import com.addonengine.addons.analytics.rest.filter.UserHasPermissionToViewInstanceAnalyticsFilter;
import com.addonengine.addons.analytics.rest.filter.UserIsSystemAdminFilter;
import com.addonengine.addons.analytics.rest.filter.ValidAddonLicenseFilter;
import com.addonengine.addons.analytics.rest.util.ApiDateTime;
import com.addonengine.addons.analytics.rest.util.ContentTypeSetParam;
import com.addonengine.addons.analytics.rest.util.CountTypeParam;
import com.addonengine.addons.analytics.rest.util.DatePeriodOptionsKt;
import com.addonengine.addons.analytics.rest.util.OffsetDateTimeParam;
import com.addonengine.addons.analytics.rest.util.PeriodOptionParam;
import com.addonengine.addons.analytics.rest.util.SpaceTypeParam;
import com.addonengine.addons.analytics.rest.util.ZoneIdParam;
import com.addonengine.addons.analytics.rest.util.excel.ExcelReportUtilsKt;
import com.addonengine.addons.analytics.service.AddonService;
import com.addonengine.addons.analytics.service.InstanceAnalyticsService;
import com.addonengine.addons.analytics.service.SettingsService;
import com.addonengine.addons.analytics.service.excel.InstanceActivityBySpaceExcelService;
import com.addonengine.addons.analytics.service.excel.InstanceActivityByUserExcelService;
import com.addonengine.addons.analytics.service.excel.model.ExcelReport;
import com.addonengine.addons.analytics.service.model.ActivityByPeriod;
import com.addonengine.addons.analytics.service.model.AddonDetails;
import com.addonengine.addons.analytics.service.model.ContentType;
import com.addonengine.addons.analytics.service.model.DatePeriodOptions;
import com.addonengine.addons.analytics.service.model.PeriodActivity;
import com.addonengine.addons.analytics.service.model.SampleDataDetails;
import com.addonengine.addons.analytics.service.model.SpaceType;
import com.addonengine.addons.analytics.service.model.settings.DataRetentionSettings;
import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.sun.jersey.spi.container.ResourceFilters;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Path(value="/instance")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000~\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\fJ\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0002JN\u0010\u0011\u001a\u00020\u00122\b\b\u0001\u0010\u0013\u001a\u00020\u00142\b\b\u0001\u0010\u0015\u001a\u00020\u00142\b\b\u0001\u0010\u0016\u001a\u00020\u00172\b\b\u0001\u0010\u0018\u001a\u00020\u00192\b\b\u0001\u0010\u001a\u001a\u00020\u001b2\b\b\u0001\u0010\u001c\u001a\u00020\u001d2\b\b\u0001\u0010\u001e\u001a\u00020\u001fH\u0007JD\u0010 \u001a\u00020\u00122\b\b\u0001\u0010\u0013\u001a\u00020\u00142\b\b\u0001\u0010\u0015\u001a\u00020\u00142\b\b\u0001\u0010\u0016\u001a\u00020\u00172\b\b\u0001\u0010\u0018\u001a\u00020\u00192\b\b\u0001\u0010\u001a\u001a\u00020\u001b2\b\b\u0001\u0010\u001c\u001a\u00020\u001dH\u0007JD\u0010!\u001a\u00020\u00122\b\b\u0001\u0010\u0013\u001a\u00020\u00142\b\b\u0001\u0010\u0015\u001a\u00020\u00142\b\b\u0001\u0010\u0016\u001a\u00020\u00172\b\b\u0001\u0010\u0018\u001a\u00020\u00192\b\b\u0001\u0010\u001a\u001a\u00020\u001b2\b\b\u0001\u0010\u001c\u001a\u00020\u001dH\u0007J\b\u0010\"\u001a\u00020\u0012H\u0007J\u001c\u0010#\u001a\b\u0012\u0004\u0012\u00020%0$2\f\u0010&\u001a\b\u0012\u0004\u0012\u00020'0$H\u0002J%\u0010(\u001a\u00020\u00122\n\b\u0003\u0010)\u001a\u0004\u0018\u00010*2\n\b\u0003\u0010+\u001a\u0004\u0018\u00010*H\u0007\u00a2\u0006\u0002\u0010,R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006-"}, d2={"Lcom/addonengine/addons/analytics/rest/InstanceAnalyticsResource;", "", "instanceAnalyticsService", "Lcom/addonengine/addons/analytics/service/InstanceAnalyticsService;", "instanceActivityByUserExcelService", "Lcom/addonengine/addons/analytics/service/excel/InstanceActivityByUserExcelService;", "instanceActivityBySpaceExcelService", "Lcom/addonengine/addons/analytics/service/excel/InstanceActivityBySpaceExcelService;", "addonService", "Lcom/addonengine/addons/analytics/service/AddonService;", "settingsService", "Lcom/addonengine/addons/analytics/service/SettingsService;", "(Lcom/addonengine/addons/analytics/service/InstanceAnalyticsService;Lcom/addonengine/addons/analytics/service/excel/InstanceActivityByUserExcelService;Lcom/addonengine/addons/analytics/service/excel/InstanceActivityBySpaceExcelService;Lcom/addonengine/addons/analytics/service/AddonService;Lcom/addonengine/addons/analytics/service/SettingsService;)V", "buildInstanceActivityByPeriodDto", "Lcom/addonengine/addons/analytics/rest/dto/ActivityByPeriodDto;", "activityByPeriod", "Lcom/addonengine/addons/analytics/service/model/ActivityByPeriod;", "getActivityByPeriod", "Ljavax/ws/rs/core/Response;", "fromDate", "Lcom/addonengine/addons/analytics/rest/util/OffsetDateTimeParam;", "toDate", "period", "Lcom/addonengine/addons/analytics/rest/util/PeriodOptionParam;", "timezone", "Lcom/addonengine/addons/analytics/rest/util/ZoneIdParam;", "spaceType", "Lcom/addonengine/addons/analytics/rest/util/SpaceTypeParam;", "contentTypeSet", "Lcom/addonengine/addons/analytics/rest/util/ContentTypeSetParam;", "countType", "Lcom/addonengine/addons/analytics/rest/util/CountTypeParam;", "getActivityBySpaceXlsx", "getActivityByUserXlsx", "getDetails", "mapListOfPeriodActivityToDto", "", "Lcom/addonengine/addons/analytics/rest/dto/PeriodActivityDto;", "listOfPeriodActivity", "Lcom/addonengine/addons/analytics/service/model/PeriodActivity;", "postSampleData", "fromTime", "", "toTime", "(Ljava/lang/Long;Ljava/lang/Long;)Ljavax/ws/rs/core/Response;", "analytics"})
@SourceDebugExtension(value={"SMAP\nInstanceAnalyticsResource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InstanceAnalyticsResource.kt\ncom/addonengine/addons/analytics/rest/InstanceAnalyticsResource\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,170:1\n1549#2:171\n1620#2,3:172\n*S KotlinDebug\n*F\n+ 1 InstanceAnalyticsResource.kt\ncom/addonengine/addons/analytics/rest/InstanceAnalyticsResource\n*L\n166#1:171\n166#1:172,3\n*E\n"})
public final class InstanceAnalyticsResource {
    @NotNull
    private final InstanceAnalyticsService instanceAnalyticsService;
    @NotNull
    private final InstanceActivityByUserExcelService instanceActivityByUserExcelService;
    @NotNull
    private final InstanceActivityBySpaceExcelService instanceActivityBySpaceExcelService;
    @NotNull
    private final AddonService addonService;
    @NotNull
    private final SettingsService settingsService;

    public InstanceAnalyticsResource(@NotNull InstanceAnalyticsService instanceAnalyticsService, @NotNull InstanceActivityByUserExcelService instanceActivityByUserExcelService, @NotNull InstanceActivityBySpaceExcelService instanceActivityBySpaceExcelService, @NotNull AddonService addonService, @NotNull SettingsService settingsService) {
        Intrinsics.checkNotNullParameter((Object)instanceAnalyticsService, (String)"instanceAnalyticsService");
        Intrinsics.checkNotNullParameter((Object)instanceActivityByUserExcelService, (String)"instanceActivityByUserExcelService");
        Intrinsics.checkNotNullParameter((Object)instanceActivityBySpaceExcelService, (String)"instanceActivityBySpaceExcelService");
        Intrinsics.checkNotNullParameter((Object)addonService, (String)"addonService");
        Intrinsics.checkNotNullParameter((Object)settingsService, (String)"settingsService");
        this.instanceAnalyticsService = instanceAnalyticsService;
        this.instanceActivityByUserExcelService = instanceActivityByUserExcelService;
        this.instanceActivityBySpaceExcelService = instanceActivityBySpaceExcelService;
        this.addonService = addonService;
        this.settingsService = settingsService;
    }

    @GET
    @Path(value="activityByUser.xlsx")
    @ResourceFilters(value={ValidAddonLicenseFilter.class, UserHasPermissionToViewInstanceAnalyticsFilter.class, RateLimitFilter.class})
    @Produces(value={"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"})
    @NotNull
    public final Response getActivityByUserXlsx(@QueryParam(value="fromDate") @NotNull OffsetDateTimeParam fromDate, @QueryParam(value="toDate") @NotNull OffsetDateTimeParam toDate, @QueryParam(value="period") @NotNull PeriodOptionParam period, @QueryParam(value="timezone") @NotNull ZoneIdParam timezone, @QueryParam(value="spaceType") @NotNull SpaceTypeParam spaceType, @QueryParam(value="content") @NotNull ContentTypeSetParam contentTypeSet) {
        Intrinsics.checkNotNullParameter((Object)fromDate, (String)"fromDate");
        Intrinsics.checkNotNullParameter((Object)toDate, (String)"toDate");
        Intrinsics.checkNotNullParameter((Object)period, (String)"period");
        Intrinsics.checkNotNullParameter((Object)timezone, (String)"timezone");
        Intrinsics.checkNotNullParameter((Object)spaceType, (String)"spaceType");
        Intrinsics.checkNotNullParameter((Object)contentTypeSet, (String)"contentTypeSet");
        DatePeriodOptions datePeriodOptions = DatePeriodOptionsKt.buildDatePeriodOptionsFromParams(fromDate, toDate, period, timezone);
        return ExcelReportUtilsKt.buildExcelReportAndResponse("Users Report.xlsx", (Function0<? extends ExcelReport>)((Function0)new Function0<ExcelReport>(this, datePeriodOptions, spaceType, contentTypeSet){
            final /* synthetic */ InstanceAnalyticsResource this$0;
            final /* synthetic */ DatePeriodOptions $datePeriodOptions;
            final /* synthetic */ SpaceTypeParam $spaceType;
            final /* synthetic */ ContentTypeSetParam $contentTypeSet;
            {
                this.this$0 = $receiver;
                this.$datePeriodOptions = $datePeriodOptions;
                this.$spaceType = $spaceType;
                this.$contentTypeSet = $contentTypeSet;
                super(0);
            }

            @NotNull
            public final ExcelReport invoke() {
                return InstanceAnalyticsResource.access$getInstanceActivityByUserExcelService$p(this.this$0).buildExcelReport(this.$datePeriodOptions, (Set<? extends SpaceType>)this.$spaceType.getValue(), (Set<? extends ContentType>)this.$contentTypeSet.getValue());
            }
        }));
    }

    @GET
    @Path(value="activityByDate")
    @ResourceFilters(value={ValidAddonLicenseFilter.class, UserHasPermissionToViewInstanceAnalyticsFilter.class, RateLimitFilter.class})
    @NotNull
    public final Response getActivityByPeriod(@QueryParam(value="fromDate") @NotNull OffsetDateTimeParam fromDate, @QueryParam(value="toDate") @NotNull OffsetDateTimeParam toDate, @QueryParam(value="period") @NotNull PeriodOptionParam period, @QueryParam(value="timezone") @NotNull ZoneIdParam timezone, @QueryParam(value="spaceType") @NotNull SpaceTypeParam spaceType, @QueryParam(value="content") @NotNull ContentTypeSetParam contentTypeSet, @QueryParam(value="type") @NotNull CountTypeParam countType) {
        Intrinsics.checkNotNullParameter((Object)fromDate, (String)"fromDate");
        Intrinsics.checkNotNullParameter((Object)toDate, (String)"toDate");
        Intrinsics.checkNotNullParameter((Object)period, (String)"period");
        Intrinsics.checkNotNullParameter((Object)timezone, (String)"timezone");
        Intrinsics.checkNotNullParameter((Object)spaceType, (String)"spaceType");
        Intrinsics.checkNotNullParameter((Object)contentTypeSet, (String)"contentTypeSet");
        Intrinsics.checkNotNullParameter((Object)countType, (String)"countType");
        DatePeriodOptions datePeriodOptions = DatePeriodOptionsKt.buildDatePeriodOptionsFromParams(fromDate, toDate, period, timezone);
        ActivityByPeriod activityByPeriod = this.instanceAnalyticsService.getActivityByPeriod(datePeriodOptions, (Set<? extends SpaceType>)spaceType.getValue(), (Set<? extends ContentType>)contentTypeSet.getValue(), countType.getValue());
        Response response = Response.ok((Object)this.buildInstanceActivityByPeriodDto(activityByPeriod)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    @GET
    @Path(value="activityBySpace.xlsx")
    @ResourceFilters(value={ValidAddonLicenseFilter.class, UserHasPermissionToViewInstanceAnalyticsFilter.class, RateLimitFilter.class})
    @Produces(value={"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"})
    @NotNull
    public final Response getActivityBySpaceXlsx(@QueryParam(value="fromDate") @NotNull OffsetDateTimeParam fromDate, @QueryParam(value="toDate") @NotNull OffsetDateTimeParam toDate, @QueryParam(value="period") @NotNull PeriodOptionParam period, @QueryParam(value="timezone") @NotNull ZoneIdParam timezone, @QueryParam(value="spaceType") @NotNull SpaceTypeParam spaceType, @QueryParam(value="content") @NotNull ContentTypeSetParam contentTypeSet) {
        Intrinsics.checkNotNullParameter((Object)fromDate, (String)"fromDate");
        Intrinsics.checkNotNullParameter((Object)toDate, (String)"toDate");
        Intrinsics.checkNotNullParameter((Object)period, (String)"period");
        Intrinsics.checkNotNullParameter((Object)timezone, (String)"timezone");
        Intrinsics.checkNotNullParameter((Object)spaceType, (String)"spaceType");
        Intrinsics.checkNotNullParameter((Object)contentTypeSet, (String)"contentTypeSet");
        DatePeriodOptions datePeriodOptions = DatePeriodOptionsKt.buildDatePeriodOptionsFromParams(fromDate, toDate, period, timezone);
        return ExcelReportUtilsKt.buildExcelReportAndResponse("Spaces Report.xlsx", (Function0<? extends ExcelReport>)((Function0)new Function0<ExcelReport>(this, datePeriodOptions, spaceType, contentTypeSet){
            final /* synthetic */ InstanceAnalyticsResource this$0;
            final /* synthetic */ DatePeriodOptions $datePeriodOptions;
            final /* synthetic */ SpaceTypeParam $spaceType;
            final /* synthetic */ ContentTypeSetParam $contentTypeSet;
            {
                this.this$0 = $receiver;
                this.$datePeriodOptions = $datePeriodOptions;
                this.$spaceType = $spaceType;
                this.$contentTypeSet = $contentTypeSet;
                super(0);
            }

            @NotNull
            public final ExcelReport invoke() {
                return InstanceAnalyticsResource.access$getInstanceActivityBySpaceExcelService$p(this.this$0).buildExcelReport(this.$datePeriodOptions, (Set<? extends SpaceType>)this.$spaceType.getValue(), (Set<? extends ContentType>)this.$contentTypeSet.getValue());
            }
        }));
    }

    @GET
    @Path(value="details")
    @ResourceFilters(value={ValidAddonLicenseFilter.class, UserHasPermissionToViewInstanceAnalyticsFilter.class})
    @NotNull
    public final Response getDetails() {
        SampleDataDetailsDto sampleDataDetailsDto;
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
        Response response = Response.ok((Object)new InstanceAnalyticsDetailsDto(new AddonDetailsDto(new ApiDateTime(minDate), sampleDataDetailsDto2))).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    @POST
    @Path(value="sampleData")
    @ResourceFilters(value={ValidAddonLicenseFilter.class, UserIsSystemAdminFilter.class})
    @NotNull
    public final Response postSampleData(@QueryParam(value="fromTime") @Nullable Long fromTime, @QueryParam(value="toTime") @Nullable Long toTime) {
        Response response;
        if (ConfluenceSystemProperties.isDevMode()) {
            this.addonService.buildSampleData(fromTime, toTime);
            Response response2 = Response.ok().build();
            Intrinsics.checkNotNull((Object)response2);
            response = response2;
        } else {
            Response response3 = Response.status((Response.Status)Response.Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE).entity((Object)"Test data generation is only available in dev mode").build();
            Intrinsics.checkNotNull((Object)response3);
            response = response3;
        }
        return response;
    }

    public static /* synthetic */ Response postSampleData$default(InstanceAnalyticsResource instanceAnalyticsResource, Long l, Long l2, int n, Object object) {
        if ((n & 1) != 0) {
            l = null;
        }
        if ((n & 2) != 0) {
            l2 = null;
        }
        return instanceAnalyticsResource.postSampleData(l, l2);
    }

    private final ActivityByPeriodDto buildInstanceActivityByPeriodDto(ActivityByPeriod activityByPeriod) {
        return new ActivityByPeriodDto(new ActivityByPeriodEventsDto(this.mapListOfPeriodActivityToDto(activityByPeriod.getCreates()), this.mapListOfPeriodActivityToDto(activityByPeriod.getUpdates()), this.mapListOfPeriodActivityToDto(activityByPeriod.getViews())));
    }

    /*
     * WARNING - void declaration
     */
    private final List<PeriodActivityDto> mapListOfPeriodActivityToDto(List<PeriodActivity> listOfPeriodActivity) {
        void $this$mapTo$iv$iv;
        Iterable $this$map$iv = listOfPeriodActivity;
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
        return (List)destination$iv$iv;
    }

    public static final /* synthetic */ InstanceActivityByUserExcelService access$getInstanceActivityByUserExcelService$p(InstanceAnalyticsResource $this) {
        return $this.instanceActivityByUserExcelService;
    }

    public static final /* synthetic */ InstanceActivityBySpaceExcelService access$getInstanceActivityBySpaceExcelService$p(InstanceAnalyticsResource $this) {
        return $this.instanceActivityBySpaceExcelService;
    }
}

