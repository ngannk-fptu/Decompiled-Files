/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.confluence.util.FugueConversionUtil;
import com.atlassian.fugue.Option;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface BuildNumberRangeChecker {
    @Deprecated
    default public boolean isBuildNumberInRange(String currentBuildNumber, Option<Integer> minBuildNumber, Option<Integer> maxBuildNumber) {
        return this.checkifBuildNumberInRange(currentBuildNumber, (Integer)minBuildNumber.getOrNull(), (Integer)maxBuildNumber.getOrNull());
    }

    public boolean checkifBuildNumberInRange(String var1, @Nullable Integer var2, @Nullable Integer var3);

    @Deprecated
    default public Option<Integer> parseBuildNumberOrThrow(String buildNumber, String exceptionMessage) {
        return FugueConversionUtil.toComOption(this.parseBuildNumberOrFail(buildNumber, exceptionMessage));
    }

    public Optional<Integer> parseBuildNumberOrFail(String var1, String var2);
}

