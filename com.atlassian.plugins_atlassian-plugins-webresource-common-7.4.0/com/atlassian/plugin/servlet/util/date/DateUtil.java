/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.plugin.servlet.util.date;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class DateUtil {
    private DateUtil() {
    }

    public static Optional<LocalDateTime> localDateTimeOf(@Nullable Date date) {
        return Optional.ofNullable(date).filter(value -> value.getTime() >= 0L).map(Date::toInstant).map(instant -> instant.atZone(ZoneOffset.UTC)).map(ZonedDateTime::toLocalDateTime);
    }

    public static LocalDateTime defaultIfNull(@Nullable Date date, @Nonnull LocalDateTime defaultValue) {
        return DateUtil.localDateTimeOf(date).orElse(defaultValue);
    }
}

