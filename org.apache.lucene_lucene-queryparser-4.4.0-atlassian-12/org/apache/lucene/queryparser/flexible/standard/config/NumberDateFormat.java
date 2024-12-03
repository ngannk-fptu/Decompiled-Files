/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.config;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Date;

public class NumberDateFormat
extends NumberFormat {
    private static final long serialVersionUID = 964823936071308283L;
    private final DateFormat dateFormat;

    public NumberDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
        return this.dateFormat.format(new Date((long)number), toAppendTo, pos);
    }

    @Override
    public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
        return this.dateFormat.format(new Date(number), toAppendTo, pos);
    }

    @Override
    public Number parse(String source, ParsePosition parsePosition) {
        Date date = this.dateFormat.parse(source, parsePosition);
        return date == null ? null : Long.valueOf(date.getTime());
    }

    @Override
    public StringBuffer format(Object number, StringBuffer toAppendTo, FieldPosition pos) {
        return this.dateFormat.format(number, toAppendTo, pos);
    }
}

