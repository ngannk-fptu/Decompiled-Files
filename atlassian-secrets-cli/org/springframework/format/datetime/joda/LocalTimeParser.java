/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.LocalTime
 *  org.joda.time.format.DateTimeFormatter
 */
package org.springframework.format.datetime.joda;

import java.text.ParseException;
import java.util.Locale;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.format.Parser;
import org.springframework.format.datetime.joda.JodaTimeContextHolder;

public final class LocalTimeParser
implements Parser<LocalTime> {
    private final DateTimeFormatter formatter;

    public LocalTimeParser(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public LocalTime parse(String text, Locale locale) throws ParseException {
        return JodaTimeContextHolder.getFormatter(this.formatter, locale).parseLocalTime(text);
    }
}

