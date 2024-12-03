/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.ratelimiting.history;

import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public enum RateLimitingReportOrder {
    FREQUENCY,
    NEWEST;


    @Nonnull
    public static RateLimitingReportOrder fromString(@Nullable String value, @Nullable RateLimitingReportOrder selectedOrder) {
        if (StringUtils.isEmpty((CharSequence)value)) {
            return Objects.nonNull((Object)selectedOrder) ? selectedOrder : FREQUENCY;
        }
        return RateLimitingReportOrder.valueOf(StringUtils.upperCase((String)value, (Locale)Locale.ROOT));
    }
}

