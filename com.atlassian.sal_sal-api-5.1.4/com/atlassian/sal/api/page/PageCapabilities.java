/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.sal.api.page;

import com.atlassian.sal.api.page.PageCapability;
import com.google.common.base.Joiner;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class PageCapabilities {
    public static final String SEPARATOR = ",";

    public static EnumSet<PageCapability> empty() {
        return EnumSet.noneOf(PageCapability.class);
    }

    public static EnumSet<PageCapability> valueOf(@Nullable String values) {
        if (values == null || values.length() == 0) {
            return PageCapabilities.empty();
        }
        return Arrays.stream(values.split(SEPARATOR)).map(value -> {
            try {
                return PageCapability.valueOf(value);
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toCollection(() -> EnumSet.noneOf(PageCapability.class)));
    }

    public static String toString(@Nonnull EnumSet<PageCapability> pageCaps) {
        return Joiner.on((String)SEPARATOR).join(pageCaps.iterator());
    }
}

