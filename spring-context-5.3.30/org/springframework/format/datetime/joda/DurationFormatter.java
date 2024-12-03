/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.Duration
 */
package org.springframework.format.datetime.joda;

import java.text.ParseException;
import java.util.Locale;
import org.joda.time.Duration;
import org.springframework.format.Formatter;

class DurationFormatter
implements Formatter<Duration> {
    DurationFormatter() {
    }

    @Override
    public Duration parse(String text, Locale locale) throws ParseException {
        return Duration.parse((String)text);
    }

    @Override
    public String print(Duration object, Locale locale) {
        return object.toString();
    }
}

