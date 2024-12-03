/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.util;

import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class NumberUtil {
    public static @NonNull Optional<Integer> parseInteger(@Nullable String string) {
        try {
            return Optional.of(Integer.parseInt(StringUtils.trimToEmpty((String)string)));
        }
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private NumberUtil() {
    }
}

