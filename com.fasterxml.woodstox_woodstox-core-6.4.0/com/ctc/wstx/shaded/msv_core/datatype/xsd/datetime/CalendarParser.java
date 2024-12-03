/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarParser;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public final class CalendarParser
extends AbstractCalendarParser {
    private final GregorianCalendar cal = new GregorianCalendar(0, 0, 0);

    public static GregorianCalendar parse(String format, String value) throws IllegalArgumentException {
        CalendarParser parser = new CalendarParser(format, value);
        parser.parse();
        return parser.cal;
    }

    private CalendarParser(String format, String value) {
        super(format, value);
        this.cal.clear(1);
        this.cal.clear(2);
        this.cal.clear(5);
    }

    protected void parseFractionSeconds() {
        this.cal.set(14, this.parseInt(1, 3));
        this.skipDigits();
    }

    protected void setTimeZone(TimeZone tz) {
        this.cal.setTimeZone(tz);
    }

    protected void setSeconds(int i) {
        this.cal.set(13, i);
    }

    protected void setMinutes(int i) {
        this.cal.set(12, i);
    }

    protected void setHours(int i) {
        this.cal.set(11, i);
    }

    protected void setDay(int i) {
        this.cal.set(5, i);
    }

    protected void setMonth(int i) {
        this.cal.set(2, i - 1);
    }

    protected void setYear(int i) {
        this.cal.set(1, i);
    }
}

