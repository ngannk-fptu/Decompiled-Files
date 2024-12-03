/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service.excel.poi;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0010\b\u0086\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0007H\u00c6\u0003J'\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u0013\u001a\u00020\u00072\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0015\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u0016\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u0017"}, d2={"Lcom/addonengine/addons/analytics/service/excel/poi/ExcelColumnHeader;", "", "i18nKey", "", "size", "", "hidden", "", "(Ljava/lang/String;IZ)V", "getHidden", "()Z", "getI18nKey", "()Ljava/lang/String;", "getSize", "()I", "component1", "component2", "component3", "copy", "equals", "other", "hashCode", "toString", "analytics"})
public final class ExcelColumnHeader {
    @NotNull
    private final String i18nKey;
    private final int size;
    private final boolean hidden;

    public ExcelColumnHeader(@NotNull String i18nKey, int size, boolean hidden) {
        Intrinsics.checkNotNullParameter((Object)i18nKey, (String)"i18nKey");
        this.i18nKey = i18nKey;
        this.size = size;
        this.hidden = hidden;
    }

    public /* synthetic */ ExcelColumnHeader(String string, int n, boolean bl, int n2, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n2 & 4) != 0) {
            bl = false;
        }
        this(string, n, bl);
    }

    @NotNull
    public final String getI18nKey() {
        return this.i18nKey;
    }

    public final int getSize() {
        return this.size;
    }

    public final boolean getHidden() {
        return this.hidden;
    }

    @NotNull
    public final String component1() {
        return this.i18nKey;
    }

    public final int component2() {
        return this.size;
    }

    public final boolean component3() {
        return this.hidden;
    }

    @NotNull
    public final ExcelColumnHeader copy(@NotNull String i18nKey, int size, boolean hidden) {
        Intrinsics.checkNotNullParameter((Object)i18nKey, (String)"i18nKey");
        return new ExcelColumnHeader(i18nKey, size, hidden);
    }

    public static /* synthetic */ ExcelColumnHeader copy$default(ExcelColumnHeader excelColumnHeader, String string, int n, boolean bl, int n2, Object object) {
        if ((n2 & 1) != 0) {
            string = excelColumnHeader.i18nKey;
        }
        if ((n2 & 2) != 0) {
            n = excelColumnHeader.size;
        }
        if ((n2 & 4) != 0) {
            bl = excelColumnHeader.hidden;
        }
        return excelColumnHeader.copy(string, n, bl);
    }

    @NotNull
    public String toString() {
        return "ExcelColumnHeader(i18nKey=" + this.i18nKey + ", size=" + this.size + ", hidden=" + this.hidden + ')';
    }

    public int hashCode() {
        int result = this.i18nKey.hashCode();
        result = result * 31 + Integer.hashCode(this.size);
        int n = this.hidden ? 1 : 0;
        if (n != 0) {
            n = 1;
        }
        result = result * 31 + n;
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ExcelColumnHeader)) {
            return false;
        }
        ExcelColumnHeader excelColumnHeader = (ExcelColumnHeader)other;
        if (!Intrinsics.areEqual((Object)this.i18nKey, (Object)excelColumnHeader.i18nKey)) {
            return false;
        }
        if (this.size != excelColumnHeader.size) {
            return false;
        }
        return this.hidden == excelColumnHeader.hidden;
    }
}

