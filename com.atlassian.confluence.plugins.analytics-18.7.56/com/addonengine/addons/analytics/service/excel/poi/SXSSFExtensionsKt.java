/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.apache.poi.xssf.streaming.SXSSFCell
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service.excel.poi;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=2, xi=48, d1={"\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\u0010\t\n\u0000\u001a\u0012\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004\u001a\u0012\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0005\u00a8\u0006\u0006"}, d2={"setCellValue", "", "Lorg/apache/poi/xssf/streaming/SXSSFCell;", "value", "", "", "analytics"})
public final class SXSSFExtensionsKt {
    public static final void setCellValue(@NotNull SXSSFCell $this$setCellValue, long value) {
        Intrinsics.checkNotNullParameter((Object)$this$setCellValue, (String)"<this>");
        $this$setCellValue.setCellValue((double)value);
    }

    public static final void setCellValue(@NotNull SXSSFCell $this$setCellValue, int value) {
        Intrinsics.checkNotNullParameter((Object)$this$setCellValue, (String)"<this>");
        $this$setCellValue.setCellValue((double)value);
    }
}

