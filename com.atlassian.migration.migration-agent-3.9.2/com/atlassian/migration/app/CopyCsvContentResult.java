/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.dto.check.CsvFileContent
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.dto.check.CsvFileContent;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B\u0017\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000b\u0010\u000b\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001f\u0010\r\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u00052\b\u0010\u000f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0010\u001a\u00020\u0011H\u00d6\u0001J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0014"}, d2={"Lcom/atlassian/migration/app/CopyCsvContentResult;", "", "csvFileContent", "Lcom/atlassian/migration/app/dto/check/CsvFileContent;", "truncated", "", "(Lcom/atlassian/migration/app/dto/check/CsvFileContent;Z)V", "getCsvFileContent", "()Lcom/atlassian/migration/app/dto/check/CsvFileContent;", "getTruncated", "()Z", "component1", "component2", "copy", "equals", "other", "hashCode", "", "toString", "", "app-migration-assistant"})
final class CopyCsvContentResult {
    @Nullable
    private final CsvFileContent csvFileContent;
    private final boolean truncated;

    public CopyCsvContentResult(@Nullable CsvFileContent csvFileContent, boolean truncated) {
        this.csvFileContent = csvFileContent;
        this.truncated = truncated;
    }

    @Nullable
    public final CsvFileContent getCsvFileContent() {
        return this.csvFileContent;
    }

    public final boolean getTruncated() {
        return this.truncated;
    }

    @Nullable
    public final CsvFileContent component1() {
        return this.csvFileContent;
    }

    public final boolean component2() {
        return this.truncated;
    }

    @NotNull
    public final CopyCsvContentResult copy(@Nullable CsvFileContent csvFileContent, boolean truncated) {
        return new CopyCsvContentResult(csvFileContent, truncated);
    }

    public static /* synthetic */ CopyCsvContentResult copy$default(CopyCsvContentResult copyCsvContentResult, CsvFileContent csvFileContent, boolean bl, int n, Object object) {
        if ((n & 1) != 0) {
            csvFileContent = copyCsvContentResult.csvFileContent;
        }
        if ((n & 2) != 0) {
            bl = copyCsvContentResult.truncated;
        }
        return copyCsvContentResult.copy(csvFileContent, bl);
    }

    @NotNull
    public String toString() {
        return "CopyCsvContentResult(csvFileContent=" + this.csvFileContent + ", truncated=" + this.truncated + ')';
    }

    public int hashCode() {
        int result = this.csvFileContent == null ? 0 : this.csvFileContent.hashCode();
        int n = this.truncated ? 1 : 0;
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
        if (!(other instanceof CopyCsvContentResult)) {
            return false;
        }
        CopyCsvContentResult copyCsvContentResult = (CopyCsvContentResult)other;
        if (!Intrinsics.areEqual((Object)this.csvFileContent, (Object)copyCsvContentResult.csvFileContent)) {
            return false;
        }
        return this.truncated == copyCsvContentResult.truncated;
    }
}

