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
 *  javax.ws.rs.core.Response$Status
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.comparisons.ComparisonsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.rest;

import com.addonengine.addons.analytics.rest.dto.ActivityByPeriodDto;
import com.addonengine.addons.analytics.rest.dto.ActivityByPeriodEventsDto;
import com.addonengine.addons.analytics.rest.dto.AddonDetailsDto;
import com.addonengine.addons.analytics.rest.dto.PeriodActivityDto;
import com.addonengine.addons.analytics.rest.dto.SampleDataDetailsDto;
import com.addonengine.addons.analytics.rest.dto.SpaceAnalyticsDetailsDto;
import com.addonengine.addons.analytics.rest.dto.SpaceDetailsDto;
import com.addonengine.addons.analytics.rest.filter.RateLimitFilter;
import com.addonengine.addons.analytics.rest.filter.UserHasPermissionToViewSpaceAnalyticsFilter;
import com.addonengine.addons.analytics.rest.filter.ValidAddonLicenseFilter;
import com.addonengine.addons.analytics.rest.util.ApiDateTime;
import com.addonengine.addons.analytics.rest.util.ContentTypeSetParam;
import com.addonengine.addons.analytics.rest.util.CountTypeParam;
import com.addonengine.addons.analytics.rest.util.DatePeriodOptionsKt;
import com.addonengine.addons.analytics.rest.util.OffsetDateTimeParam;
import com.addonengine.addons.analytics.rest.util.PeriodOptionParam;
import com.addonengine.addons.analytics.rest.util.ZoneIdParam;
import com.addonengine.addons.analytics.rest.util.excel.ExcelReportUtilsKt;
import com.addonengine.addons.analytics.service.AddonService;
import com.addonengine.addons.analytics.service.SettingsService;
import com.addonengine.addons.analytics.service.SpaceAnalyticsService;
import com.addonengine.addons.analytics.service.confluence.NoSpaceOrNoPermissionException;
import com.addonengine.addons.analytics.service.confluence.SpaceService;
import com.addonengine.addons.analytics.service.confluence.model.Space;
import com.addonengine.addons.analytics.service.excel.SpaceActivityByContentExcelService;
import com.addonengine.addons.analytics.service.excel.SpaceActivityByUserExcelService;
import com.addonengine.addons.analytics.service.excel.model.ExcelReport;
import com.addonengine.addons.analytics.service.model.ActivityByPeriod;
import com.addonengine.addons.analytics.service.model.AddonDetails;
import com.addonengine.addons.analytics.service.model.ContentType;
import com.addonengine.addons.analytics.service.model.DatePeriodOptions;
import com.addonengine.addons.analytics.service.model.PeriodActivity;
import com.addonengine.addons.analytics.service.model.SampleDataDetails;
import com.addonengine.addons.analytics.service.model.settings.DataRetentionSettings;
import com.sun.jersey.spi.container.ResourceFilters;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Path(value="/space")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000|\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B5\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\r\u00a2\u0006\u0002\u0010\u000eJ\u0010\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0002JD\u0010\u0013\u001a\u00020\u00142\b\b\u0001\u0010\u0015\u001a\u00020\u00162\b\b\u0001\u0010\u0017\u001a\u00020\u00162\b\b\u0001\u0010\u0018\u001a\u00020\u00192\b\b\u0001\u0010\u001a\u001a\u00020\u001b2\b\b\u0001\u0010\u001c\u001a\u00020\u001d2\b\b\u0001\u0010\u001e\u001a\u00020\u001fH\u0007JN\u0010 \u001a\u00020\u00142\b\b\u0001\u0010\u0015\u001a\u00020\u00162\b\b\u0001\u0010\u0017\u001a\u00020\u00162\b\b\u0001\u0010\u0018\u001a\u00020\u00192\b\b\u0001\u0010\u001a\u001a\u00020\u001b2\b\b\u0001\u0010\u001c\u001a\u00020\u001d2\b\b\u0001\u0010!\u001a\u00020\"2\b\b\u0001\u0010\u001e\u001a\u00020\u001fH\u0007JD\u0010#\u001a\u00020\u00142\b\b\u0001\u0010\u0015\u001a\u00020\u00162\b\b\u0001\u0010\u0017\u001a\u00020\u00162\b\b\u0001\u0010\u0018\u001a\u00020\u00192\b\b\u0001\u0010\u001a\u001a\u00020\u001b2\b\b\u0001\u0010\u001c\u001a\u00020\u001d2\b\b\u0001\u0010\u001e\u001a\u00020\u001fH\u0007J\u0012\u0010$\u001a\u00020\u00142\b\b\u0001\u0010\u001e\u001a\u00020\u001fH\u0007J\u0012\u0010%\u001a\u00020\u00142\b\b\u0001\u0010\u001e\u001a\u00020\u001fH\u0007J\u001c\u0010&\u001a\b\u0012\u0004\u0012\u00020(0'2\f\u0010)\u001a\b\u0012\u0004\u0012\u00020*0'H\u0002R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006+"}, d2={"Lcom/addonengine/addons/analytics/rest/SpaceAnalyticsResource;", "", "spaceAnalyticsService", "Lcom/addonengine/addons/analytics/service/SpaceAnalyticsService;", "spaceActivityByUserExcelService", "Lcom/addonengine/addons/analytics/service/excel/SpaceActivityByUserExcelService;", "spaceActivityByContentExcelService", "Lcom/addonengine/addons/analytics/service/excel/SpaceActivityByContentExcelService;", "addonService", "Lcom/addonengine/addons/analytics/service/AddonService;", "spaceService", "Lcom/addonengine/addons/analytics/service/confluence/SpaceService;", "settingsService", "Lcom/addonengine/addons/analytics/service/SettingsService;", "(Lcom/addonengine/addons/analytics/service/SpaceAnalyticsService;Lcom/addonengine/addons/analytics/service/excel/SpaceActivityByUserExcelService;Lcom/addonengine/addons/analytics/service/excel/SpaceActivityByContentExcelService;Lcom/addonengine/addons/analytics/service/AddonService;Lcom/addonengine/addons/analytics/service/confluence/SpaceService;Lcom/addonengine/addons/analytics/service/SettingsService;)V", "buildInstanceActivityByPeriodDto", "Lcom/addonengine/addons/analytics/rest/dto/ActivityByPeriodDto;", "activityByPeriod", "Lcom/addonengine/addons/analytics/service/model/ActivityByPeriod;", "getActivityByContentXlsx", "Ljavax/ws/rs/core/Response;", "fromDate", "Lcom/addonengine/addons/analytics/rest/util/OffsetDateTimeParam;", "toDate", "period", "Lcom/addonengine/addons/analytics/rest/util/PeriodOptionParam;", "timezone", "Lcom/addonengine/addons/analytics/rest/util/ZoneIdParam;", "contentTypeSet", "Lcom/addonengine/addons/analytics/rest/util/ContentTypeSetParam;", "spaceKey", "", "getActivityByPeriod", "countType", "Lcom/addonengine/addons/analytics/rest/util/CountTypeParam;", "getActivityByUserXlsx", "getDetails", "getSpaceLogo", "mapListOfPeriodActivityToDto", "", "Lcom/addonengine/addons/analytics/rest/dto/PeriodActivityDto;", "listOfPeriodActivity", "Lcom/addonengine/addons/analytics/service/model/PeriodActivity;", "analytics"})
@SourceDebugExtension(value={"SMAP\nSpaceAnalyticsResource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SpaceAnalyticsResource.kt\ncom/addonengine/addons/analytics/rest/SpaceAnalyticsResource\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,176:1\n1549#2:177\n1620#2,3:178\n*S KotlinDebug\n*F\n+ 1 SpaceAnalyticsResource.kt\ncom/addonengine/addons/analytics/rest/SpaceAnalyticsResource\n*L\n172#1:177\n172#1:178,3\n*E\n"})
public final class SpaceAnalyticsResource {
    @NotNull
    private final SpaceAnalyticsService spaceAnalyticsService;
    @NotNull
    private final SpaceActivityByUserExcelService spaceActivityByUserExcelService;
    @NotNull
    private final SpaceActivityByContentExcelService spaceActivityByContentExcelService;
    @NotNull
    private final AddonService addonService;
    @NotNull
    private final SpaceService spaceService;
    @NotNull
    private final SettingsService settingsService;

    public SpaceAnalyticsResource(@NotNull SpaceAnalyticsService spaceAnalyticsService, @NotNull SpaceActivityByUserExcelService spaceActivityByUserExcelService, @NotNull SpaceActivityByContentExcelService spaceActivityByContentExcelService, @NotNull AddonService addonService, @NotNull SpaceService spaceService, @NotNull SettingsService settingsService) {
        Intrinsics.checkNotNullParameter((Object)spaceAnalyticsService, (String)"spaceAnalyticsService");
        Intrinsics.checkNotNullParameter((Object)spaceActivityByUserExcelService, (String)"spaceActivityByUserExcelService");
        Intrinsics.checkNotNullParameter((Object)spaceActivityByContentExcelService, (String)"spaceActivityByContentExcelService");
        Intrinsics.checkNotNullParameter((Object)addonService, (String)"addonService");
        Intrinsics.checkNotNullParameter((Object)spaceService, (String)"spaceService");
        Intrinsics.checkNotNullParameter((Object)settingsService, (String)"settingsService");
        this.spaceAnalyticsService = spaceAnalyticsService;
        this.spaceActivityByUserExcelService = spaceActivityByUserExcelService;
        this.spaceActivityByContentExcelService = spaceActivityByContentExcelService;
        this.addonService = addonService;
        this.spaceService = spaceService;
        this.settingsService = settingsService;
    }

    @GET
    @Path(value="activityByUser.xlsx")
    @ResourceFilters(value={ValidAddonLicenseFilter.class, UserHasPermissionToViewSpaceAnalyticsFilter.class})
    @Produces(value={"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"})
    @NotNull
    public final Response getActivityByUserXlsx(@QueryParam(value="fromDate") @NotNull OffsetDateTimeParam fromDate, @QueryParam(value="toDate") @NotNull OffsetDateTimeParam toDate, @QueryParam(value="period") @NotNull PeriodOptionParam period, @QueryParam(value="timezone") @NotNull ZoneIdParam timezone, @QueryParam(value="content") @NotNull ContentTypeSetParam contentTypeSet, @QueryParam(value="spaceKey") @NotNull String spaceKey) {
        Intrinsics.checkNotNullParameter((Object)fromDate, (String)"fromDate");
        Intrinsics.checkNotNullParameter((Object)toDate, (String)"toDate");
        Intrinsics.checkNotNullParameter((Object)period, (String)"period");
        Intrinsics.checkNotNullParameter((Object)timezone, (String)"timezone");
        Intrinsics.checkNotNullParameter((Object)contentTypeSet, (String)"contentTypeSet");
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        DatePeriodOptions datePeriodOptions = DatePeriodOptionsKt.buildDatePeriodOptionsFromParams(fromDate, toDate, period, timezone);
        return ExcelReportUtilsKt.buildExcelReportAndResponse("Space Users Report.xlsx", (Function0<? extends ExcelReport>)((Function0)new Function0<ExcelReport>(this, datePeriodOptions, contentTypeSet, spaceKey){
            final /* synthetic */ SpaceAnalyticsResource this$0;
            final /* synthetic */ DatePeriodOptions $datePeriodOptions;
            final /* synthetic */ ContentTypeSetParam $contentTypeSet;
            final /* synthetic */ String $spaceKey;
            {
                this.this$0 = $receiver;
                this.$datePeriodOptions = $datePeriodOptions;
                this.$contentTypeSet = $contentTypeSet;
                this.$spaceKey = $spaceKey;
                super(0);
            }

            @NotNull
            public final ExcelReport invoke() {
                return SpaceAnalyticsResource.access$getSpaceActivityByUserExcelService$p(this.this$0).buildExcelReport(this.$datePeriodOptions, (Set<? extends ContentType>)this.$contentTypeSet.getValue(), this.$spaceKey);
            }
        }));
    }

    @GET
    @ResourceFilters(value={ValidAddonLicenseFilter.class, UserHasPermissionToViewSpaceAnalyticsFilter.class, RateLimitFilter.class})
    @Path(value="activityByDate")
    @NotNull
    public final Response getActivityByPeriod(@QueryParam(value="fromDate") @NotNull OffsetDateTimeParam fromDate, @QueryParam(value="toDate") @NotNull OffsetDateTimeParam toDate, @QueryParam(value="period") @NotNull PeriodOptionParam period, @QueryParam(value="timezone") @NotNull ZoneIdParam timezone, @QueryParam(value="content") @NotNull ContentTypeSetParam contentTypeSet, @QueryParam(value="type") @NotNull CountTypeParam countType, @QueryParam(value="spaceKey") @NotNull String spaceKey) {
        Intrinsics.checkNotNullParameter((Object)fromDate, (String)"fromDate");
        Intrinsics.checkNotNullParameter((Object)toDate, (String)"toDate");
        Intrinsics.checkNotNullParameter((Object)period, (String)"period");
        Intrinsics.checkNotNullParameter((Object)timezone, (String)"timezone");
        Intrinsics.checkNotNullParameter((Object)contentTypeSet, (String)"contentTypeSet");
        Intrinsics.checkNotNullParameter((Object)countType, (String)"countType");
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        DatePeriodOptions datePeriodOptions = DatePeriodOptionsKt.buildDatePeriodOptionsFromParams(fromDate, toDate, period, timezone);
        ActivityByPeriod activityByPeriod = this.spaceAnalyticsService.getActivityByPeriod(datePeriodOptions, (Set<? extends ContentType>)contentTypeSet.getValue(), countType.getValue(), spaceKey);
        Response response = Response.ok((Object)this.buildInstanceActivityByPeriodDto(activityByPeriod)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    @GET
    @Path(value="contentViews.xlsx")
    @ResourceFilters(value={ValidAddonLicenseFilter.class, UserHasPermissionToViewSpaceAnalyticsFilter.class})
    @Produces(value={"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"})
    @NotNull
    public final Response getActivityByContentXlsx(@QueryParam(value="fromDate") @NotNull OffsetDateTimeParam fromDate, @QueryParam(value="toDate") @NotNull OffsetDateTimeParam toDate, @QueryParam(value="period") @NotNull PeriodOptionParam period, @QueryParam(value="timezone") @NotNull ZoneIdParam timezone, @QueryParam(value="content") @NotNull ContentTypeSetParam contentTypeSet, @QueryParam(value="spaceKey") @NotNull String spaceKey) {
        Intrinsics.checkNotNullParameter((Object)fromDate, (String)"fromDate");
        Intrinsics.checkNotNullParameter((Object)toDate, (String)"toDate");
        Intrinsics.checkNotNullParameter((Object)period, (String)"period");
        Intrinsics.checkNotNullParameter((Object)timezone, (String)"timezone");
        Intrinsics.checkNotNullParameter((Object)contentTypeSet, (String)"contentTypeSet");
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        DatePeriodOptions datePeriodOptions = DatePeriodOptionsKt.buildDatePeriodOptionsFromParams(fromDate, toDate, period, timezone);
        return ExcelReportUtilsKt.buildExcelReportAndResponse("Space Contents Report.xlsx", (Function0<? extends ExcelReport>)((Function0)new Function0<ExcelReport>(this, datePeriodOptions, contentTypeSet, spaceKey){
            final /* synthetic */ SpaceAnalyticsResource this$0;
            final /* synthetic */ DatePeriodOptions $datePeriodOptions;
            final /* synthetic */ ContentTypeSetParam $contentTypeSet;
            final /* synthetic */ String $spaceKey;
            {
                this.this$0 = $receiver;
                this.$datePeriodOptions = $datePeriodOptions;
                this.$contentTypeSet = $contentTypeSet;
                this.$spaceKey = $spaceKey;
                super(0);
            }

            @NotNull
            public final ExcelReport invoke() {
                return SpaceAnalyticsResource.access$getSpaceActivityByContentExcelService$p(this.this$0).buildExcelReport(this.$datePeriodOptions, (Set<? extends ContentType>)this.$contentTypeSet.getValue(), this.$spaceKey);
            }
        }));
    }

    @GET
    @Path(value="details")
    @ResourceFilters(value={ValidAddonLicenseFilter.class, UserHasPermissionToViewSpaceAnalyticsFilter.class})
    @NotNull
    public final Response getDetails(@QueryParam(value="spaceKey") @NotNull String spaceKey) {
        SampleDataDetailsDto sampleDataDetailsDto;
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        Space space = this.spaceService.getByKey(spaceKey);
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
        Response response = Response.ok((Object)new SpaceAnalyticsDetailsDto(new AddonDetailsDto(new ApiDateTime(minDate), sampleDataDetailsDto2), new SpaceDetailsDto(space.getKey(), space.getName(), new ApiDateTime(space.getCreatedAt()), space.getLink()))).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    @GET
    @Path(value="logo")
    @ResourceFilters(value={ValidAddonLicenseFilter.class})
    @NotNull
    public final Response getSpaceLogo(@QueryParam(value="spaceKey") @NotNull String spaceKey) {
        Response response;
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        try {
            response = Response.seeOther((URI)SpaceService.DefaultImpls.getSpacesLogoUrl$default(this.spaceService, this.spaceService.getByKey(spaceKey).getKey(), false, 2, null).toURI()).build();
            Intrinsics.checkNotNull((Object)response);
        }
        catch (NoSpaceOrNoPermissionException e) {
            Response response2 = Response.status((Response.Status)Response.Status.NOT_FOUND).build();
            Intrinsics.checkNotNull((Object)response2);
            response = response2;
        }
        catch (Exception e) {
            Response response3 = Response.seeOther((URI)this.spaceService.getDefaultSpacesLogoUrl().toURI()).build();
            Intrinsics.checkNotNull((Object)response3);
            response = response3;
        }
        return response;
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

    public static final /* synthetic */ SpaceActivityByUserExcelService access$getSpaceActivityByUserExcelService$p(SpaceAnalyticsResource $this) {
        return $this.spaceActivityByUserExcelService;
    }

    public static final /* synthetic */ SpaceActivityByContentExcelService access$getSpaceActivityByContentExcelService$p(SpaceAnalyticsResource $this) {
        return $this.spaceActivityByContentExcelService;
    }
}

