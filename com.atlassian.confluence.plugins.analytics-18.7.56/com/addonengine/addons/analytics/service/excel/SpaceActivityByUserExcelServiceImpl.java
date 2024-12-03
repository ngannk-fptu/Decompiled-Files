/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.inject.Named
 *  kotlin.Lazy
 *  kotlin.LazyKt
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.sequences.Sequence
 *  org.apache.poi.xssf.streaming.SXSSFSheet
 *  org.apache.poi.xssf.streaming.SXSSFWorkbook
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.addonengine.addons.analytics.service.excel;

import com.addonengine.addons.analytics.service.SpacePaginatedAnalyticsService;
import com.addonengine.addons.analytics.service.confluence.UserService;
import com.addonengine.addons.analytics.service.confluence.model.User;
import com.addonengine.addons.analytics.service.excel.SpaceActivityByUserExcelService;
import com.addonengine.addons.analytics.service.excel.SpaceActivityByUserExcelServiceImpl;
import com.addonengine.addons.analytics.service.excel.model.ExcelReport;
import com.addonengine.addons.analytics.service.excel.poi.ExcelCellStyles;
import com.addonengine.addons.analytics.service.excel.poi.ExcelColumnHeader;
import com.addonengine.addons.analytics.service.excel.poi.ExcelReportSXSSFWorkbookImpl;
import com.addonengine.addons.analytics.service.excel.poi.ExcelSXSSFService;
import com.addonengine.addons.analytics.service.model.AnalyticsEvent;
import com.addonengine.addons.analytics.service.model.ContentType;
import com.addonengine.addons.analytics.service.model.DatePeriodOptions;
import com.addonengine.addons.analytics.service.model.LazyFetching;
import com.addonengine.addons.analytics.service.model.SortOrder;
import com.addonengine.addons.analytics.service.model.SpaceLevelUserActivity;
import com.addonengine.addons.analytics.service.model.SpaceLevelUserSortField;
import com.addonengine.addons.analytics.service.model.userDetailsActivity;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Named;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000l\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\b\u0007\u0018\u00002\u00020\u00012\u00020\u0002B!\b\u0007\u0012\b\b\u0001\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\tJ&\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00170\u00162\u0006\u0010\u0018\u001a\u00020\u0019H\u0016J)\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001d2\u0012\u0010\u001e\u001a\n\u0012\u0006\b\u0001\u0012\u00020 0\u001f\"\u00020 H\u0002\u00a2\u0006\u0002\u0010!R\u0018\u0010\n\u001a\n \f*\u0004\u0018\u00010\u000b0\u000bX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\rR\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\"\u00b2\u0006\n\u0010#\u001a\u00020$X\u008a\u0084\u0002"}, d2={"Lcom/addonengine/addons/analytics/service/excel/SpaceActivityByUserExcelServiceImpl;", "Lcom/addonengine/addons/analytics/service/excel/poi/ExcelSXSSFService;", "Lcom/addonengine/addons/analytics/service/excel/SpaceActivityByUserExcelService;", "i18n", "Lcom/atlassian/sal/api/message/I18nResolver;", "paginatedService", "Lcom/addonengine/addons/analytics/service/SpacePaginatedAnalyticsService;", "userService", "Lcom/addonengine/addons/analytics/service/confluence/UserService;", "(Lcom/atlassian/sal/api/message/I18nResolver;Lcom/addonengine/addons/analytics/service/SpacePaginatedAnalyticsService;Lcom/addonengine/addons/analytics/service/confluence/UserService;)V", "batchSize", "", "kotlin.jvm.PlatformType", "Ljava/lang/Integer;", "columnHeaders", "", "Lcom/addonengine/addons/analytics/service/excel/poi/ExcelColumnHeader;", "buildExcelReport", "Lcom/addonengine/addons/analytics/service/excel/model/ExcelReport;", "datePeriodOptions", "Lcom/addonengine/addons/analytics/service/model/DatePeriodOptions;", "contentTypes", "", "Lcom/addonengine/addons/analytics/service/model/ContentType;", "spaceKey", "", "calculateEventTotal", "", "userActivity", "Lcom/addonengine/addons/analytics/service/model/userDetailsActivity;", "events", "", "Lcom/addonengine/addons/analytics/service/model/AnalyticsEvent;", "(Lcom/addonengine/addons/analytics/service/model/userDetailsActivity;[Lcom/addonengine/addons/analytics/service/model/AnalyticsEvent;)J", "analytics", "anonUser", "Lcom/addonengine/addons/analytics/service/confluence/model/User;"})
@SourceDebugExtension(value={"SMAP\nSpaceActivityByUserExcelServiceImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SpaceActivityByUserExcelServiceImpl.kt\ncom/addonengine/addons/analytics/service/excel/SpaceActivityByUserExcelServiceImpl\n+ 2 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,115:1\n674#2:116\n704#2,4:117\n1549#3:121\n1620#3,3:122\n1194#3,2:125\n1222#3,4:127\n1864#3,3:131\n12720#4,3:134\n*S KotlinDebug\n*F\n+ 1 SpaceActivityByUserExcelServiceImpl.kt\ncom/addonengine/addons/analytics/service/excel/SpaceActivityByUserExcelServiceImpl\n*L\n79#1:116\n79#1:117,4\n82#1:121\n82#1:122,3\n84#1:125,2\n84#1:127,4\n96#1:131,3\n111#1:134,3\n*E\n"})
public final class SpaceActivityByUserExcelServiceImpl
extends ExcelSXSSFService
implements SpaceActivityByUserExcelService {
    @NotNull
    private final I18nResolver i18n;
    @NotNull
    private final SpacePaginatedAnalyticsService paginatedService;
    @NotNull
    private final UserService userService;
    private final Integer batchSize;
    @NotNull
    private final List<ExcelColumnHeader> columnHeaders;

    @Autowired
    public SpaceActivityByUserExcelServiceImpl(@ComponentImport @NotNull I18nResolver i18n, @NotNull SpacePaginatedAnalyticsService paginatedService, @NotNull UserService userService) {
        Intrinsics.checkNotNullParameter((Object)i18n, (String)"i18n");
        Intrinsics.checkNotNullParameter((Object)paginatedService, (String)"paginatedService");
        Intrinsics.checkNotNullParameter((Object)userService, (String)"userService");
        super(i18n);
        this.i18n = i18n;
        this.paginatedService = paginatedService;
        this.userService = userService;
        this.batchSize = Integer.getInteger("confluence.analytics.pagination.xls.batch.size", 25000);
        Object[] objectArray = new ExcelColumnHeader[]{new ExcelColumnHeader("com.addonengine.addons.analytics.excel.space.user.header.name", 40, false, 4, null), new ExcelColumnHeader("com.addonengine.addons.analytics.excel.space.user.header.email", 40, false, 4, null), new ExcelColumnHeader("com.addonengine.addons.analytics.excel.space.user.header.created", 20, false, 4, null), new ExcelColumnHeader("com.addonengine.addons.analytics.excel.space.user.header.updated", 20, false, 4, null), new ExcelColumnHeader("com.addonengine.addons.analytics.excel.space.user.header.comments", 15, false, 4, null), new ExcelColumnHeader("com.addonengine.addons.analytics.excel.space.user.header.views", 10, false, 4, null)};
        this.columnHeaders = CollectionsKt.listOf((Object[])objectArray);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public ExcelReport buildExcelReport(@NotNull DatePeriodOptions datePeriodOptions, @NotNull Set<? extends ContentType> contentTypes, @NotNull String spaceKey) {
        void $this$associateByTo$iv$iv;
        void $this$associateBy$iv;
        void $this$mapTo$iv$iv;
        Object object;
        void $this$associateByTo$iv$iv2;
        void $this$associateBy$iv2;
        Intrinsics.checkNotNullParameter((Object)datePeriodOptions, (String)"datePeriodOptions");
        Intrinsics.checkNotNullParameter(contentTypes, (String)"contentTypes");
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        Integer n = this.batchSize;
        Intrinsics.checkNotNullExpressionValue((Object)n, (String)"batchSize");
        int n2 = ((Number)n).intValue();
        Integer n3 = this.batchSize;
        Intrinsics.checkNotNullExpressionValue((Object)n3, (String)"batchSize");
        Sequence sequence = LazyFetching.Companion.seek(n2, ((Number)n3).intValue(), (Function2)new Function2<String, Integer, List<? extends SpaceLevelUserActivity>>(this, datePeriodOptions, spaceKey, contentTypes){
            final /* synthetic */ SpaceActivityByUserExcelServiceImpl this$0;
            final /* synthetic */ DatePeriodOptions $datePeriodOptions;
            final /* synthetic */ String $spaceKey;
            final /* synthetic */ Set<ContentType> $contentTypes;
            {
                this.this$0 = $receiver;
                this.$datePeriodOptions = $datePeriodOptions;
                this.$spaceKey = $spaceKey;
                this.$contentTypes = $contentTypes;
                super(2);
            }

            @NotNull
            public final List<SpaceLevelUserActivity> invoke(@Nullable String offset, int limit) {
                return SpaceActivityByUserExcelServiceImpl.access$getPaginatedService$p(this.this$0).getActivityByUser(this.$datePeriodOptions, this.$spaceKey, this.$contentTypes, offset, limit, SpaceLevelUserSortField.VIEWED_COUNT, SortOrder.DESC);
            }
        }, buildExcelReport.activityByUser.2.INSTANCE).asSequence();
        boolean $i$f$associateBy = false;
        void var7_7 = $this$associateBy$iv2;
        Map destination$iv$iv = new LinkedHashMap();
        boolean $i$f$associateByTo22 = false;
        for (Object element$iv$iv : $this$associateByTo$iv$iv2) {
            Iterator it;
            SpaceLevelUserActivity spaceLevelUserActivity = (SpaceLevelUserActivity)element$iv$iv;
            object = destination$iv$iv;
            boolean bl = false;
            object.put(((SpaceLevelUserActivity)((Object)it)).getUserKey(), element$iv$iv);
        }
        Map activityByUser2 = destination$iv$iv;
        Lazy anonUser$delegate = LazyKt.lazy((Function0)((Function0)new Function0<User>(this){
            final /* synthetic */ SpaceActivityByUserExcelServiceImpl this$0;
            {
                this.this$0 = $receiver;
                super(0);
            }

            @NotNull
            public final User invoke() {
                return SpaceActivityByUserExcelServiceImpl.access$getUserService$p(this.this$0).getAnonymousUserDetails();
            }
        }));
        Iterable $this$map$iv = activityByUser2.keySet();
        boolean $i$f$map = false;
        Iterable $i$f$associateByTo22 = $this$map$iv;
        Collection destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            String string = (String)item$iv$iv;
            object = destination$iv$iv2;
            boolean bl = false;
            object.add(it == null || Intrinsics.areEqual((Object)it, (Object)"[anonymous]") ? SpaceActivityByUserExcelServiceImpl.buildExcelReport$lambda$1((Lazy<User>)anonUser$delegate) : this.userService.getUserDetails((String)it));
        }
        $this$map$iv = (List)destination$iv$iv2;
        boolean $i$f$associateBy2 = false;
        int capacity$iv = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateBy$iv, (int)10)), (int)16);
        destination$iv$iv2 = $this$associateBy$iv;
        Map destination$iv$iv3 = new LinkedHashMap(capacity$iv);
        boolean $i$f$associateByTo = false;
        for (Object element$iv$iv : $this$associateByTo$iv$iv) {
            void it;
            User bl = (User)element$iv$iv;
            object = destination$iv$iv3;
            boolean bl2 = false;
            object.put(it.getUserKey(), element$iv$iv);
        }
        Map detailsByUser = destination$iv$iv3;
        SXSSFWorkbook workbook = this.createWorkbook();
        ExcelCellStyles cellStyles = this.buildDefaultWorkbookStyles(workbook);
        SXSSFSheet sheet = this.createWorksheet(workbook, "com.addonengine.addons.analytics.excel.space.user.sheetName");
        this.buildColumnHeaderRow(sheet, cellStyles, this.columnHeaders);
        Iterable $this$forEachIndexed$iv = activityByUser2.entrySet();
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            int n4;
            if ((n4 = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            Map.Entry entry = (Map.Entry)item$iv;
            int index = n4;
            boolean bl = false;
            String userKey = (String)entry.getKey();
            SpaceLevelUserActivity userActivity = (SpaceLevelUserActivity)entry.getValue();
            ZoneId zoneId = datePeriodOptions.getTimezone();
            Object[] objectArray = new Object[6];
            Object object2 = (User)detailsByUser.get(userKey);
            if (object2 == null || (object2 = ((User)object2).getDisplayName()) == null) {
                object2 = this.userService.getUnknownUserName();
            }
            objectArray[0] = object2;
            User user = (User)detailsByUser.get(userKey);
            objectArray[1] = user != null ? user.getEmail() : null;
            objectArray[2] = userActivity.getCreatedCount();
            objectArray[3] = userActivity.getUpdatedCount();
            objectArray[4] = userActivity.getCommentsCount();
            objectArray[5] = userActivity.getViewedCount();
            this.buildDataRow(sheet, index, zoneId, cellStyles, CollectionsKt.listOf((Object[])objectArray));
        }
        return new ExcelReportSXSSFWorkbookImpl(workbook);
    }

    /*
     * WARNING - void declaration
     */
    private final long calculateEventTotal(userDetailsActivity userActivity, AnalyticsEvent ... events2) {
        void $this$fold$iv;
        AnalyticsEvent[] analyticsEventArray = events2;
        long initial$iv = 0L;
        boolean $i$f$fold = false;
        long accumulator$iv = initial$iv;
        int n = ((void)$this$fold$iv).length;
        for (int i = 0; i < n; ++i) {
            void event;
            void element$iv;
            void var12_10 = element$iv = $this$fold$iv[i];
            long total = accumulator$iv;
            boolean bl = false;
            Long l = userActivity.getEventTotals().get(event);
            accumulator$iv = total + (l != null ? l : 0L);
        }
        return accumulator$iv;
    }

    private static final User buildExcelReport$lambda$1(Lazy<User> $anonUser$delegate) {
        Lazy<User> lazy = $anonUser$delegate;
        return (User)lazy.getValue();
    }

    public static final /* synthetic */ SpacePaginatedAnalyticsService access$getPaginatedService$p(SpaceActivityByUserExcelServiceImpl $this) {
        return $this.paginatedService;
    }

    public static final /* synthetic */ UserService access$getUserService$p(SpaceActivityByUserExcelServiceImpl $this) {
        return $this.userService;
    }
}

