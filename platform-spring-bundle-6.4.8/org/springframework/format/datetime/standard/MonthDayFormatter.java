/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.format.datetime.standard;

import java.text.ParseException;
import java.time.MonthDay;
import java.util.Locale;
import org.springframework.format.Formatter;

class MonthDayFormatter
implements Formatter<MonthDay> {
    MonthDayFormatter() {
    }

    @Override
    public MonthDay parse(String text, Locale locale) throws ParseException {
        return MonthDay.parse(text);
    }

    @Override
    public String print(MonthDay object, Locale locale) {
        return object.toString();
    }
}

