/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.format.datetime.standard;

import java.text.ParseException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.format.Formatter;

public class InstantFormatter
implements Formatter<Instant> {
    @Override
    public Instant parse(String text, Locale locale) throws ParseException {
        if (text.length() > 0 && Character.isDigit(text.charAt(0))) {
            return Instant.parse(text);
        }
        return Instant.from(DateTimeFormatter.RFC_1123_DATE_TIME.parse(text));
    }

    @Override
    public String print(Instant object, Locale locale) {
        return object.toString();
    }
}

