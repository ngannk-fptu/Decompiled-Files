/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarParser;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.BigDateTimeValueType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.TimeZone;
import java.math.BigDecimal;
import java.math.BigInteger;

public class PreciseCalendarParser
extends AbstractCalendarParser {
    private BigInteger year;
    private Integer month;
    private Integer day;
    private Integer hour;
    private Integer minute;
    private BigDecimal second;
    private java.util.TimeZone timeZone;

    public static BigDateTimeValueType parse(String format, String value) throws IllegalArgumentException {
        PreciseCalendarParser parser = new PreciseCalendarParser(format, value);
        parser.parse();
        return parser.createCalendar();
    }

    private PreciseCalendarParser(String format, String value) {
        super(format, value);
    }

    private BigDateTimeValueType createCalendar() {
        return new BigDateTimeValueType(this.year, this.month, this.day, this.hour, this.minute, this.second, this.timeZone);
    }

    protected void parseFractionSeconds() {
        int s = this.vidx;
        BigInteger bi = this.parseBigInteger(1, Integer.MAX_VALUE);
        BigDecimal d = new BigDecimal(bi, this.vidx - s);
        this.second = this.second == null ? d : this.second.add(d);
    }

    protected void setTimeZone(java.util.TimeZone tz) {
        if (tz == TimeZone.MISSING) {
            tz = null;
        }
        this.timeZone = tz;
    }

    protected void setSeconds(int i) {
        BigDecimal d = new BigDecimal(BigInteger.valueOf(i));
        this.second = this.second == null ? d : this.second.add(d);
    }

    protected void setMinutes(int i) {
        this.minute = new Integer(i);
    }

    protected void setHours(int i) {
        this.hour = new Integer(i);
    }

    protected void setDay(int i) {
        this.day = new Integer(i - 1);
    }

    protected void setMonth(int i) {
        this.month = new Integer(i - 1);
    }

    protected void setYear(int i) {
        this.year = BigInteger.valueOf(i);
    }
}

