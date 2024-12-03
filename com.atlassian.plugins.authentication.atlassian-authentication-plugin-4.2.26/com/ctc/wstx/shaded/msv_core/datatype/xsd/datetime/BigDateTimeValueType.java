/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.BigTimeDurationValueType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.IDateTimeValueType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.ITimeDurationValueType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.PreciseCalendarFormatter;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.TimeZone;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.Util;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class BigDateTimeValueType
implements IDateTimeValueType {
    private BigInteger year;
    private Integer month;
    private Integer day;
    private Integer hour;
    private Integer minute;
    private BigDecimal second;
    private java.util.TimeZone zone;
    private IDateTimeValueType normalizedValue = null;
    private static final long serialVersionUID = 1L;

    public BigInteger getYear() {
        return this.year;
    }

    public Integer getMonth() {
        return this.month;
    }

    public Integer getDay() {
        return this.day;
    }

    public Integer getHour() {
        return this.hour;
    }

    public Integer getMinute() {
        return this.minute;
    }

    public BigDecimal getSecond() {
        return this.second;
    }

    public java.util.TimeZone getTimeZone() {
        return this.zone;
    }

    public BigDateTimeValueType(BigDateTimeValueType base, java.util.TimeZone newTimeZone) {
        this(base.year, base.month, base.day, base.hour, base.minute, base.second, newTimeZone);
    }

    public BigDateTimeValueType(BigInteger year, int month, int day, int hour, int minute, BigDecimal second, java.util.TimeZone timeZone) {
        this(year, new Integer(month), new Integer(day), new Integer(hour), new Integer(minute), second, timeZone);
    }

    public BigDateTimeValueType(BigInteger year, Integer month, Integer day, Integer hour, Integer minute, BigDecimal second, java.util.TimeZone timeZone) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.zone = timeZone;
    }

    public BigDateTimeValueType() {
    }

    public BigDateTimeValueType getBigValue() {
        return this;
    }

    public boolean equals(Object o) {
        return this.equals((IDateTimeValueType)o);
    }

    public boolean equals(IDateTimeValueType rhs) {
        if (!(rhs instanceof BigDateTimeValueType)) {
            rhs = rhs.getBigValue();
        }
        return this.equals(this, (BigDateTimeValueType)rhs);
    }

    public boolean equals(BigDateTimeValueType lhs, BigDateTimeValueType rhs) {
        return BigDateTimeValueType.compare(lhs, rhs) == 0;
    }

    public String toString() {
        return PreciseCalendarFormatter.format("%Y-%M-%DT%h:%m:%s%z", this);
    }

    public int hashCode() {
        BigDateTimeValueType n = (BigDateTimeValueType)this.normalize();
        return Util.objHashCode(n.year) + Util.objHashCode(n.month) + Util.objHashCode(n.day) + Util.objHashCode(n.hour) + Util.objHashCode(n.minute) + Util.objHashCode(n.second) + Util.objHashCode(n.zone);
    }

    public int compare(IDateTimeValueType o) {
        if (!(o instanceof BigDateTimeValueType)) {
            o = o.getBigValue();
        }
        return BigDateTimeValueType.compare(this, (BigDateTimeValueType)o);
    }

    protected static int compare(BigDateTimeValueType lhs, BigDateTimeValueType rhs) {
        lhs = (BigDateTimeValueType)lhs.normalize();
        rhs = (BigDateTimeValueType)rhs.normalize();
        if (lhs.zone != null && rhs.zone != null || lhs.zone == null && rhs.zone == null) {
            if (!Util.objEqual(lhs.year, rhs.year)) {
                return Util.objCompare(lhs.year, rhs.year);
            }
            if (!Util.objEqual(lhs.month, rhs.month)) {
                return Util.objCompare(lhs.month, rhs.month);
            }
            if (!Util.objEqual(lhs.day, rhs.day)) {
                return Util.objCompare(lhs.day, rhs.day);
            }
            if (!Util.objEqual(lhs.hour, rhs.hour)) {
                return Util.objCompare(lhs.hour, rhs.hour);
            }
            if (!Util.objEqual(lhs.minute, rhs.minute)) {
                return Util.objCompare(lhs.minute, rhs.minute);
            }
            if (!Util.objEqual(lhs.second, rhs.second)) {
                return Util.objCompare(lhs.second, rhs.second);
            }
            return 0;
        }
        if (lhs.zone == null) {
            int r = BigDateTimeValueType.compare((BigDateTimeValueType)new BigDateTimeValueType(lhs, Util.timeZoneNeg14).normalize(), rhs);
            if (r == 0 || r == -1) {
                return -1;
            }
            r = BigDateTimeValueType.compare((BigDateTimeValueType)new BigDateTimeValueType(lhs, Util.timeZonePos14).normalize(), rhs);
            if (r == 0 || r == 1) {
                return 1;
            }
            return 999;
        }
        int r = BigDateTimeValueType.compare(lhs, new BigDateTimeValueType(rhs, Util.timeZonePos14));
        if (r == 0 || r == -1) {
            return -1;
        }
        r = BigDateTimeValueType.compare(lhs, new BigDateTimeValueType(rhs, Util.timeZoneNeg14));
        if (r == 0 || r == 1) {
            return 1;
        }
        return 999;
    }

    public IDateTimeValueType normalize() {
        if (this.zone == TimeZone.ZERO || this.zone == null) {
            return this;
        }
        if (this.normalizedValue != null) {
            return this.normalizedValue;
        }
        this.normalizedValue = this.add(BigTimeDurationValueType.fromMinutes(-this.zone.getRawOffset() / 60000));
        ((BigDateTimeValueType)this.normalizedValue).zone = TimeZone.ZERO;
        return this.normalizedValue;
    }

    private static BigInteger nullAs0(BigInteger o) {
        if (o != null) {
            return o;
        }
        return BigInteger.ZERO;
    }

    private static BigDecimal nullAs0(BigDecimal o) {
        if (o != null) {
            return o;
        }
        return Util.decimal0;
    }

    private static BigInteger[] divideAndRemainder(BigInteger x1, BigInteger x2) {
        BigInteger[] r = x1.divideAndRemainder(x2);
        if (r[1].signum() < 0) {
            r[1] = r[1].add(x2);
            r[0] = r[0].subtract(BigInteger.ONE);
        }
        return r;
    }

    public IDateTimeValueType add(ITimeDurationValueType _rhs) {
        if (_rhs instanceof BigTimeDurationValueType) {
            int dayValue;
            BigTimeDurationValueType rhs = (BigTimeDurationValueType)_rhs;
            BigInteger[] quoAndMod = BigDateTimeValueType.divideAndRemainder(Util.int2bi(this.month).add(this.signed(rhs, rhs.month)), Util.the12);
            int omonth = quoAndMod[1].intValue();
            BigInteger oyear = quoAndMod[0].add(BigDateTimeValueType.nullAs0(this.year)).add(this.signed(rhs, rhs.year));
            BigDecimal sec = BigDateTimeValueType.nullAs0(this.second).add(this.signed(rhs, rhs.second));
            quoAndMod = BigDateTimeValueType.divideAndRemainder(sec.unscaledValue(), Util.the60.multiply(Util.the10.pow(sec.scale())));
            BigDecimal osecond = new BigDecimal(quoAndMod[1], sec.scale());
            quoAndMod = BigDateTimeValueType.divideAndRemainder(quoAndMod[0].add(Util.int2bi(this.minute)).add(this.signed(rhs, rhs.minute)), Util.the60);
            int ominute = quoAndMod[1].intValue();
            quoAndMod = BigDateTimeValueType.divideAndRemainder(quoAndMod[0].add(Util.int2bi(this.hour)).add(this.signed(rhs, rhs.hour)), Util.the24);
            int ohour = quoAndMod[1].intValue();
            int md = Util.maximumDayInMonthFor(oyear, omonth);
            int n = dayValue = this.day != null ? this.day : 0;
            int tempDays = dayValue < 0 ? 0 : (dayValue >= md ? md - 1 : dayValue);
            BigInteger oday = this.signed(rhs, rhs.day).add(quoAndMod[0]).add(Util.int2bi(tempDays));
            while (true) {
                int carry;
                if (oday.signum() == -1) {
                    oday = oday.add(Util.int2bi(Util.maximumDayInMonthFor(oyear, (omonth + 11) % 12)));
                    carry = -1;
                } else {
                    BigInteger bmd = Util.int2bi(Util.maximumDayInMonthFor(oyear, omonth));
                    if (oday.compareTo(bmd) < 0) break;
                    oday = oday.subtract(bmd);
                    carry = 1;
                }
                if ((omonth += carry) < 0) {
                    omonth += 12;
                    oyear = oyear.subtract(BigInteger.ONE);
                }
                oyear = oyear.add(Util.int2bi(omonth / 12));
                omonth %= 12;
            }
            return new BigDateTimeValueType(this.year != null ? oyear : null, this.month != null ? new Integer(omonth) : null, this.day != null ? new Integer(oday.intValue()) : null, this.hour != null ? new Integer(ohour) : null, this.minute != null ? new Integer(ominute) : null, this.second != null ? osecond : null, this.zone);
        }
        return this.add(_rhs.getBigValue());
    }

    private BigInteger signed(BigTimeDurationValueType dur, BigInteger i) {
        if (dur.signum < 0) {
            return i.negate();
        }
        return i;
    }

    private BigDecimal signed(BigTimeDurationValueType dur, BigDecimal i) {
        if (dur.signum < 0) {
            return i.negate();
        }
        return i;
    }

    public Calendar toCalendar() {
        GregorianCalendar cal = new GregorianCalendar(0, 0, 0);
        ((Calendar)cal).setTimeZone(this.createJavaTimeZone());
        cal.clear(1);
        cal.clear(2);
        cal.clear(5);
        if (this.getYear() != null) {
            cal.set(1, this.getYear().intValue());
        }
        if (this.getMonth() != null) {
            cal.set(2, this.getMonth());
        }
        if (this.getDay() != null) {
            cal.set(5, this.getDay() + 1);
        }
        if (this.getHour() != null) {
            cal.set(11, this.getHour());
        }
        if (this.getMinute() != null) {
            cal.set(12, this.getMinute());
        }
        if (this.getSecond() != null) {
            cal.set(13, this.getSecond().intValue());
            cal.set(14, this.getSecond().movePointRight(3).intValue() % 1000);
        }
        return cal;
    }

    protected java.util.TimeZone createJavaTimeZone() {
        java.util.TimeZone tz = this.getTimeZone();
        if (tz == null) {
            return TimeZone.MISSING;
        }
        return tz;
    }
}

