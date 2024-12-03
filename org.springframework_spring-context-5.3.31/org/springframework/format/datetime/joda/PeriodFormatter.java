/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.Period
 */
package org.springframework.format.datetime.joda;

import java.text.ParseException;
import java.util.Locale;
import org.joda.time.Period;
import org.springframework.format.Formatter;

class PeriodFormatter
implements Formatter<Period> {
    PeriodFormatter() {
    }

    @Override
    public Period parse(String text, Locale locale) throws ParseException {
        return Period.parse((String)text);
    }

    @Override
    public String print(Period object, Locale locale) {
        return object.toString();
    }
}

