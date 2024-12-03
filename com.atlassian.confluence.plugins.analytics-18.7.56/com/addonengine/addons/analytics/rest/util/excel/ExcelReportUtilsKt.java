/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  kotlin.Metadata
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.rest.util.excel;

import com.addonengine.addons.analytics.rest.util.ResponseBuilderExtensionsKt;
import com.addonengine.addons.analytics.rest.util.excel.ExcelGenerationException;
import com.addonengine.addons.analytics.rest.util.excel.ExcelReportStreamingOutput;
import com.addonengine.addons.analytics.service.excel.model.ExcelReport;
import javax.ws.rs.core.Response;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=2, xi=48, d1={"\u0000\u001a\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u001c\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2={"EXCEL_REPORT_MIME_TYPE", "", "buildExcelReportAndResponse", "Ljavax/ws/rs/core/Response;", "fileName", "buildExcelReport", "Lkotlin/Function0;", "Lcom/addonengine/addons/analytics/service/excel/model/ExcelReport;", "analytics"})
public final class ExcelReportUtilsKt {
    @NotNull
    public static final String EXCEL_REPORT_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @NotNull
    public static final Response buildExcelReportAndResponse(@NotNull String fileName, @NotNull Function0<? extends ExcelReport> buildExcelReport2) {
        Response response;
        Intrinsics.checkNotNullParameter((Object)fileName, (String)"fileName");
        Intrinsics.checkNotNullParameter(buildExcelReport2, (String)"buildExcelReport");
        try {
            ExcelReport excelReport = (ExcelReport)buildExcelReport2.invoke();
            ExcelReportStreamingOutput stream = new ExcelReportStreamingOutput(excelReport);
            Response.ResponseBuilder responseBuilder = Response.ok((Object)stream);
            Intrinsics.checkNotNullExpressionValue((Object)responseBuilder, (String)"ok(...)");
            response = ResponseBuilderExtensionsKt.contentDispositionForAttachment(responseBuilder, fileName).build();
            Intrinsics.checkNotNull((Object)response);
        }
        catch (Exception e) {
            throw new ExcelGenerationException(e);
        }
        return response;
    }
}

