/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.format.datetime.standard;

import java.text.ParseException;
import java.time.Year;
import java.util.Locale;
import org.springframework.format.Formatter;

class YearFormatter
implements Formatter<Year> {
    YearFormatter() {
    }

    @Override
    public Year parse(String text, Locale locale) throws ParseException {
        return Year.parse(text);
    }

    @Override
    public String print(Year object, Locale locale) {
        return object.toString();
    }
}

