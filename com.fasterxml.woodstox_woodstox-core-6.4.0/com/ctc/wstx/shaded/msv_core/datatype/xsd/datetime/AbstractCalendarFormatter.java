/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.TimeZone;
import java.util.Calendar;

abstract class AbstractCalendarFormatter {
    AbstractCalendarFormatter() {
    }

    public String doFormat(String format, Object cal) throws IllegalArgumentException {
        int fidx = 0;
        int flen = format.length();
        StringBuffer buf = new StringBuffer();
        block9: while (fidx < flen) {
            char fch;
            if ((fch = format.charAt(fidx++)) != '%') {
                buf.append(fch);
                continue;
            }
            switch (format.charAt(fidx++)) {
                case 'Y': {
                    this.formatYear(cal, buf);
                    continue block9;
                }
                case 'M': {
                    this.formatMonth(cal, buf);
                    continue block9;
                }
                case 'D': {
                    this.formatDays(cal, buf);
                    continue block9;
                }
                case 'h': {
                    this.formatHours(cal, buf);
                    continue block9;
                }
                case 'm': {
                    this.formatMinutes(cal, buf);
                    continue block9;
                }
                case 's': {
                    this.formatSeconds(cal, buf);
                    continue block9;
                }
                case 'z': {
                    this.formatTimeZone(cal, buf);
                    continue block9;
                }
            }
            throw new InternalError();
        }
        return buf.toString();
    }

    protected abstract Calendar toCalendar(Object var1);

    protected abstract void formatYear(Object var1, StringBuffer var2);

    protected abstract void formatMonth(Object var1, StringBuffer var2);

    protected abstract void formatDays(Object var1, StringBuffer var2);

    protected abstract void formatHours(Object var1, StringBuffer var2);

    protected abstract void formatMinutes(Object var1, StringBuffer var2);

    protected abstract void formatSeconds(Object var1, StringBuffer var2);

    private void formatTimeZone(Object _cal, StringBuffer buf) {
        Calendar cal = this.toCalendar(_cal);
        java.util.TimeZone tz = cal.getTimeZone();
        if (tz == null) {
            return;
        }
        if (tz == TimeZone.MISSING) {
            return;
        }
        if (tz == TimeZone.ZERO) {
            buf.append('Z');
            return;
        }
        int offset = tz.inDaylightTime(cal.getTime()) ? tz.getRawOffset() + (tz.useDaylightTime() ? 3600000 : 0) : tz.getRawOffset();
        if (offset >= 0) {
            buf.append('+');
        } else {
            buf.append('-');
            offset *= -1;
        }
        this.formatTwoDigits((offset /= 60000) / 60, buf);
        buf.append(':');
        this.formatTwoDigits(offset % 60, buf);
    }

    protected final void formatTwoDigits(int n, StringBuffer buf) {
        if (n < 10) {
            buf.append('0');
        }
        buf.append(n);
    }
}

