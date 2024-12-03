/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CharMatcher
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.base;

import com.google.common.base.CharMatcher;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class SourceLocation {
    @Nonnull
    private final String filePath;
    private final String fileName;
    private final int lineNumber;
    public static final SourceLocation UNKNOWN = new SourceLocation("unknown", 0);

    public SourceLocation(String filePath, int lineNumber) {
        int lastSlashIndex;
        int lastBangIndex = filePath.lastIndexOf(33);
        if (lastBangIndex != -1) {
            filePath = filePath.substring(lastBangIndex + 1);
        }
        this.fileName = (lastSlashIndex = CharMatcher.anyOf((CharSequence)"/\\").lastIndexIn((CharSequence)filePath)) != -1 && lastSlashIndex != filePath.length() - 1 ? filePath.substring(lastSlashIndex + 1) : filePath;
        this.filePath = filePath;
        this.lineNumber = lineNumber;
    }

    @Nonnull
    public String getFilePath() {
        return this.filePath;
    }

    @Nullable
    public String getFileName() {
        if (UNKNOWN.equals(this)) {
            return null;
        }
        return this.fileName;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public boolean isKnown() {
        return !this.equals(UNKNOWN);
    }

    public boolean equals(@Nullable Object o) {
        if (!(o instanceof SourceLocation)) {
            return false;
        }
        SourceLocation that = (SourceLocation)o;
        return this.filePath.equals(that.filePath) && this.lineNumber == that.lineNumber;
    }

    public int hashCode() {
        return this.filePath.hashCode() + 31 * this.lineNumber;
    }

    public String toString() {
        return this.lineNumber != 0 ? this.filePath + ":" + this.lineNumber : this.filePath;
    }
}

