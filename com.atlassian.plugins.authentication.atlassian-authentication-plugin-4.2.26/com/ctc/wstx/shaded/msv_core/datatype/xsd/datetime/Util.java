/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

class Util {
    protected static final BigInteger the4 = new BigInteger("4");
    protected static final BigInteger the10 = new BigInteger("10");
    protected static final BigInteger the12 = new BigInteger("12");
    protected static final BigInteger the24 = new BigInteger("24");
    protected static final BigInteger the60 = new BigInteger("60");
    protected static final BigInteger the100 = new BigInteger("100");
    protected static final BigInteger the400 = new BigInteger("400");
    protected static final BigInteger the210379680 = new BigInteger("210379680");
    protected static final BigDecimal decimal0 = new BigDecimal(BigInteger.ZERO, 0);
    protected static final Integer int0 = new Integer(0);
    protected static TimeZone timeZonePos14 = new SimpleTimeZone(50400000, "");
    protected static TimeZone timeZoneNeg14 = new SimpleTimeZone(-50400000, "");
    private static final int[] dayInMonth = new int[]{31, -1, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    Util() {
    }

    protected static boolean objEqual(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return true;
        }
        return o1 != null && o2 != null && o1.equals(o2);
    }

    protected static int objHashCode(Object o) {
        if (o == null) {
            return 0;
        }
        return o.hashCode();
    }

    protected static int objCompare(Comparable o1, Comparable o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 != null && o2 != null) {
            int r = o1.compareTo(o2);
            if (r < 0) {
                return -1;
            }
            if (r > 0) {
                return 1;
            }
            return 0;
        }
        return 999;
    }

    protected static BigInteger int2bi(int v) {
        return new BigInteger(Integer.toString(v));
    }

    protected static BigInteger int2bi(Integer v) {
        if (v == null) {
            return BigInteger.ZERO;
        }
        return new BigInteger(v.toString());
    }

    public static int maximumDayInMonthFor(int year, int month) {
        if (month == 1) {
            if (year % 400 == 0) {
                return 29;
            }
            if (year % 4 == 0 && year % 100 != 0) {
                return 29;
            }
            return 28;
        }
        return dayInMonth[month];
    }

    public static int maximumDayInMonthFor(BigInteger year, int month) {
        if (month == 1) {
            if (year.mod(the400).intValue() == 0) {
                return 29;
            }
            if (year.mod(the4).intValue() == 0 && year.mod(the100).intValue() != 0) {
                return 29;
            }
            return 28;
        }
        return dayInMonth[month];
    }
}

