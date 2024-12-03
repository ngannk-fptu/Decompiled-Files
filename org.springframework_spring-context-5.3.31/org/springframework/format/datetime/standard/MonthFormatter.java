/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.format.datetime.standard;

import java.text.ParseException;
import java.time.Month;
import java.util.Locale;
import org.springframework.format.Formatter;

class MonthFormatter
implements Formatter<Month> {
    MonthFormatter() {
    }

    @Override
    public Month parse(String text, Locale locale) throws ParseException {
        return Month.valueOf(text.toUpperCase());
    }

    @Override
    public String print(Month object, Locale locale) {
        return object.toString();
    }
}

