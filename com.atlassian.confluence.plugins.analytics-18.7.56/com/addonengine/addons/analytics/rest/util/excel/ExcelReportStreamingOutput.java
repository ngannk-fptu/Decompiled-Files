/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.StreamingOutput
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.addonengine.addons.analytics.rest.util.excel;

import com.addonengine.addons.analytics.service.excel.model.ExcelReport;
import java.io.OutputStream;
import javax.ws.rs.core.StreamingOutput;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2={"Lcom/addonengine/addons/analytics/rest/util/excel/ExcelReportStreamingOutput;", "Ljavax/ws/rs/core/StreamingOutput;", "excelReport", "Lcom/addonengine/addons/analytics/service/excel/model/ExcelReport;", "(Lcom/addonengine/addons/analytics/service/excel/model/ExcelReport;)V", "log", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "write", "", "output", "Ljava/io/OutputStream;", "analytics"})
public final class ExcelReportStreamingOutput
implements StreamingOutput {
    @NotNull
    private final ExcelReport excelReport;
    private final Logger log;

    public ExcelReportStreamingOutput(@NotNull ExcelReport excelReport) {
        Intrinsics.checkNotNullParameter((Object)excelReport, (String)"excelReport");
        this.excelReport = excelReport;
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    public void write(@NotNull OutputStream output) {
        Intrinsics.checkNotNullParameter((Object)output, (String)"output");
        try {
            this.excelReport.writeToStream(output);
            output.close();
            this.excelReport.dispose();
        }
        catch (Exception e) {
            this.log.error(e.getMessage());
            output.close();
            this.excelReport.dispose();
            throw e;
        }
    }
}

