/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.ReadablePartial
 *  org.joda.time.format.DateTimeFormatter
 */
package org.springframework.format.datetime.joda;

import java.util.Locale;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.format.Printer;
import org.springframework.format.datetime.joda.JodaTimeContextHolder;

public final class ReadablePartialPrinter
implements Printer<ReadablePartial> {
    private final DateTimeFormatter formatter;

    public ReadablePartialPrinter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public String print(ReadablePartial partial, Locale locale) {
        return JodaTimeContextHolder.getFormatter(this.formatter, locale).print(partial);
    }
}

