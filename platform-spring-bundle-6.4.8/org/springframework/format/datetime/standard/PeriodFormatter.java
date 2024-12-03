/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.format.datetime.standard;

import java.text.ParseException;
import java.time.Period;
import java.util.Locale;
import org.springframework.format.Formatter;

class PeriodFormatter
implements Formatter<Period> {
    PeriodFormatter() {
    }

    @Override
    public Period parse(String text, Locale locale) throws ParseException {
        return Period.parse(text);
    }

    @Override
    public String print(Period object, Locale locale) {
        return object.toString();
    }
}

