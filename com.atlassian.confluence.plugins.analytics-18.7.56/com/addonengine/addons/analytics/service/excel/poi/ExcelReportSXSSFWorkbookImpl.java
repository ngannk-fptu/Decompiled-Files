/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.apache.poi.xssf.streaming.SXSSFWorkbook
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service.excel.poi;

import com.addonengine.addons.analytics.service.excel.model.ExcelReport;
import java.io.OutputStream;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u0005\u001a\u00020\u0006H\u0016J\u0010\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\tH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2={"Lcom/addonengine/addons/analytics/service/excel/poi/ExcelReportSXSSFWorkbookImpl;", "Lcom/addonengine/addons/analytics/service/excel/model/ExcelReport;", "workbook", "Lorg/apache/poi/xssf/streaming/SXSSFWorkbook;", "(Lorg/apache/poi/xssf/streaming/SXSSFWorkbook;)V", "dispose", "", "writeToStream", "output", "Ljava/io/OutputStream;", "analytics"})
public final class ExcelReportSXSSFWorkbookImpl
implements ExcelReport {
    @NotNull
    private final SXSSFWorkbook workbook;

    public ExcelReportSXSSFWorkbookImpl(@NotNull SXSSFWorkbook workbook) {
        Intrinsics.checkNotNullParameter((Object)workbook, (String)"workbook");
        this.workbook = workbook;
    }

    @Override
    public void writeToStream(@NotNull OutputStream output) {
        Intrinsics.checkNotNullParameter((Object)output, (String)"output");
        this.workbook.write(output);
    }

    @Override
    public void dispose() {
        this.workbook.dispose();
    }
}

