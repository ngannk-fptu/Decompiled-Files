/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.datatype;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import org.apache.xerces.jaxp.datatype.DurationImpl;
import org.apache.xerces.jaxp.datatype.SerializedXMLGregorianCalendar;
import org.apache.xerces.util.DatatypeMessageFormatter;

class XMLGregorianCalendarImpl
extends XMLGregorianCalendar
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 3905403108073447394L;
    private BigInteger orig_eon;
    private int orig_year = Integer.MIN_VALUE;
    private int orig_month = Integer.MIN_VALUE;
    private int orig_day = Integer.MIN_VALUE;
    private int orig_hour = Integer.MIN_VALUE;
    private int orig_minute = Integer.MIN_VALUE;
    private int orig_second = Integer.MIN_VALUE;
    private BigDecimal orig_fracSeconds;
    private int orig_timezone = Integer.MIN_VALUE;
    private BigInteger eon = null;
    private int year = Integer.MIN_VALUE;
    private int month = Integer.MIN_VALUE;
    private int day = Integer.MIN_VALUE;
    private int timezone = Integer.MIN_VALUE;
    private int hour = Integer.MIN_VALUE;
    private int minute = Integer.MIN_VALUE;
    private int second = Integer.MIN_VALUE;
    private BigDecimal fractionalSecond = null;
    private static final BigInteger BILLION_B = BigInteger.valueOf(1000000000L);
    private static final int BILLION_I = 1000000000;
    private static final Date PURE_GREGORIAN_CHANGE = new Date(Long.MIN_VALUE);
    private static final int YEAR = 0;
    private static final int MONTH = 1;
    private static final int DAY = 2;
    private static final int HOUR = 3;
    private static final int MINUTE = 4;
    private static final int SECOND = 5;
    private static final int MILLISECOND = 6;
    private static final int TIMEZONE = 7;
    private static final int[] MIN_FIELD_VALUE = new int[]{Integer.MIN_VALUE, 1, 1, 0, 0, 0, 0, -840};
    private static final int[] MAX_FIELD_VALUE = new int[]{Integer.MAX_VALUE, 12, 31, 24, 59, 60, 999, 840};
    private static final String[] FIELD_NAME = new String[]{"Year", "Month", "Day", "Hour", "Minute", "Second", "Millisecond", "Timezone"};
    public static final XMLGregorianCalendar LEAP_YEAR_DEFAULT = XMLGregorianCalendarImpl.createDateTime(400, 1, 1, 0, 0, 0, Integer.MIN_VALUE, Integer.MIN_VALUE);
    private static final BigInteger FOUR = BigInteger.valueOf(4L);
    private static final BigInteger HUNDRED = BigInteger.valueOf(100L);
    private static final BigInteger FOUR_HUNDRED = BigInteger.valueOf(400L);
    private static final BigInteger SIXTY = BigInteger.valueOf(60L);
    private static final BigInteger TWENTY_FOUR = BigInteger.valueOf(24L);
    private static final BigInteger TWELVE = BigInteger.valueOf(12L);
    private static final BigDecimal DECIMAL_ZERO = BigDecimal.valueOf(0L);
    private static final BigDecimal DECIMAL_ONE = BigDecimal.valueOf(1L);
    private static final BigDecimal DECIMAL_SIXTY = BigDecimal.valueOf(60L);

    protected XMLGregorianCalendarImpl(String string) throws IllegalArgumentException {
        String string2 = null;
        String string3 = string;
        int n = string3.length();
        if (string3.indexOf(84) != -1) {
            string2 = "%Y-%M-%DT%h:%m:%s%z";
        } else if (n >= 3 && string3.charAt(2) == ':') {
            string2 = "%h:%m:%s%z";
        } else if (string3.startsWith("--")) {
            if (n >= 3 && string3.charAt(2) == '-') {
                string2 = "---%D%z";
            } else if (n == 4 || n >= 6 && (string3.charAt(4) == '+' || string3.charAt(4) == '-' && (string3.charAt(5) == '-' || n == 10))) {
                string2 = "--%M--%z";
                Parser parser = new Parser(string2, string3);
                try {
                    parser.parse();
                    if (!this.isValid()) {
                        throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "InvalidXGCRepresentation", new Object[]{string}));
                    }
                    this.save();
                    return;
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    string2 = "--%M%z";
                }
            } else {
                string2 = "--%M-%D%z";
            }
        } else {
            int n2 = 0;
            int n3 = string3.indexOf(58);
            if (n3 != -1) {
                n -= 6;
            }
            for (int i = 1; i < n; ++i) {
                if (string3.charAt(i) != '-') continue;
                ++n2;
            }
            string2 = n2 == 0 ? "%Y%z" : (n2 == 1 ? "%Y-%M%z" : "%Y-%M-%D%z");
        }
        Parser parser = new Parser(string2, string3);
        parser.parse();
        if (!this.isValid()) {
            throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "InvalidXGCRepresentation", new Object[]{string}));
        }
        this.save();
    }

    private void save() {
        this.orig_eon = this.eon;
        this.orig_year = this.year;
        this.orig_month = this.month;
        this.orig_day = this.day;
        this.orig_hour = this.hour;
        this.orig_minute = this.minute;
        this.orig_second = this.second;
        this.orig_fracSeconds = this.fractionalSecond;
        this.orig_timezone = this.timezone;
    }

    public XMLGregorianCalendarImpl() {
    }

    protected XMLGregorianCalendarImpl(BigInteger bigInteger, int n, int n2, int n3, int n4, int n5, BigDecimal bigDecimal, int n6) {
        this.setYear(bigInteger);
        this.setMonth(n);
        this.setDay(n2);
        this.setTime(n3, n4, n5, bigDecimal);
        this.setTimezone(n6);
        if (!this.isValid()) {
            throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "InvalidXGCValue-fractional", new Object[]{bigInteger, new Integer(n), new Integer(n2), new Integer(n3), new Integer(n4), new Integer(n5), bigDecimal, new Integer(n6)}));
        }
        this.save();
    }

    private XMLGregorianCalendarImpl(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        this.setYear(n);
        this.setMonth(n2);
        this.setDay(n3);
        this.setTime(n4, n5, n6);
        this.setTimezone(n8);
        BigDecimal bigDecimal = null;
        if (n7 != Integer.MIN_VALUE) {
            bigDecimal = BigDecimal.valueOf(n7, 3);
        }
        this.setFractionalSecond(bigDecimal);
        if (!this.isValid()) {
            throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "InvalidXGCValue-milli", new Object[]{new Integer(n), new Integer(n2), new Integer(n3), new Integer(n4), new Integer(n5), new Integer(n6), new Integer(n7), new Integer(n8)}));
        }
        this.save();
    }

    public XMLGregorianCalendarImpl(GregorianCalendar gregorianCalendar) {
        int n = gregorianCalendar.get(1);
        if (gregorianCalendar.get(0) == 0) {
            n = -n;
        }
        this.setYear(n);
        this.setMonth(gregorianCalendar.get(2) + 1);
        this.setDay(gregorianCalendar.get(5));
        this.setTime(gregorianCalendar.get(11), gregorianCalendar.get(12), gregorianCalendar.get(13), gregorianCalendar.get(14));
        int n2 = (gregorianCalendar.get(15) + gregorianCalendar.get(16)) / 60000;
        this.setTimezone(n2);
        this.save();
    }

    public static XMLGregorianCalendar createDateTime(BigInteger bigInteger, int n, int n2, int n3, int n4, int n5, BigDecimal bigDecimal, int n6) {
        return new XMLGregorianCalendarImpl(bigInteger, n, n2, n3, n4, n5, bigDecimal, n6);
    }

    public static XMLGregorianCalendar createDateTime(int n, int n2, int n3, int n4, int n5, int n6) {
        return new XMLGregorianCalendarImpl(n, n2, n3, n4, n5, n6, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public static XMLGregorianCalendar createDateTime(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        return new XMLGregorianCalendarImpl(n, n2, n3, n4, n5, n6, n7, n8);
    }

    public static XMLGregorianCalendar createDate(int n, int n2, int n3, int n4) {
        return new XMLGregorianCalendarImpl(n, n2, n3, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, n4);
    }

    public static XMLGregorianCalendar createTime(int n, int n2, int n3, int n4) {
        return new XMLGregorianCalendarImpl(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, n, n2, n3, Integer.MIN_VALUE, n4);
    }

    public static XMLGregorianCalendar createTime(int n, int n2, int n3, BigDecimal bigDecimal, int n4) {
        return new XMLGregorianCalendarImpl(null, Integer.MIN_VALUE, Integer.MIN_VALUE, n, n2, n3, bigDecimal, n4);
    }

    public static XMLGregorianCalendar createTime(int n, int n2, int n3, int n4, int n5) {
        return new XMLGregorianCalendarImpl(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, n, n2, n3, n4, n5);
    }

    @Override
    public BigInteger getEon() {
        return this.eon;
    }

    @Override
    public int getYear() {
        return this.year;
    }

    @Override
    public BigInteger getEonAndYear() {
        if (this.year != Integer.MIN_VALUE && this.eon != null) {
            return this.eon.add(BigInteger.valueOf(this.year));
        }
        if (this.year != Integer.MIN_VALUE && this.eon == null) {
            return BigInteger.valueOf(this.year);
        }
        return null;
    }

    @Override
    public int getMonth() {
        return this.month;
    }

    @Override
    public int getDay() {
        return this.day;
    }

    @Override
    public int getTimezone() {
        return this.timezone;
    }

    @Override
    public int getHour() {
        return this.hour;
    }

    @Override
    public int getMinute() {
        return this.minute;
    }

    @Override
    public int getSecond() {
        return this.second;
    }

    private BigDecimal getSeconds() {
        if (this.second == Integer.MIN_VALUE) {
            return DECIMAL_ZERO;
        }
        BigDecimal bigDecimal = BigDecimal.valueOf(this.second);
        if (this.fractionalSecond != null) {
            return bigDecimal.add(this.fractionalSecond);
        }
        return bigDecimal;
    }

    @Override
    public int getMillisecond() {
        if (this.fractionalSecond == null) {
            return Integer.MIN_VALUE;
        }
        return this.fractionalSecond.movePointRight(3).intValue();
    }

    @Override
    public BigDecimal getFractionalSecond() {
        return this.fractionalSecond;
    }

    @Override
    public void setYear(BigInteger bigInteger) {
        if (bigInteger == null) {
            this.eon = null;
            this.year = Integer.MIN_VALUE;
        } else {
            BigInteger bigInteger2 = bigInteger.remainder(BILLION_B);
            this.year = bigInteger2.intValue();
            this.setEon(bigInteger.subtract(bigInteger2));
        }
    }

    @Override
    public void setYear(int n) {
        if (n == Integer.MIN_VALUE) {
            this.year = Integer.MIN_VALUE;
            this.eon = null;
        } else if (Math.abs(n) < 1000000000) {
            this.year = n;
            this.eon = null;
        } else {
            BigInteger bigInteger = BigInteger.valueOf(n);
            BigInteger bigInteger2 = bigInteger.remainder(BILLION_B);
            this.year = bigInteger2.intValue();
            this.setEon(bigInteger.subtract(bigInteger2));
        }
    }

    private void setEon(BigInteger bigInteger) {
        this.eon = bigInteger != null && bigInteger.compareTo(BigInteger.ZERO) == 0 ? null : bigInteger;
    }

    @Override
    public void setMonth(int n) {
        this.checkFieldValueConstraint(1, n);
        this.month = n;
    }

    @Override
    public void setDay(int n) {
        this.checkFieldValueConstraint(2, n);
        this.day = n;
    }

    @Override
    public void setTimezone(int n) {
        this.checkFieldValueConstraint(7, n);
        this.timezone = n;
    }

    @Override
    public void setTime(int n, int n2, int n3) {
        this.setTime(n, n2, n3, null);
    }

    private void checkFieldValueConstraint(int n, int n2) throws IllegalArgumentException {
        if (n2 < MIN_FIELD_VALUE[n] && n2 != Integer.MIN_VALUE || n2 > MAX_FIELD_VALUE[n]) {
            throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "InvalidFieldValue", new Object[]{new Integer(n2), FIELD_NAME[n]}));
        }
    }

    @Override
    public void setHour(int n) {
        this.checkFieldValueConstraint(3, n);
        this.hour = n;
    }

    @Override
    public void setMinute(int n) {
        this.checkFieldValueConstraint(4, n);
        this.minute = n;
    }

    @Override
    public void setSecond(int n) {
        this.checkFieldValueConstraint(5, n);
        this.second = n;
    }

    @Override
    public void setTime(int n, int n2, int n3, BigDecimal bigDecimal) {
        this.setHour(n);
        this.setMinute(n2);
        this.setSecond(n3);
        this.setFractionalSecond(bigDecimal);
    }

    @Override
    public void setTime(int n, int n2, int n3, int n4) {
        this.setHour(n);
        this.setMinute(n2);
        this.setSecond(n3);
        this.setMillisecond(n4);
    }

    @Override
    public int compare(XMLGregorianCalendar xMLGregorianCalendar) {
        XMLGregorianCalendar xMLGregorianCalendar2;
        int n = 2;
        XMLGregorianCalendarImpl xMLGregorianCalendarImpl = this;
        XMLGregorianCalendar xMLGregorianCalendar3 = xMLGregorianCalendar;
        if (((XMLGregorianCalendar)xMLGregorianCalendarImpl).getTimezone() == xMLGregorianCalendar3.getTimezone()) {
            return XMLGregorianCalendarImpl.internalCompare(xMLGregorianCalendarImpl, xMLGregorianCalendar3);
        }
        if (((XMLGregorianCalendar)xMLGregorianCalendarImpl).getTimezone() != Integer.MIN_VALUE && xMLGregorianCalendar3.getTimezone() != Integer.MIN_VALUE) {
            xMLGregorianCalendarImpl = (XMLGregorianCalendarImpl)((XMLGregorianCalendar)xMLGregorianCalendarImpl).normalize();
            xMLGregorianCalendar3 = (XMLGregorianCalendarImpl)xMLGregorianCalendar3.normalize();
            return XMLGregorianCalendarImpl.internalCompare(xMLGregorianCalendarImpl, xMLGregorianCalendar3);
        }
        if (((XMLGregorianCalendar)xMLGregorianCalendarImpl).getTimezone() != Integer.MIN_VALUE) {
            XMLGregorianCalendar xMLGregorianCalendar4;
            if (((XMLGregorianCalendar)xMLGregorianCalendarImpl).getTimezone() != 0) {
                xMLGregorianCalendarImpl = (XMLGregorianCalendarImpl)((XMLGregorianCalendar)xMLGregorianCalendarImpl).normalize();
            }
            if ((n = XMLGregorianCalendarImpl.internalCompare(xMLGregorianCalendarImpl, xMLGregorianCalendar4 = this.normalizeToTimezone(xMLGregorianCalendar3, 840))) == -1) {
                return n;
            }
            XMLGregorianCalendar xMLGregorianCalendar5 = this.normalizeToTimezone(xMLGregorianCalendar3, -840);
            n = XMLGregorianCalendarImpl.internalCompare(xMLGregorianCalendarImpl, xMLGregorianCalendar5);
            if (n == 1) {
                return n;
            }
            return 2;
        }
        if (xMLGregorianCalendar3.getTimezone() != 0) {
            xMLGregorianCalendar3 = (XMLGregorianCalendarImpl)this.normalizeToTimezone(xMLGregorianCalendar3, xMLGregorianCalendar3.getTimezone());
        }
        if ((n = XMLGregorianCalendarImpl.internalCompare(xMLGregorianCalendar2 = this.normalizeToTimezone(xMLGregorianCalendarImpl, -840), xMLGregorianCalendar3)) == -1) {
            return n;
        }
        XMLGregorianCalendar xMLGregorianCalendar6 = this.normalizeToTimezone(xMLGregorianCalendarImpl, 840);
        n = XMLGregorianCalendarImpl.internalCompare(xMLGregorianCalendar6, xMLGregorianCalendar3);
        if (n == 1) {
            return n;
        }
        return 2;
    }

    @Override
    public XMLGregorianCalendar normalize() {
        XMLGregorianCalendar xMLGregorianCalendar = this.normalizeToTimezone(this, this.timezone);
        if (this.getTimezone() == Integer.MIN_VALUE) {
            xMLGregorianCalendar.setTimezone(Integer.MIN_VALUE);
        }
        if (this.getMillisecond() == Integer.MIN_VALUE) {
            xMLGregorianCalendar.setMillisecond(Integer.MIN_VALUE);
        }
        return xMLGregorianCalendar;
    }

    private XMLGregorianCalendar normalizeToTimezone(XMLGregorianCalendar xMLGregorianCalendar, int n) {
        int n2 = n;
        XMLGregorianCalendar xMLGregorianCalendar2 = (XMLGregorianCalendar)xMLGregorianCalendar.clone();
        DurationImpl durationImpl = new DurationImpl((n2 = -n2) >= 0, 0, 0, 0, 0, n2 < 0 ? -n2 : n2, 0);
        xMLGregorianCalendar2.add(durationImpl);
        xMLGregorianCalendar2.setTimezone(0);
        return xMLGregorianCalendar2;
    }

    private static int internalCompare(XMLGregorianCalendar xMLGregorianCalendar, XMLGregorianCalendar xMLGregorianCalendar2) {
        int n;
        if (xMLGregorianCalendar.getEon() == xMLGregorianCalendar2.getEon() ? (n = XMLGregorianCalendarImpl.compareField(xMLGregorianCalendar.getYear(), xMLGregorianCalendar2.getYear())) != 0 : (n = XMLGregorianCalendarImpl.compareField(xMLGregorianCalendar.getEonAndYear(), xMLGregorianCalendar2.getEonAndYear())) != 0) {
            return n;
        }
        n = XMLGregorianCalendarImpl.compareField(xMLGregorianCalendar.getMonth(), xMLGregorianCalendar2.getMonth());
        if (n != 0) {
            return n;
        }
        n = XMLGregorianCalendarImpl.compareField(xMLGregorianCalendar.getDay(), xMLGregorianCalendar2.getDay());
        if (n != 0) {
            return n;
        }
        n = XMLGregorianCalendarImpl.compareField(xMLGregorianCalendar.getHour(), xMLGregorianCalendar2.getHour());
        if (n != 0) {
            return n;
        }
        n = XMLGregorianCalendarImpl.compareField(xMLGregorianCalendar.getMinute(), xMLGregorianCalendar2.getMinute());
        if (n != 0) {
            return n;
        }
        n = XMLGregorianCalendarImpl.compareField(xMLGregorianCalendar.getSecond(), xMLGregorianCalendar2.getSecond());
        if (n != 0) {
            return n;
        }
        n = XMLGregorianCalendarImpl.compareField(xMLGregorianCalendar.getFractionalSecond(), xMLGregorianCalendar2.getFractionalSecond());
        return n;
    }

    private static int compareField(int n, int n2) {
        if (n == n2) {
            return 0;
        }
        if (n == Integer.MIN_VALUE || n2 == Integer.MIN_VALUE) {
            return 2;
        }
        return n < n2 ? -1 : 1;
    }

    private static int compareField(BigInteger bigInteger, BigInteger bigInteger2) {
        if (bigInteger == null) {
            return bigInteger2 == null ? 0 : 2;
        }
        if (bigInteger2 == null) {
            return 2;
        }
        return bigInteger.compareTo(bigInteger2);
    }

    private static int compareField(BigDecimal bigDecimal, BigDecimal bigDecimal2) {
        if (bigDecimal == bigDecimal2) {
            return 0;
        }
        if (bigDecimal == null) {
            bigDecimal = DECIMAL_ZERO;
        }
        if (bigDecimal2 == null) {
            bigDecimal2 = DECIMAL_ZERO;
        }
        return bigDecimal.compareTo(bigDecimal2);
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof XMLGregorianCalendar) {
            return this.compare((XMLGregorianCalendar)object) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int n = this.getTimezone();
        if (n == Integer.MIN_VALUE) {
            n = 0;
        }
        XMLGregorianCalendar xMLGregorianCalendar = this;
        if (n != 0) {
            xMLGregorianCalendar = this.normalizeToTimezone(this, this.getTimezone());
        }
        return xMLGregorianCalendar.getYear() + xMLGregorianCalendar.getMonth() + xMLGregorianCalendar.getDay() + xMLGregorianCalendar.getHour() + xMLGregorianCalendar.getMinute() + xMLGregorianCalendar.getSecond();
    }

    public static XMLGregorianCalendar parse(String string) {
        return new XMLGregorianCalendarImpl(string);
    }

    @Override
    public String toXMLFormat() {
        QName qName = this.getXMLSchemaType();
        String string = null;
        if (qName == DatatypeConstants.DATETIME) {
            string = "%Y-%M-%DT%h:%m:%s%z";
        } else if (qName == DatatypeConstants.DATE) {
            string = "%Y-%M-%D%z";
        } else if (qName == DatatypeConstants.TIME) {
            string = "%h:%m:%s%z";
        } else if (qName == DatatypeConstants.GMONTH) {
            string = "--%M--%z";
        } else if (qName == DatatypeConstants.GDAY) {
            string = "---%D%z";
        } else if (qName == DatatypeConstants.GYEAR) {
            string = "%Y%z";
        } else if (qName == DatatypeConstants.GYEARMONTH) {
            string = "%Y-%M%z";
        } else if (qName == DatatypeConstants.GMONTHDAY) {
            string = "--%M-%D%z";
        }
        return this.format(string);
    }

    @Override
    public QName getXMLSchemaType() {
        if (this.year != Integer.MIN_VALUE && this.month != Integer.MIN_VALUE && this.day != Integer.MIN_VALUE && this.hour != Integer.MIN_VALUE && this.minute != Integer.MIN_VALUE && this.second != Integer.MIN_VALUE) {
            return DatatypeConstants.DATETIME;
        }
        if (this.year != Integer.MIN_VALUE && this.month != Integer.MIN_VALUE && this.day != Integer.MIN_VALUE && this.hour == Integer.MIN_VALUE && this.minute == Integer.MIN_VALUE && this.second == Integer.MIN_VALUE) {
            return DatatypeConstants.DATE;
        }
        if (this.year == Integer.MIN_VALUE && this.month == Integer.MIN_VALUE && this.day == Integer.MIN_VALUE && this.hour != Integer.MIN_VALUE && this.minute != Integer.MIN_VALUE && this.second != Integer.MIN_VALUE) {
            return DatatypeConstants.TIME;
        }
        if (this.year != Integer.MIN_VALUE && this.month != Integer.MIN_VALUE && this.day == Integer.MIN_VALUE && this.hour == Integer.MIN_VALUE && this.minute == Integer.MIN_VALUE && this.second == Integer.MIN_VALUE) {
            return DatatypeConstants.GYEARMONTH;
        }
        if (this.year == Integer.MIN_VALUE && this.month != Integer.MIN_VALUE && this.day != Integer.MIN_VALUE && this.hour == Integer.MIN_VALUE && this.minute == Integer.MIN_VALUE && this.second == Integer.MIN_VALUE) {
            return DatatypeConstants.GMONTHDAY;
        }
        if (this.year != Integer.MIN_VALUE && this.month == Integer.MIN_VALUE && this.day == Integer.MIN_VALUE && this.hour == Integer.MIN_VALUE && this.minute == Integer.MIN_VALUE && this.second == Integer.MIN_VALUE) {
            return DatatypeConstants.GYEAR;
        }
        if (this.year == Integer.MIN_VALUE && this.month != Integer.MIN_VALUE && this.day == Integer.MIN_VALUE && this.hour == Integer.MIN_VALUE && this.minute == Integer.MIN_VALUE && this.second == Integer.MIN_VALUE) {
            return DatatypeConstants.GMONTH;
        }
        if (this.year == Integer.MIN_VALUE && this.month == Integer.MIN_VALUE && this.day != Integer.MIN_VALUE && this.hour == Integer.MIN_VALUE && this.minute == Integer.MIN_VALUE && this.second == Integer.MIN_VALUE) {
            return DatatypeConstants.GDAY;
        }
        throw new IllegalStateException(this.getClass().getName() + "#getXMLSchemaType() :" + DatatypeMessageFormatter.formatMessage(null, "InvalidXGCFields", null));
    }

    @Override
    public boolean isValid() {
        if (this.month != Integer.MIN_VALUE && this.day != Integer.MIN_VALUE && (this.year != Integer.MIN_VALUE ? (this.eon == null ? this.day > XMLGregorianCalendarImpl.maximumDayInMonthFor(this.year, this.month) : this.day > XMLGregorianCalendarImpl.maximumDayInMonthFor(this.getEonAndYear(), this.month)) : this.day > XMLGregorianCalendarImpl.maximumDayInMonthFor(2000, this.month))) {
            return false;
        }
        if (this.hour == 24 && (this.minute != 0 || this.second != 0 || this.fractionalSecond != null && this.fractionalSecond.compareTo(DECIMAL_ZERO) != 0)) {
            return false;
        }
        return this.eon != null || this.year != 0;
    }

    @Override
    public void add(Duration duration) {
        int n;
        BigDecimal bigDecimal;
        boolean[] blArray = new boolean[]{false, false, false, false, false, false};
        int n2 = duration.getSign();
        int n3 = this.getMonth();
        if (n3 == Integer.MIN_VALUE) {
            n3 = MIN_FIELD_VALUE[1];
            blArray[1] = true;
        }
        BigInteger bigInteger = XMLGregorianCalendarImpl.sanitize(duration.getField(DatatypeConstants.MONTHS), n2);
        BigInteger bigInteger2 = BigInteger.valueOf(n3).add(bigInteger);
        this.setMonth(bigInteger2.subtract(BigInteger.ONE).mod(TWELVE).intValue() + 1);
        BigInteger bigInteger3 = new BigDecimal(bigInteger2.subtract(BigInteger.ONE)).divide(new BigDecimal(TWELVE), 3).toBigInteger();
        BigInteger bigInteger4 = this.getEonAndYear();
        if (bigInteger4 == null) {
            blArray[0] = true;
            bigInteger4 = BigInteger.ZERO;
        }
        BigInteger bigInteger5 = XMLGregorianCalendarImpl.sanitize(duration.getField(DatatypeConstants.YEARS), n2);
        BigInteger bigInteger6 = bigInteger4.add(bigInteger5).add(bigInteger3);
        this.setYear(bigInteger6);
        if (this.getSecond() == Integer.MIN_VALUE) {
            blArray[5] = true;
            bigDecimal = DECIMAL_ZERO;
        } else {
            bigDecimal = this.getSeconds();
        }
        BigDecimal bigDecimal2 = DurationImpl.sanitize((BigDecimal)duration.getField(DatatypeConstants.SECONDS), n2);
        BigDecimal bigDecimal3 = bigDecimal.add(bigDecimal2);
        BigDecimal bigDecimal4 = new BigDecimal(new BigDecimal(bigDecimal3.toBigInteger()).divide(DECIMAL_SIXTY, 3).toBigInteger());
        BigDecimal bigDecimal5 = bigDecimal3.subtract(bigDecimal4.multiply(DECIMAL_SIXTY));
        bigInteger3 = bigDecimal4.toBigInteger();
        this.setSecond(bigDecimal5.intValue());
        BigDecimal bigDecimal6 = bigDecimal5.subtract(new BigDecimal(BigInteger.valueOf(this.getSecond())));
        if (bigDecimal6.compareTo(DECIMAL_ZERO) < 0) {
            this.setFractionalSecond(DECIMAL_ONE.add(bigDecimal6));
            if (this.getSecond() == 0) {
                this.setSecond(59);
                bigInteger3 = bigInteger3.subtract(BigInteger.ONE);
            } else {
                this.setSecond(this.getSecond() - 1);
            }
        } else {
            this.setFractionalSecond(bigDecimal6);
        }
        int n4 = this.getMinute();
        if (n4 == Integer.MIN_VALUE) {
            blArray[4] = true;
            n4 = MIN_FIELD_VALUE[4];
        }
        BigInteger bigInteger7 = XMLGregorianCalendarImpl.sanitize(duration.getField(DatatypeConstants.MINUTES), n2);
        bigInteger2 = BigInteger.valueOf(n4).add(bigInteger7).add(bigInteger3);
        this.setMinute(bigInteger2.mod(SIXTY).intValue());
        bigInteger3 = new BigDecimal(bigInteger2).divide(DECIMAL_SIXTY, 3).toBigInteger();
        int n5 = this.getHour();
        if (n5 == Integer.MIN_VALUE) {
            blArray[3] = true;
            n5 = MIN_FIELD_VALUE[3];
        }
        BigInteger bigInteger8 = XMLGregorianCalendarImpl.sanitize(duration.getField(DatatypeConstants.HOURS), n2);
        bigInteger2 = BigInteger.valueOf(n5).add(bigInteger8).add(bigInteger3);
        this.setHour(bigInteger2.mod(TWENTY_FOUR).intValue());
        bigInteger3 = new BigDecimal(bigInteger2).divide(new BigDecimal(TWENTY_FOUR), 3).toBigInteger();
        int n6 = this.getDay();
        if (n6 == Integer.MIN_VALUE) {
            blArray[2] = true;
            n6 = MIN_FIELD_VALUE[2];
        }
        BigInteger bigInteger9 = XMLGregorianCalendarImpl.sanitize(duration.getField(DatatypeConstants.DAYS), n2);
        int n7 = XMLGregorianCalendarImpl.maximumDayInMonthFor(this.getEonAndYear(), this.getMonth());
        BigInteger bigInteger10 = n6 > n7 ? BigInteger.valueOf(n7) : (n6 < 1 ? BigInteger.ONE : BigInteger.valueOf(n6));
        BigInteger bigInteger11 = bigInteger10.add(bigInteger9).add(bigInteger3);
        while (true) {
            int n8;
            int n9;
            if (bigInteger11.compareTo(BigInteger.ONE) < 0) {
                BigInteger bigInteger12 = null;
                bigInteger12 = this.month >= 2 ? BigInteger.valueOf(XMLGregorianCalendarImpl.maximumDayInMonthFor(this.getEonAndYear(), this.getMonth() - 1)) : BigInteger.valueOf(XMLGregorianCalendarImpl.maximumDayInMonthFor(this.getEonAndYear().subtract(BigInteger.valueOf(1L)), 12));
                bigInteger11 = bigInteger11.add(bigInteger12);
                n9 = -1;
            } else {
                if (bigInteger11.compareTo(BigInteger.valueOf(XMLGregorianCalendarImpl.maximumDayInMonthFor(this.getEonAndYear(), this.getMonth()))) <= 0) break;
                bigInteger11 = bigInteger11.add(BigInteger.valueOf(-XMLGregorianCalendarImpl.maximumDayInMonthFor(this.getEonAndYear(), this.getMonth())));
                n9 = 1;
            }
            int n10 = this.getMonth() + n9;
            n = (n10 - 1) % 12;
            if (n < 0) {
                n = 12 + n + 1;
                n8 = BigDecimal.valueOf(n10 - 1).divide(new BigDecimal(TWELVE), 0).intValue();
            } else {
                n8 = (n10 - 1) / 12;
                ++n;
            }
            this.setMonth(n);
            if (n8 == 0) continue;
            this.setYear(this.getEonAndYear().add(BigInteger.valueOf(n8)));
        }
        this.setDay(bigInteger11.intValue());
        block9: for (n = 0; n <= 5; ++n) {
            if (!blArray[n]) continue;
            switch (n) {
                case 0: {
                    this.setYear(Integer.MIN_VALUE);
                    continue block9;
                }
                case 1: {
                    this.setMonth(Integer.MIN_VALUE);
                    continue block9;
                }
                case 2: {
                    this.setDay(Integer.MIN_VALUE);
                    continue block9;
                }
                case 3: {
                    this.setHour(Integer.MIN_VALUE);
                    continue block9;
                }
                case 4: {
                    this.setMinute(Integer.MIN_VALUE);
                    continue block9;
                }
                case 5: {
                    this.setSecond(Integer.MIN_VALUE);
                    this.setFractionalSecond(null);
                }
            }
        }
    }

    private static int maximumDayInMonthFor(BigInteger bigInteger, int n) {
        if (n != 2) {
            return DaysInMonth.table[n];
        }
        if (bigInteger.mod(FOUR_HUNDRED).equals(BigInteger.ZERO) || !bigInteger.mod(HUNDRED).equals(BigInteger.ZERO) && bigInteger.mod(FOUR).equals(BigInteger.ZERO)) {
            return 29;
        }
        return DaysInMonth.table[n];
    }

    private static int maximumDayInMonthFor(int n, int n2) {
        if (n2 != 2) {
            return DaysInMonth.table[n2];
        }
        if (n % 400 == 0 || n % 100 != 0 && n % 4 == 0) {
            return 29;
        }
        return DaysInMonth.table[2];
    }

    @Override
    public GregorianCalendar toGregorianCalendar() {
        GregorianCalendar gregorianCalendar = null;
        TimeZone timeZone = this.getTimeZone(Integer.MIN_VALUE);
        Locale locale = Locale.getDefault();
        gregorianCalendar = new GregorianCalendar(timeZone, locale);
        gregorianCalendar.clear();
        gregorianCalendar.setGregorianChange(PURE_GREGORIAN_CHANGE);
        if (this.year != Integer.MIN_VALUE) {
            if (this.eon == null) {
                gregorianCalendar.set(0, this.year < 0 ? 0 : 1);
                gregorianCalendar.set(1, Math.abs(this.year));
            } else {
                BigInteger bigInteger = this.getEonAndYear();
                gregorianCalendar.set(0, bigInteger.signum() == -1 ? 0 : 1);
                gregorianCalendar.set(1, bigInteger.abs().intValue());
            }
        }
        if (this.month != Integer.MIN_VALUE) {
            gregorianCalendar.set(2, this.month - 1);
        }
        if (this.day != Integer.MIN_VALUE) {
            gregorianCalendar.set(5, this.day);
        }
        if (this.hour != Integer.MIN_VALUE) {
            gregorianCalendar.set(11, this.hour);
        }
        if (this.minute != Integer.MIN_VALUE) {
            gregorianCalendar.set(12, this.minute);
        }
        if (this.second != Integer.MIN_VALUE) {
            gregorianCalendar.set(13, this.second);
        }
        if (this.fractionalSecond != null) {
            gregorianCalendar.set(14, this.getMillisecond());
        }
        return gregorianCalendar;
    }

    @Override
    public GregorianCalendar toGregorianCalendar(TimeZone timeZone, Locale locale, XMLGregorianCalendar xMLGregorianCalendar) {
        int n;
        GregorianCalendar gregorianCalendar = null;
        TimeZone timeZone2 = timeZone;
        if (timeZone2 == null) {
            n = Integer.MIN_VALUE;
            if (xMLGregorianCalendar != null) {
                n = xMLGregorianCalendar.getTimezone();
            }
            timeZone2 = this.getTimeZone(n);
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        gregorianCalendar = new GregorianCalendar(timeZone2, locale);
        gregorianCalendar.clear();
        gregorianCalendar.setGregorianChange(PURE_GREGORIAN_CHANGE);
        if (this.year != Integer.MIN_VALUE) {
            if (this.eon == null) {
                gregorianCalendar.set(0, this.year < 0 ? 0 : 1);
                gregorianCalendar.set(1, Math.abs(this.year));
            } else {
                BigInteger bigInteger = this.getEonAndYear();
                gregorianCalendar.set(0, bigInteger.signum() == -1 ? 0 : 1);
                gregorianCalendar.set(1, bigInteger.abs().intValue());
            }
        } else if (xMLGregorianCalendar != null && (n = xMLGregorianCalendar.getYear()) != Integer.MIN_VALUE) {
            if (xMLGregorianCalendar.getEon() == null) {
                gregorianCalendar.set(0, n < 0 ? 0 : 1);
                gregorianCalendar.set(1, Math.abs(n));
            } else {
                BigInteger bigInteger = xMLGregorianCalendar.getEonAndYear();
                gregorianCalendar.set(0, bigInteger.signum() == -1 ? 0 : 1);
                gregorianCalendar.set(1, bigInteger.abs().intValue());
            }
        }
        if (this.month != Integer.MIN_VALUE) {
            gregorianCalendar.set(2, this.month - 1);
        } else {
            int n2;
            int n3 = n2 = xMLGregorianCalendar != null ? xMLGregorianCalendar.getMonth() : Integer.MIN_VALUE;
            if (n2 != Integer.MIN_VALUE) {
                gregorianCalendar.set(2, n2 - 1);
            }
        }
        if (this.day != Integer.MIN_VALUE) {
            gregorianCalendar.set(5, this.day);
        } else {
            int n4;
            int n5 = n4 = xMLGregorianCalendar != null ? xMLGregorianCalendar.getDay() : Integer.MIN_VALUE;
            if (n4 != Integer.MIN_VALUE) {
                gregorianCalendar.set(5, n4);
            }
        }
        if (this.hour != Integer.MIN_VALUE) {
            gregorianCalendar.set(11, this.hour);
        } else {
            int n6;
            int n7 = n6 = xMLGregorianCalendar != null ? xMLGregorianCalendar.getHour() : Integer.MIN_VALUE;
            if (n6 != Integer.MIN_VALUE) {
                gregorianCalendar.set(11, n6);
            }
        }
        if (this.minute != Integer.MIN_VALUE) {
            gregorianCalendar.set(12, this.minute);
        } else {
            int n8;
            int n9 = n8 = xMLGregorianCalendar != null ? xMLGregorianCalendar.getMinute() : Integer.MIN_VALUE;
            if (n8 != Integer.MIN_VALUE) {
                gregorianCalendar.set(12, n8);
            }
        }
        if (this.second != Integer.MIN_VALUE) {
            gregorianCalendar.set(13, this.second);
        } else {
            int n10;
            int n11 = n10 = xMLGregorianCalendar != null ? xMLGregorianCalendar.getSecond() : Integer.MIN_VALUE;
            if (n10 != Integer.MIN_VALUE) {
                gregorianCalendar.set(13, n10);
            }
        }
        if (this.fractionalSecond != null) {
            gregorianCalendar.set(14, this.getMillisecond());
        } else {
            BigDecimal bigDecimal;
            BigDecimal bigDecimal2 = bigDecimal = xMLGregorianCalendar != null ? xMLGregorianCalendar.getFractionalSecond() : null;
            if (bigDecimal != null) {
                gregorianCalendar.set(14, xMLGregorianCalendar.getMillisecond());
            }
        }
        return gregorianCalendar;
    }

    @Override
    public TimeZone getTimeZone(int n) {
        TimeZone timeZone = null;
        int n2 = this.getTimezone();
        if (n2 == Integer.MIN_VALUE) {
            n2 = n;
        }
        if (n2 == Integer.MIN_VALUE) {
            timeZone = TimeZone.getDefault();
        } else {
            char c;
            char c2 = c = n2 < 0 ? (char)'-' : '+';
            if (c == '-') {
                n2 = -n2;
            }
            int n3 = n2 / 60;
            int n4 = n2 - n3 * 60;
            StringBuffer stringBuffer = new StringBuffer(8);
            stringBuffer.append("GMT");
            stringBuffer.append(c);
            stringBuffer.append(n3);
            if (n4 != 0) {
                if (n4 < 10) {
                    stringBuffer.append('0');
                }
                stringBuffer.append(n4);
            }
            timeZone = TimeZone.getTimeZone(stringBuffer.toString());
        }
        return timeZone;
    }

    @Override
    public Object clone() {
        return new XMLGregorianCalendarImpl(this.getEonAndYear(), this.month, this.day, this.hour, this.minute, this.second, this.fractionalSecond, this.timezone);
    }

    @Override
    public void clear() {
        this.eon = null;
        this.year = Integer.MIN_VALUE;
        this.month = Integer.MIN_VALUE;
        this.day = Integer.MIN_VALUE;
        this.timezone = Integer.MIN_VALUE;
        this.hour = Integer.MIN_VALUE;
        this.minute = Integer.MIN_VALUE;
        this.second = Integer.MIN_VALUE;
        this.fractionalSecond = null;
    }

    @Override
    public void setMillisecond(int n) {
        if (n == Integer.MIN_VALUE) {
            this.fractionalSecond = null;
        } else {
            this.checkFieldValueConstraint(6, n);
            this.fractionalSecond = BigDecimal.valueOf(n, 3);
        }
    }

    @Override
    public void setFractionalSecond(BigDecimal bigDecimal) {
        if (bigDecimal != null && (bigDecimal.compareTo(DECIMAL_ZERO) < 0 || bigDecimal.compareTo(DECIMAL_ONE) > 0)) {
            throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "InvalidFractional", new Object[]{bigDecimal}));
        }
        this.fractionalSecond = bigDecimal;
    }

    private static boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    private String format(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        int n = 0;
        int n2 = string.length();
        block9: while (n < n2) {
            char c;
            if ((c = string.charAt(n++)) != '%') {
                stringBuffer.append(c);
                continue;
            }
            switch (string.charAt(n++)) {
                case 'Y': {
                    if (this.eon == null) {
                        int n3 = this.year;
                        if (n3 < 0) {
                            stringBuffer.append('-');
                            n3 = -this.year;
                        }
                        this.printNumber(stringBuffer, n3, 4);
                        continue block9;
                    }
                    this.printNumber(stringBuffer, this.getEonAndYear(), 4);
                    continue block9;
                }
                case 'M': {
                    this.printNumber(stringBuffer, this.getMonth(), 2);
                    continue block9;
                }
                case 'D': {
                    this.printNumber(stringBuffer, this.getDay(), 2);
                    continue block9;
                }
                case 'h': {
                    this.printNumber(stringBuffer, this.getHour(), 2);
                    continue block9;
                }
                case 'm': {
                    this.printNumber(stringBuffer, this.getMinute(), 2);
                    continue block9;
                }
                case 's': {
                    this.printNumber(stringBuffer, this.getSecond(), 2);
                    if (this.getFractionalSecond() == null) continue block9;
                    String string2 = this.toString(this.getFractionalSecond());
                    stringBuffer.append(string2.substring(1, string2.length()));
                    continue block9;
                }
                case 'z': {
                    int n4 = this.getTimezone();
                    if (n4 == 0) {
                        stringBuffer.append('Z');
                        continue block9;
                    }
                    if (n4 == Integer.MIN_VALUE) continue block9;
                    if (n4 < 0) {
                        stringBuffer.append('-');
                        n4 *= -1;
                    } else {
                        stringBuffer.append('+');
                    }
                    this.printNumber(stringBuffer, n4 / 60, 2);
                    stringBuffer.append(':');
                    this.printNumber(stringBuffer, n4 % 60, 2);
                    continue block9;
                }
            }
            throw new InternalError();
        }
        return stringBuffer.toString();
    }

    private void printNumber(StringBuffer stringBuffer, int n, int n2) {
        String string = String.valueOf(n);
        for (int i = string.length(); i < n2; ++i) {
            stringBuffer.append('0');
        }
        stringBuffer.append(string);
    }

    private void printNumber(StringBuffer stringBuffer, BigInteger bigInteger, int n) {
        String string = bigInteger.toString();
        for (int i = string.length(); i < n; ++i) {
            stringBuffer.append('0');
        }
        stringBuffer.append(string);
    }

    private String toString(BigDecimal bigDecimal) {
        StringBuffer stringBuffer;
        String string = bigDecimal.unscaledValue().toString();
        int n = bigDecimal.scale();
        if (n == 0) {
            return string;
        }
        int n2 = string.length() - n;
        if (n2 == 0) {
            return "0." + string;
        }
        if (n2 > 0) {
            stringBuffer = new StringBuffer(string);
            stringBuffer.insert(n2, '.');
        } else {
            stringBuffer = new StringBuffer(3 - n2 + string.length());
            stringBuffer.append("0.");
            for (int i = 0; i < -n2; ++i) {
                stringBuffer.append('0');
            }
            stringBuffer.append(string);
        }
        return stringBuffer.toString();
    }

    static BigInteger sanitize(Number number, int n) {
        if (n == 0 || number == null) {
            return BigInteger.ZERO;
        }
        return n < 0 ? ((BigInteger)number).negate() : (BigInteger)number;
    }

    @Override
    public void reset() {
        this.eon = this.orig_eon;
        this.year = this.orig_year;
        this.month = this.orig_month;
        this.day = this.orig_day;
        this.hour = this.orig_hour;
        this.minute = this.orig_minute;
        this.second = this.orig_second;
        this.fractionalSecond = this.orig_fracSeconds;
        this.timezone = this.orig_timezone;
    }

    private Object writeReplace() throws IOException {
        return new SerializedXMLGregorianCalendar(this.toXMLFormat());
    }

    private final class Parser {
        private final String format;
        private final String value;
        private final int flen;
        private final int vlen;
        private int fidx;
        private int vidx;

        private Parser(String string, String string2) {
            this.format = string;
            this.value = string2;
            this.flen = string.length();
            this.vlen = string2.length();
        }

        public void parse() throws IllegalArgumentException {
            block9: while (this.fidx < this.flen) {
                char c;
                if ((c = this.format.charAt(this.fidx++)) != '%') {
                    this.skip(c);
                    continue;
                }
                switch (this.format.charAt(this.fidx++)) {
                    case 'Y': {
                        this.parseYear();
                        continue block9;
                    }
                    case 'M': {
                        XMLGregorianCalendarImpl.this.setMonth(this.parseInt(2, 2));
                        continue block9;
                    }
                    case 'D': {
                        XMLGregorianCalendarImpl.this.setDay(this.parseInt(2, 2));
                        continue block9;
                    }
                    case 'h': {
                        XMLGregorianCalendarImpl.this.setHour(this.parseInt(2, 2));
                        continue block9;
                    }
                    case 'm': {
                        XMLGregorianCalendarImpl.this.setMinute(this.parseInt(2, 2));
                        continue block9;
                    }
                    case 's': {
                        XMLGregorianCalendarImpl.this.setSecond(this.parseInt(2, 2));
                        if (this.peek() != '.') continue block9;
                        XMLGregorianCalendarImpl.this.setFractionalSecond(this.parseBigDecimal());
                        continue block9;
                    }
                    case 'z': {
                        char c2 = this.peek();
                        if (c2 == 'Z') {
                            ++this.vidx;
                            XMLGregorianCalendarImpl.this.setTimezone(0);
                            continue block9;
                        }
                        if (c2 != '+' && c2 != '-') continue block9;
                        ++this.vidx;
                        int n = this.parseInt(2, 2);
                        this.skip(':');
                        int n2 = this.parseInt(2, 2);
                        XMLGregorianCalendarImpl.this.setTimezone((n * 60 + n2) * (c2 == '+' ? 1 : -1));
                        continue block9;
                    }
                }
                throw new InternalError();
            }
            if (this.vidx != this.vlen) {
                throw new IllegalArgumentException(this.value);
            }
        }

        private char peek() throws IllegalArgumentException {
            if (this.vidx == this.vlen) {
                return '\uffff';
            }
            return this.value.charAt(this.vidx);
        }

        private char read() throws IllegalArgumentException {
            if (this.vidx == this.vlen) {
                throw new IllegalArgumentException(this.value);
            }
            return this.value.charAt(this.vidx++);
        }

        private void skip(char c) throws IllegalArgumentException {
            if (this.read() != c) {
                throw new IllegalArgumentException(this.value);
            }
        }

        private void parseYear() throws IllegalArgumentException {
            int n = this.vidx++;
            int n2 = 0;
            if (this.peek() == '-') {
                n2 = 1;
            }
            while (XMLGregorianCalendarImpl.isDigit(this.peek())) {
                ++this.vidx;
            }
            int n3 = this.vidx - n - n2;
            if (n3 < 4) {
                throw new IllegalArgumentException(this.value);
            }
            String string = this.value.substring(n, this.vidx);
            if (n3 < 10) {
                XMLGregorianCalendarImpl.this.setYear(Integer.parseInt(string));
            } else {
                XMLGregorianCalendarImpl.this.setYear(new BigInteger(string));
            }
        }

        private int parseInt(int n, int n2) throws IllegalArgumentException {
            int n3 = this.vidx;
            while (XMLGregorianCalendarImpl.isDigit(this.peek()) && this.vidx - n3 < n2) {
                ++this.vidx;
            }
            if (this.vidx - n3 < n) {
                throw new IllegalArgumentException(this.value);
            }
            return Integer.parseInt(this.value.substring(n3, this.vidx));
        }

        private BigDecimal parseBigDecimal() throws IllegalArgumentException {
            int n = this.vidx++;
            if (this.peek() != '.') {
                throw new IllegalArgumentException(this.value);
            }
            while (XMLGregorianCalendarImpl.isDigit(this.peek())) {
                ++this.vidx;
            }
            return new BigDecimal(this.value.substring(n, this.vidx));
        }
    }

    private static class DaysInMonth {
        private static final int[] table = new int[]{0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        private DaysInMonth() {
        }
    }
}

