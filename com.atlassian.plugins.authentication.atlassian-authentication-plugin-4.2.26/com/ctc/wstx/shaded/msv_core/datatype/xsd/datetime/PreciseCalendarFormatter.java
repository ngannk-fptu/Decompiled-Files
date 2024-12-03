/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarFormatter;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.BigDateTimeValueType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.IDateTimeValueType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;

public class PreciseCalendarFormatter
extends AbstractCalendarFormatter {
    private static final PreciseCalendarFormatter theInstance = new PreciseCalendarFormatter();

    private PreciseCalendarFormatter() {
    }

    public static String format(String format, IDateTimeValueType cal) {
        return theInstance.doFormat(format, cal.getBigValue());
    }

    protected Calendar toCalendar(Object cal) {
        return ((BigDateTimeValueType)cal).toCalendar();
    }

    protected void formatYear(Object cal, StringBuffer buf) {
        String s;
        BigDateTimeValueType bv = ((IDateTimeValueType)cal).getBigValue();
        BigInteger year = bv.getYear();
        if (year == null) {
            buf.append("0000");
            return;
        }
        if (year.signum() <= 0) {
            buf.append('-');
            s = year.negate().add(BigInteger.ONE).toString();
        } else {
            s = year.toString();
        }
        while (s.length() < 4) {
            s = "0" + s;
        }
        buf.append(s);
    }

    protected void formatMonth(Object cal, StringBuffer buf) {
        BigDateTimeValueType bv = ((IDateTimeValueType)cal).getBigValue();
        this.formatTwoDigits(bv.getMonth(), 1, buf);
    }

    protected void formatDays(Object cal, StringBuffer buf) {
        BigDateTimeValueType bv = ((IDateTimeValueType)cal).getBigValue();
        this.formatTwoDigits(bv.getDay(), 1, buf);
    }

    protected void formatHours(Object cal, StringBuffer buf) {
        BigDateTimeValueType bv = ((IDateTimeValueType)cal).getBigValue();
        this.formatTwoDigits(bv.getHour(), buf);
    }

    protected void formatMinutes(Object cal, StringBuffer buf) {
        BigDateTimeValueType bv = ((IDateTimeValueType)cal).getBigValue();
        this.formatTwoDigits(bv.getMinute(), buf);
    }

    protected void formatSeconds(Object cal, StringBuffer buf) {
        BigDateTimeValueType bv = ((IDateTimeValueType)cal).getBigValue();
        BigDecimal sec = bv.getSecond();
        if (sec == null) {
            buf.append("00");
            return;
        }
        while (sec.scale() > 0 && sec.toString().endsWith("0")) {
            sec = sec.movePointLeft(1);
        }
        String s = sec.toString();
        if (sec.compareTo(new BigDecimal("10")) < 0) {
            s = "0" + s;
        }
        buf.append(s);
    }

    private void formatTwoDigits(Integer v, StringBuffer buf) {
        this.formatTwoDigits(v, 0, buf);
    }

    private void formatTwoDigits(Integer v, int offset, StringBuffer buf) {
        if (v == null) {
            buf.append("00");
        } else {
            this.formatTwoDigits(v + offset, buf);
        }
    }
}

