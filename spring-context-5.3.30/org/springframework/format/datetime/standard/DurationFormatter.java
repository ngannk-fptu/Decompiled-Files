/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.format.datetime.standard;

import java.text.ParseException;
import java.time.Duration;
import java.util.Locale;
import org.springframework.format.Formatter;

class DurationFormatter
implements Formatter<Duration> {
    DurationFormatter() {
    }

    @Override
    public Duration parse(String text, Locale locale) throws ParseException {
        return Duration.parse(text);
    }

    @Override
    public String print(Duration object, Locale locale) {
        return object.toString();
    }
}

