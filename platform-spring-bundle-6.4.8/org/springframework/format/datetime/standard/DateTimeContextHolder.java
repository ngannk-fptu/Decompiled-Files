/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.format.datetime.standard;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.core.NamedThreadLocal;
import org.springframework.format.datetime.standard.DateTimeContext;
import org.springframework.lang.Nullable;

public final class DateTimeContextHolder {
    private static final ThreadLocal<DateTimeContext> dateTimeContextHolder = new NamedThreadLocal<DateTimeContext>("DateTimeContext");

    private DateTimeContextHolder() {
    }

    public static void resetDateTimeContext() {
        dateTimeContextHolder.remove();
    }

    public static void setDateTimeContext(@Nullable DateTimeContext dateTimeContext) {
        if (dateTimeContext == null) {
            DateTimeContextHolder.resetDateTimeContext();
        } else {
            dateTimeContextHolder.set(dateTimeContext);
        }
    }

    @Nullable
    public static DateTimeContext getDateTimeContext() {
        return dateTimeContextHolder.get();
    }

    public static DateTimeFormatter getFormatter(DateTimeFormatter formatter, @Nullable Locale locale) {
        DateTimeFormatter formatterToUse = locale != null ? formatter.withLocale(locale) : formatter;
        DateTimeContext context = DateTimeContextHolder.getDateTimeContext();
        return context != null ? context.getFormatter(formatterToUse) : formatterToUse;
    }
}

