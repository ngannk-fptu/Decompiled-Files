/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.MonthDay
 */
package org.springframework.format.datetime.joda;

import java.text.ParseException;
import java.util.Locale;
import org.joda.time.MonthDay;
import org.springframework.format.Formatter;

class MonthDayFormatter
implements Formatter<MonthDay> {
    MonthDayFormatter() {
    }

    @Override
    public MonthDay parse(String text, Locale locale) throws ParseException {
        return MonthDay.parse((String)text);
    }

    @Override
    public String print(MonthDay object, Locale locale) {
        return object.toString();
    }
}

