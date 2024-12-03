/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.YearMonth
 */
package org.springframework.format.datetime.joda;

import java.text.ParseException;
import java.util.Locale;
import org.joda.time.YearMonth;
import org.springframework.format.Formatter;

class YearMonthFormatter
implements Formatter<YearMonth> {
    YearMonthFormatter() {
    }

    @Override
    public YearMonth parse(String text, Locale locale) throws ParseException {
        return YearMonth.parse((String)text);
    }

    @Override
    public String print(YearMonth object, Locale locale) {
        return object.toString();
    }
}

