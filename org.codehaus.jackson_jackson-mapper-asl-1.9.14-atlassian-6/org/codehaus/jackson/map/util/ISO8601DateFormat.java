/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.codehaus.jackson.map.util.ISO8601Utils;

public class ISO8601DateFormat
extends DateFormat {
    private static final long serialVersionUID = 1L;
    private static Calendar CALENDAR = new GregorianCalendar();
    private static NumberFormat NUMBER_FORMAT = new DecimalFormat();

    public ISO8601DateFormat() {
        this.numberFormat = NUMBER_FORMAT;
        this.calendar = CALENDAR;
    }

    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        String value = ISO8601Utils.format(date);
        toAppendTo.append(value);
        return toAppendTo;
    }

    public Date parse(String source, ParsePosition pos) {
        pos.setIndex(source.length());
        return ISO8601Utils.parse(source);
    }

    public Object clone() {
        return this;
    }
}

