/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.check.CsvFileContent
 *  com.atlassian.migration.app.dto.check.CsvFileContent
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.CopyCsvContentResult;
import com.atlassian.migration.app.VendorCheckExecutor;
import com.atlassian.migration.app.dto.check.CsvFileContent;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Metadata(mv={1, 7, 1}, k=2, xi=48, d1={"\u0000*\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\u001a\u001a\u0010\u0002\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0002\u001a\u0018\u0010\b\u001a\u00020\t2\u000e\u0010\n\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\f0\u000bH\u0002\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2={"log", "Lorg/slf4j/Logger;", "copyCsvContentWithLimitsApplied", "Lcom/atlassian/migration/app/CopyCsvContentResult;", "csvFileContent", "Lcom/atlassian/migration/app/check/CsvFileContent;", "maxCSVFileSize", "", "getCharCount", "", "row", "", "", "app-migration-assistant"})
public final class VendorCheckExecutorKt {
    @NotNull
    private static final Logger log;

    private static final CopyCsvContentResult copyCsvContentWithLimitsApplied(com.atlassian.migration.app.check.CsvFileContent csvFileContent, long maxCSVFileSize) {
        if (csvFileContent == null) {
            return new CopyCsvContentResult(null, false);
        }
        List headers = csvFileContent.getColumnHeaders();
        List rows = csvFileContent.getRows();
        int csvCharCount = 0;
        List rowsToAdd = new ArrayList();
        Intrinsics.checkNotNullExpressionValue((Object)headers, (String)"headers");
        if ((long)(csvCharCount += VendorCheckExecutorKt.getCharCount(headers)) > maxCSVFileSize) {
            log.warn("Skipping csv because headers exceeds csv content limit {}", (Object)maxCSVFileSize);
            return new CopyCsvContentResult(null, true);
        }
        boolean truncated = false;
        for (List row : rows) {
            Intrinsics.checkNotNullExpressionValue((Object)row, (String)"row");
            if ((long)(csvCharCount += VendorCheckExecutorKt.getCharCount(row)) > maxCSVFileSize) {
                log.warn("Truncating csv because csv content limit of {} characters exceeded", (Object)maxCSVFileSize);
                truncated = true;
                break;
            }
            rowsToAdd.add(row);
        }
        return new CopyCsvContentResult(new CsvFileContent(headers, rowsToAdd), truncated);
    }

    /*
     * WARNING - void declaration
     */
    private static final int getCharCount(List<String> row) {
        Iterable iterable = row;
        int n = 0;
        for (Object t : iterable) {
            void it;
            String string = (String)t;
            int n2 = n;
            boolean bl = false;
            void v0 = it;
            int n3 = v0 != null ? v0.length() : 0;
            n = n2 + n3;
        }
        return n;
    }

    public static final /* synthetic */ Logger access$getLog$p() {
        return log;
    }

    public static final /* synthetic */ CopyCsvContentResult access$copyCsvContentWithLimitsApplied(com.atlassian.migration.app.check.CsvFileContent csvFileContent, long maxCSVFileSize) {
        return VendorCheckExecutorKt.copyCsvContentWithLimitsApplied(csvFileContent, maxCSVFileSize);
    }

    static {
        Logger logger = LoggerFactory.getLogger(VendorCheckExecutor.class);
        Intrinsics.checkNotNull((Object)logger);
        log = logger;
    }
}

