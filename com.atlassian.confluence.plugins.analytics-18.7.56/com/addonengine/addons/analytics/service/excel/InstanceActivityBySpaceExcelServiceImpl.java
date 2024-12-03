/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.SetsKt
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.sequences.Sequence
 *  org.apache.poi.xssf.streaming.SXSSFSheet
 *  org.apache.poi.xssf.streaming.SXSSFWorkbook
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.addonengine.addons.analytics.service.excel;

import com.addonengine.addons.analytics.service.InstancePaginatedAnalyticsService;
import com.addonengine.addons.analytics.service.excel.InstanceActivityBySpaceExcelService;
import com.addonengine.addons.analytics.service.excel.InstanceActivityBySpaceExcelServiceImpl;
import com.addonengine.addons.analytics.service.excel.model.ExcelReport;
import com.addonengine.addons.analytics.service.excel.poi.ExcelCellStyles;
import com.addonengine.addons.analytics.service.excel.poi.ExcelColumnHeader;
import com.addonengine.addons.analytics.service.excel.poi.ExcelReportSXSSFWorkbookImpl;
import com.addonengine.addons.analytics.service.excel.poi.ExcelSXSSFService;
import com.addonengine.addons.analytics.service.model.ContentType;
import com.addonengine.addons.analytics.service.model.CountType;
import com.addonengine.addons.analytics.service.model.DatePeriodOptions;
import com.addonengine.addons.analytics.service.model.LazyFetching;
import com.addonengine.addons.analytics.service.model.SortOrder;
import com.addonengine.addons.analytics.service.model.SpaceActivity;
import com.addonengine.addons.analytics.service.model.SpaceSortField;
import com.addonengine.addons.analytics.service.model.SpaceType;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.sequences.Sequence;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u00012\u00020\u0002B\u0019\b\u0007\u0012\b\b\u0001\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007J,\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u00142\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\u0014H\u0016R\u0018\u0010\b\u001a\n \n*\u0004\u0018\u00010\t0\tX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u000bR\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2={"Lcom/addonengine/addons/analytics/service/excel/InstanceActivityBySpaceExcelServiceImpl;", "Lcom/addonengine/addons/analytics/service/excel/poi/ExcelSXSSFService;", "Lcom/addonengine/addons/analytics/service/excel/InstanceActivityBySpaceExcelService;", "i18n", "Lcom/atlassian/sal/api/message/I18nResolver;", "paginatedService", "Lcom/addonengine/addons/analytics/service/InstancePaginatedAnalyticsService;", "(Lcom/atlassian/sal/api/message/I18nResolver;Lcom/addonengine/addons/analytics/service/InstancePaginatedAnalyticsService;)V", "batchSize", "", "kotlin.jvm.PlatformType", "Ljava/lang/Integer;", "columnHeaders", "", "Lcom/addonengine/addons/analytics/service/excel/poi/ExcelColumnHeader;", "buildExcelReport", "Lcom/addonengine/addons/analytics/service/excel/model/ExcelReport;", "datePeriodOptions", "Lcom/addonengine/addons/analytics/service/model/DatePeriodOptions;", "spaceTypes", "", "Lcom/addonengine/addons/analytics/service/model/SpaceType;", "contentTypes", "Lcom/addonengine/addons/analytics/service/model/ContentType;", "analytics"})
@SourceDebugExtension(value={"SMAP\nInstanceActivityBySpaceExcelServiceImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InstanceActivityBySpaceExcelServiceImpl.kt\ncom/addonengine/addons/analytics/service/excel/InstanceActivityBySpaceExcelServiceImpl\n+ 2 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n*L\n1#1,113:1\n1324#2,3:114\n*S KotlinDebug\n*F\n+ 1 InstanceActivityBySpaceExcelServiceImpl.kt\ncom/addonengine/addons/analytics/service/excel/InstanceActivityBySpaceExcelServiceImpl\n*L\n98#1:114,3\n*E\n"})
public final class InstanceActivityBySpaceExcelServiceImpl
extends ExcelSXSSFService
implements InstanceActivityBySpaceExcelService {
    @NotNull
    private final I18nResolver i18n;
    @NotNull
    private final InstancePaginatedAnalyticsService paginatedService;
    private final Integer batchSize;
    @NotNull
    private final List<ExcelColumnHeader> columnHeaders;

    @Autowired
    public InstanceActivityBySpaceExcelServiceImpl(@ComponentImport @NotNull I18nResolver i18n, @NotNull InstancePaginatedAnalyticsService paginatedService) {
        Intrinsics.checkNotNullParameter((Object)i18n, (String)"i18n");
        Intrinsics.checkNotNullParameter((Object)paginatedService, (String)"paginatedService");
        super(i18n);
        this.i18n = i18n;
        this.paginatedService = paginatedService;
        this.batchSize = Integer.getInteger("confluence.analytics.pagination.xls.batch.size", 25000);
        Object[] objectArray = new ExcelColumnHeader[]{new ExcelColumnHeader("com.addonengine.addons.analytics.excel.instance.space.header.name", 60, false, 4, null), new ExcelColumnHeader("com.addonengine.addons.analytics.excel.instance.space.header.link", 60, true), new ExcelColumnHeader("com.addonengine.addons.analytics.excel.instance.space.header.categories", 20, false, 4, null), new ExcelColumnHeader("com.addonengine.addons.analytics.excel.instance.space.header.created", 20, false, 4, null), new ExcelColumnHeader("com.addonengine.addons.analytics.excel.instance.space.header.updated", 20, false, 4, null), new ExcelColumnHeader("com.addonengine.addons.analytics.excel.instance.space.header.lastViewed", 20, false, 4, null), new ExcelColumnHeader("com.addonengine.addons.analytics.excel.instance.space.header.usersViewed", 15, false, 4, null), new ExcelColumnHeader("com.addonengine.addons.analytics.excel.instance.space.header.views", 10, false, 4, null)};
        this.columnHeaders = CollectionsKt.listOf((Object[])objectArray);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public ExcelReport buildExcelReport(@NotNull DatePeriodOptions datePeriodOptions, @NotNull Set<? extends SpaceType> spaceTypes, @NotNull Set<? extends ContentType> contentTypes) {
        Intrinsics.checkNotNullParameter((Object)datePeriodOptions, (String)"datePeriodOptions");
        Intrinsics.checkNotNullParameter(spaceTypes, (String)"spaceTypes");
        Intrinsics.checkNotNullParameter(contentTypes, (String)"contentTypes");
        Integer n = this.batchSize;
        Intrinsics.checkNotNullExpressionValue((Object)n, (String)"batchSize");
        int n2 = ((Number)n).intValue();
        Integer n3 = this.batchSize;
        Intrinsics.checkNotNullExpressionValue((Object)n3, (String)"batchSize");
        Sequence activityBySpace2 = LazyFetching.Companion.seek(n2, ((Number)n3).intValue(), (Function2)new Function2<String, Integer, List<? extends SpaceActivity>>(this, datePeriodOptions, spaceTypes, contentTypes){
            final /* synthetic */ InstanceActivityBySpaceExcelServiceImpl this$0;
            final /* synthetic */ DatePeriodOptions $datePeriodOptions;
            final /* synthetic */ Set<SpaceType> $spaceTypes;
            final /* synthetic */ Set<ContentType> $contentTypes;
            {
                this.this$0 = $receiver;
                this.$datePeriodOptions = $datePeriodOptions;
                this.$spaceTypes = $spaceTypes;
                this.$contentTypes = $contentTypes;
                super(2);
            }

            @NotNull
            public final List<SpaceActivity> invoke(@Nullable String offset, int limit) {
                CountType[] countTypeArray = new CountType[]{CountType.TOTAL, CountType.UNIQUE};
                return InstanceActivityBySpaceExcelServiceImpl.access$getPaginatedService$p(this.this$0).getActivityBySpace(this.$datePeriodOptions, this.$spaceTypes, this.$contentTypes, offset, limit, SpaceSortField.SPACE_NAME, SortOrder.ASC, SetsKt.setOf((Object[])countTypeArray), true);
            }
        }, buildExcelReport.activityBySpace.2.INSTANCE).asSequence();
        SXSSFWorkbook workbook = this.createWorkbook();
        ExcelCellStyles cellStyles = this.buildDefaultWorkbookStyles(workbook);
        SXSSFSheet sheet = this.createWorksheet(workbook, "com.addonengine.addons.analytics.excel.instance.space.sheetName");
        this.buildColumnHeaderRow(sheet, cellStyles, this.columnHeaders);
        Sequence $this$forEachIndexed$iv = activityBySpace2;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            void spaceActivity;
            int n4;
            if ((n4 = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            SpaceActivity spaceActivity2 = (SpaceActivity)item$iv;
            int index = n4;
            boolean bl = false;
            Object[] objectArray = new Serializable[]{spaceActivity.getSpace().getName(), spaceActivity.getSpace().getLink(), CollectionsKt.joinToString$default((Iterable)spaceActivity.getSpace().getCategories(), (CharSequence)", ", null, null, (int)0, null, null, (int)62, null), Long.valueOf(spaceActivity.getCreated()), Long.valueOf(spaceActivity.getUpdated()), spaceActivity.getLastViewedAt(), spaceActivity.getUsersViewed(), spaceActivity.getViews()};
            this.buildDataRow(sheet, index, datePeriodOptions.getTimezone(), cellStyles, CollectionsKt.listOf((Object[])objectArray));
        }
        return new ExcelReportSXSSFWorkbookImpl(workbook);
    }

    public static final /* synthetic */ InstancePaginatedAnalyticsService access$getPaginatedService$p(InstanceActivityBySpaceExcelServiceImpl $this) {
        return $this.paginatedService;
    }
}

