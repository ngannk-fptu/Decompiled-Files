/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.apache.poi.common.usermodel.HyperlinkType
 *  org.apache.poi.ss.usermodel.CellStyle
 *  org.apache.poi.ss.usermodel.Font
 *  org.apache.poi.ss.usermodel.Hyperlink
 *  org.apache.poi.ss.usermodel.PrintSetup
 *  org.apache.poi.ss.util.CellRangeAddress
 *  org.apache.poi.xssf.streaming.SXSSFCell
 *  org.apache.poi.xssf.streaming.SXSSFRow
 *  org.apache.poi.xssf.streaming.SXSSFSheet
 *  org.apache.poi.xssf.streaming.SXSSFWorkbook
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service.excel.poi;

import com.addonengine.addons.analytics.service.excel.poi.ExcelCellStyles;
import com.addonengine.addons.analytics.service.excel.poi.ExcelColumnHeader;
import com.addonengine.addons.analytics.service.excel.poi.SXSSFExtensionsKt;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jetbrains.annotations.NotNull;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000^\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\b'\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J(\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0002J&\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00170\u0016H\u0004J8\u0010\u0018\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u0013\u001a\u00020\u00142\u000e\u0010\u001b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u0016H\u0004J\u0010\u0010\u001c\u001a\u00020\u00142\u0006\u0010\u001d\u001a\u00020\u001eH\u0004J\b\u0010\u001f\u001a\u00020\u001eH\u0004J\u0018\u0010 \u001a\u00020\u00122\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010!\u001a\u00020\u000eH\u0004R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\""}, d2={"Lcom/addonengine/addons/analytics/service/excel/poi/ExcelSXSSFService;", "", "i18n", "Lcom/atlassian/sal/api/message/I18nResolver;", "(Lcom/atlassian/sal/api/message/I18nResolver;)V", "buildColumnHeaderCell", "Lorg/apache/poi/xssf/streaming/SXSSFCell;", "row", "Lorg/apache/poi/xssf/streaming/SXSSFRow;", "index", "", "style", "Lorg/apache/poi/ss/usermodel/CellStyle;", "name", "", "buildColumnHeaderRow", "", "sheet", "Lorg/apache/poi/xssf/streaming/SXSSFSheet;", "cellStyles", "Lcom/addonengine/addons/analytics/service/excel/poi/ExcelCellStyles;", "columnHeaders", "", "Lcom/addonengine/addons/analytics/service/excel/poi/ExcelColumnHeader;", "buildDataRow", "timezone", "Ljava/time/ZoneId;", "values", "buildDefaultWorkbookStyles", "workbook", "Lorg/apache/poi/xssf/streaming/SXSSFWorkbook;", "createWorkbook", "createWorksheet", "sheetNameI18nKey", "analytics"})
@SourceDebugExtension(value={"SMAP\nExcelSXSSFService.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ExcelSXSSFService.kt\ncom/addonengine/addons/analytics/service/excel/poi/ExcelSXSSFService\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,124:1\n1864#2,3:125\n1864#2,3:128\n*S KotlinDebug\n*F\n+ 1 ExcelSXSSFService.kt\ncom/addonengine/addons/analytics/service/excel/poi/ExcelSXSSFService\n*L\n52#1:125,3\n71#1:128,3\n*E\n"})
public abstract class ExcelSXSSFService {
    @NotNull
    private final I18nResolver i18n;

    public ExcelSXSSFService(@NotNull I18nResolver i18n) {
        Intrinsics.checkNotNullParameter((Object)i18n, (String)"i18n");
        this.i18n = i18n;
    }

    @NotNull
    protected final SXSSFWorkbook createWorkbook() {
        SXSSFWorkbook sXSSFWorkbook;
        SXSSFWorkbook it = sXSSFWorkbook = new SXSSFWorkbook();
        boolean bl = false;
        it.getXSSFWorkbook().getProperties().getCoreProperties().setCreator("Analytics for Confluence");
        return sXSSFWorkbook;
    }

    @NotNull
    protected final SXSSFSheet createWorksheet(@NotNull SXSSFWorkbook workbook, @NotNull String sheetNameI18nKey) {
        PrintSetup printSetup;
        SXSSFSheet sXSSFSheet;
        Intrinsics.checkNotNullParameter((Object)workbook, (String)"workbook");
        Intrinsics.checkNotNullParameter((Object)sheetNameI18nKey, (String)"sheetNameI18nKey");
        SXSSFSheet it = sXSSFSheet = workbook.createSheet(this.i18n.getText(sheetNameI18nKey));
        boolean bl = false;
        PrintSetup it2 = printSetup = it.getPrintSetup();
        boolean bl2 = false;
        it2.setPaperSize((short)9);
        it2.setLandscape(true);
        SXSSFSheet sXSSFSheet2 = sXSSFSheet;
        Intrinsics.checkNotNullExpressionValue((Object)sXSSFSheet2, (String)"also(...)");
        return sXSSFSheet2;
    }

    /*
     * WARNING - void declaration
     */
    protected final void buildColumnHeaderRow(@NotNull SXSSFSheet sheet, @NotNull ExcelCellStyles cellStyles, @NotNull List<ExcelColumnHeader> columnHeaders) {
        SXSSFRow sXSSFRow;
        Intrinsics.checkNotNullParameter((Object)sheet, (String)"sheet");
        Intrinsics.checkNotNullParameter((Object)cellStyles, (String)"cellStyles");
        Intrinsics.checkNotNullParameter(columnHeaders, (String)"columnHeaders");
        SXSSFRow row = sXSSFRow = sheet.createRow(0);
        boolean bl = false;
        Iterable $this$forEachIndexed$iv = columnHeaders;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            void columnHeader;
            int n;
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            ExcelColumnHeader excelColumnHeader = (ExcelColumnHeader)item$iv;
            int index = n;
            boolean bl2 = false;
            Intrinsics.checkNotNull((Object)row);
            CellStyle cellStyle = cellStyles.getHeader();
            String string = this.i18n.getText(columnHeader.getI18nKey());
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getText(...)");
            this.buildColumnHeaderCell(row, index, cellStyle, string);
            sheet.setColumnWidth(index, columnHeader.getSize() * 256);
            sheet.setColumnHidden(index, columnHeader.getHidden());
        }
        sheet.createFreezePane(0, 1);
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, columnHeaders.size() - 1));
    }

    /*
     * WARNING - void declaration
     */
    protected final void buildDataRow(@NotNull SXSSFSheet sheet, int index, @NotNull ZoneId timezone, @NotNull ExcelCellStyles cellStyles, @NotNull List<? extends Object> values) {
        Intrinsics.checkNotNullParameter((Object)sheet, (String)"sheet");
        Intrinsics.checkNotNullParameter((Object)timezone, (String)"timezone");
        Intrinsics.checkNotNullParameter((Object)cellStyles, (String)"cellStyles");
        Intrinsics.checkNotNullParameter(values, (String)"values");
        SXSSFRow row = sheet.createRow(index + 1);
        Iterable $this$forEachIndexed$iv = values;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            void value;
            SXSSFCell sXSSFCell;
            int n;
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            Object t = item$iv;
            int index2 = n;
            boolean bl = false;
            SXSSFCell it = sXSSFCell = row.createCell(index2);
            boolean bl2 = false;
            void var19_19 = value;
            if (var19_19 instanceof String) {
                it.setCellValue((String)value);
                continue;
            }
            if (var19_19 instanceof Integer) {
                Intrinsics.checkNotNull((Object)it);
                SXSSFExtensionsKt.setCellValue(it, ((Number)value).intValue());
                continue;
            }
            if (var19_19 instanceof Long) {
                Intrinsics.checkNotNull((Object)it);
                SXSSFExtensionsKt.setCellValue(it, ((Number)value).longValue());
                continue;
            }
            if (var19_19 instanceof Double) {
                it.setCellValue(((Number)value).doubleValue());
                continue;
            }
            if (var19_19 instanceof URL) {
                void it2;
                Hyperlink hyperlink;
                String url = ((URL)value).toString();
                it.setCellValue(url);
                Hyperlink hyperlink2 = hyperlink = sheet.getWorkbook().getCreationHelper().createHyperlink(HyperlinkType.URL);
                SXSSFCell sXSSFCell2 = it;
                boolean bl3 = false;
                it2.setAddress(url);
                sXSSFCell2.setHyperlink(hyperlink);
                continue;
            }
            if (var19_19 instanceof Instant) {
                LocalDateTime localDate = LocalDateTime.ofInstant((Instant)value, timezone);
                it.setCellValue(localDate);
                it.setCellStyle(cellStyles.getDate());
                continue;
            }
            if (var19_19 instanceof Serializable) {
                it.setCellValue(value.toString());
                continue;
            }
            if (var19_19 == null) {
                it.setCellValue(null);
                continue;
            }
            throw new IllegalArgumentException("The type isn't supported.");
        }
    }

    @NotNull
    protected final ExcelCellStyles buildDefaultWorkbookStyles(@NotNull SXSSFWorkbook workbook) {
        Font font;
        CellStyle cellStyle;
        Intrinsics.checkNotNullParameter((Object)workbook, (String)"workbook");
        CellStyle it = cellStyle = workbook.createCellStyle();
        boolean bl = false;
        Font it2 = font = workbook.createFont();
        boolean bl2 = false;
        it2.setBold(true);
        Font font2 = font;
        it.setFont(font2);
        CellStyle cellStyle2 = cellStyle;
        Intrinsics.checkNotNullExpressionValue((Object)cellStyle2, (String)"also(...)");
        it = cellStyle = workbook.createCellStyle();
        CellStyle cellStyle3 = cellStyle2;
        boolean bl3 = false;
        it.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("yyyy/mm/dd hh:mm"));
        CellStyle cellStyle4 = cellStyle;
        Intrinsics.checkNotNullExpressionValue((Object)cellStyle4, (String)"also(...)");
        CellStyle cellStyle5 = cellStyle4;
        CellStyle cellStyle6 = cellStyle3;
        return new ExcelCellStyles(cellStyle6, cellStyle5);
    }

    private final SXSSFCell buildColumnHeaderCell(SXSSFRow row, int index, CellStyle style, String name) {
        SXSSFCell sXSSFCell;
        SXSSFCell it = sXSSFCell = row.createCell(index);
        boolean bl = false;
        it.setCellValue(name);
        it.setCellStyle(style);
        SXSSFCell sXSSFCell2 = sXSSFCell;
        Intrinsics.checkNotNullExpressionValue((Object)sXSSFCell2, (String)"also(...)");
        return sXSSFCell2;
    }
}

