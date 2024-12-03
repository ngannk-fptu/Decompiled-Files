/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.apache.poi.ss.usermodel.CellStyle
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service.excel.poi;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.apache.poi.ss.usermodel.CellStyle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0005J\t\u0010\t\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\n\u001a\u00020\u0003H\u00c6\u0003J\u001d\u0010\u000b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0007\u00a8\u0006\u0013"}, d2={"Lcom/addonengine/addons/analytics/service/excel/poi/ExcelCellStyles;", "", "header", "Lorg/apache/poi/ss/usermodel/CellStyle;", "date", "(Lorg/apache/poi/ss/usermodel/CellStyle;Lorg/apache/poi/ss/usermodel/CellStyle;)V", "getDate", "()Lorg/apache/poi/ss/usermodel/CellStyle;", "getHeader", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class ExcelCellStyles {
    @NotNull
    private final CellStyle header;
    @NotNull
    private final CellStyle date;

    public ExcelCellStyles(@NotNull CellStyle header, @NotNull CellStyle date) {
        Intrinsics.checkNotNullParameter((Object)header, (String)"header");
        Intrinsics.checkNotNullParameter((Object)date, (String)"date");
        this.header = header;
        this.date = date;
    }

    @NotNull
    public final CellStyle getHeader() {
        return this.header;
    }

    @NotNull
    public final CellStyle getDate() {
        return this.date;
    }

    @NotNull
    public final CellStyle component1() {
        return this.header;
    }

    @NotNull
    public final CellStyle component2() {
        return this.date;
    }

    @NotNull
    public final ExcelCellStyles copy(@NotNull CellStyle header, @NotNull CellStyle date) {
        Intrinsics.checkNotNullParameter((Object)header, (String)"header");
        Intrinsics.checkNotNullParameter((Object)date, (String)"date");
        return new ExcelCellStyles(header, date);
    }

    public static /* synthetic */ ExcelCellStyles copy$default(ExcelCellStyles excelCellStyles, CellStyle cellStyle, CellStyle cellStyle2, int n, Object object) {
        if ((n & 1) != 0) {
            cellStyle = excelCellStyles.header;
        }
        if ((n & 2) != 0) {
            cellStyle2 = excelCellStyles.date;
        }
        return excelCellStyles.copy(cellStyle, cellStyle2);
    }

    @NotNull
    public String toString() {
        return "ExcelCellStyles(header=" + this.header + ", date=" + this.date + ')';
    }

    public int hashCode() {
        int result = this.header.hashCode();
        result = result * 31 + this.date.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ExcelCellStyles)) {
            return false;
        }
        ExcelCellStyles excelCellStyles = (ExcelCellStyles)other;
        if (!Intrinsics.areEqual((Object)this.header, (Object)excelCellStyles.header)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.date, (Object)excelCellStyles.date);
    }
}

