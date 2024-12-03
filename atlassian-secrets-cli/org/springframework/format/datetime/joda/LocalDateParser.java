/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.LocalDate
 *  org.joda.time.format.DateTimeFormatter
 */
package org.springframework.format.datetime.joda;

import java.text.ParseException;
import java.util.Locale;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.format.Parser;
import org.springframework.format.datetime.joda.JodaTimeContextHolder;

public final class LocalDateParser
implements Parser<LocalDate> {
    private final DateTimeFormatter formatter;

    public LocalDateParser(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public LocalDate parse(String text, Locale locale) throws ParseException {
        return JodaTimeContextHolder.getFormatter(this.formatter, locale).parseLocalDate(text);
    }
}

