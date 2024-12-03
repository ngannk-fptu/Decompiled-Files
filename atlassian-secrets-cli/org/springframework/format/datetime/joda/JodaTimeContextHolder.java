/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.format.DateTimeFormatter
 */
package org.springframework.format.datetime.joda;

import java.util.Locale;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.core.NamedThreadLocal;
import org.springframework.format.datetime.joda.JodaTimeContext;
import org.springframework.lang.Nullable;

public final class JodaTimeContextHolder {
    private static final ThreadLocal<JodaTimeContext> jodaTimeContextHolder = new NamedThreadLocal<JodaTimeContext>("JodaTimeContext");

    public static void resetJodaTimeContext() {
        jodaTimeContextHolder.remove();
    }

    public static void setJodaTimeContext(@Nullable JodaTimeContext jodaTimeContext) {
        if (jodaTimeContext == null) {
            JodaTimeContextHolder.resetJodaTimeContext();
        } else {
            jodaTimeContextHolder.set(jodaTimeContext);
        }
    }

    @Nullable
    public static JodaTimeContext getJodaTimeContext() {
        return jodaTimeContextHolder.get();
    }

    public static DateTimeFormatter getFormatter(DateTimeFormatter formatter, @Nullable Locale locale) {
        DateTimeFormatter formatterToUse = locale != null ? formatter.withLocale(locale) : formatter;
        JodaTimeContext context = JodaTimeContextHolder.getJodaTimeContext();
        return context != null ? context.getFormatter(formatterToUse) : formatterToUse;
    }
}

