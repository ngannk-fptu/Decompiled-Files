/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.format.DateTimeFormatter
 */
package org.springframework.format.datetime.joda;

import java.util.Locale;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.format.Printer;
import org.springframework.format.datetime.joda.JodaTimeContextHolder;

@Deprecated
public final class MillisecondInstantPrinter
implements Printer<Long> {
    private final DateTimeFormatter formatter;

    public MillisecondInstantPrinter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public String print(Long instant, Locale locale) {
        return JodaTimeContextHolder.getFormatter(this.formatter, locale).print(instant.longValue());
    }
}

