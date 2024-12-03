/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import java.math.BigDecimal;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.xerces.impl.dv.xs.DurationDV;
import org.apache.xerces.impl.dv.xs.TypeValidator;
import org.apache.xerces.jaxp.datatype.DatatypeFactoryImpl;
import org.apache.xerces.xs.datatypes.XSDateTime;

public abstract class AbstractDateTimeDV
extends TypeValidator {
    private static final boolean DEBUG = false;
    protected static final int YEAR = 2000;
    protected static final int MONTH = 1;
    protected static final int DAY = 1;
    protected static final DatatypeFactory datatypeFactory = new DatatypeFactoryImpl();

    @Override
    public short getAllowedFacets() {
        return 2552;
    }

    @Override
    public boolean isIdentical(Object object, Object object2) {
        if (!(object instanceof DateTimeData) || !(object2 instanceof DateTimeData)) {
            return false;
        }
        DateTimeData dateTimeData = (DateTimeData)object;
        DateTimeData dateTimeData2 = (DateTimeData)object2;
        if (dateTimeData.timezoneHr == dateTimeData2.timezoneHr && dateTimeData.timezoneMin == dateTimeData2.timezoneMin) {
            return dateTimeData.equals(dateTimeData2);
        }
        return false;
    }

    @Override
    public int compare(Object object, Object object2) {
        return this.compareDates((DateTimeData)object, (DateTimeData)object2, true);
    }

    protected short compareDates(DateTimeData dateTimeData, DateTimeData dateTimeData2, boolean bl) {
        if (dateTimeData.utc == dateTimeData2.utc) {
            return this.compareOrder(dateTimeData, dateTimeData2);
        }
        DateTimeData dateTimeData3 = new DateTimeData(null, this);
        if (dateTimeData.utc == 90) {
            this.cloneDate(dateTimeData2, dateTimeData3);
            dateTimeData3.timezoneHr = 14;
            dateTimeData3.timezoneMin = 0;
            dateTimeData3.utc = 43;
            this.normalize(dateTimeData3);
            short s = this.compareOrder(dateTimeData, dateTimeData3);
            if (s == -1) {
                return s;
            }
            this.cloneDate(dateTimeData2, dateTimeData3);
            dateTimeData3.timezoneHr = -14;
            dateTimeData3.timezoneMin = 0;
            dateTimeData3.utc = 45;
            this.normalize(dateTimeData3);
            short s2 = this.compareOrder(dateTimeData, dateTimeData3);
            if (s2 == 1) {
                return s2;
            }
            return 2;
        }
        if (dateTimeData2.utc == 90) {
            this.cloneDate(dateTimeData, dateTimeData3);
            dateTimeData3.timezoneHr = -14;
            dateTimeData3.timezoneMin = 0;
            dateTimeData3.utc = 45;
            this.normalize(dateTimeData3);
            short s = this.compareOrder(dateTimeData3, dateTimeData2);
            if (s == -1) {
                return s;
            }
            this.cloneDate(dateTimeData, dateTimeData3);
            dateTimeData3.timezoneHr = 14;
            dateTimeData3.timezoneMin = 0;
            dateTimeData3.utc = 43;
            this.normalize(dateTimeData3);
            short s3 = this.compareOrder(dateTimeData3, dateTimeData2);
            if (s3 == 1) {
                return s3;
            }
            return 2;
        }
        return 2;
    }

    protected short compareOrder(DateTimeData dateTimeData, DateTimeData dateTimeData2) {
        if (dateTimeData.position < 1) {
            if (dateTimeData.year < dateTimeData2.year) {
                return -1;
            }
            if (dateTimeData.year > dateTimeData2.year) {
                return 1;
            }
        }
        if (dateTimeData.position < 2) {
            if (dateTimeData.month < dateTimeData2.month) {
                return -1;
            }
            if (dateTimeData.month > dateTimeData2.month) {
                return 1;
            }
        }
        if (dateTimeData.day < dateTimeData2.day) {
            return -1;
        }
        if (dateTimeData.day > dateTimeData2.day) {
            return 1;
        }
        if (dateTimeData.hour < dateTimeData2.hour) {
            return -1;
        }
        if (dateTimeData.hour > dateTimeData2.hour) {
            return 1;
        }
        if (dateTimeData.minute < dateTimeData2.minute) {
            return -1;
        }
        if (dateTimeData.minute > dateTimeData2.minute) {
            return 1;
        }
        if (dateTimeData.second < dateTimeData2.second) {
            return -1;
        }
        if (dateTimeData.second > dateTimeData2.second) {
            return 1;
        }
        if (dateTimeData.utc < dateTimeData2.utc) {
            return -1;
        }
        if (dateTimeData.utc > dateTimeData2.utc) {
            return 1;
        }
        return 0;
    }

    protected void getTime(String string, int n, int n2, DateTimeData dateTimeData) throws RuntimeException {
        int n3 = n + 2;
        dateTimeData.hour = this.parseInt(string, n, n3);
        if (string.charAt(n3++) != ':') {
            throw new RuntimeException("Error in parsing time zone");
        }
        n = n3;
        dateTimeData.minute = this.parseInt(string, n, n3 += 2);
        if (string.charAt(n3++) != ':') {
            throw new RuntimeException("Error in parsing time zone");
        }
        int n4 = this.findUTCSign(string, n, n2);
        n = n3;
        n3 = n4 < 0 ? n2 : n4;
        dateTimeData.second = this.parseSecond(string, n, n3);
        if (n4 > 0) {
            this.getTimeZone(string, dateTimeData, n4, n2);
        }
    }

    protected int getDate(String string, int n, int n2, DateTimeData dateTimeData) throws RuntimeException {
        n = this.getYearMonth(string, n, n2, dateTimeData);
        if (string.charAt(n++) != '-') {
            throw new RuntimeException("CCYY-MM must be followed by '-' sign");
        }
        int n3 = n + 2;
        dateTimeData.day = this.parseInt(string, n, n3);
        return n3;
    }

    protected int getYearMonth(String string, int n, int n2, DateTimeData dateTimeData) throws RuntimeException {
        int n3;
        if (string.charAt(0) == '-') {
            ++n;
        }
        if ((n3 = this.indexOf(string, n, n2, '-')) == -1) {
            throw new RuntimeException("Year separator is missing or misplaced");
        }
        int n4 = n3 - n;
        if (n4 < 4) {
            throw new RuntimeException("Year must have 'CCYY' format");
        }
        if (n4 > 4 && string.charAt(n) == '0') {
            throw new RuntimeException("Leading zeros are required if the year value would otherwise have fewer than four digits; otherwise they are forbidden");
        }
        dateTimeData.year = this.parseIntYear(string, n3);
        if (string.charAt(n3) != '-') {
            throw new RuntimeException("CCYY must be followed by '-' sign");
        }
        n = ++n3;
        n3 = n + 2;
        dateTimeData.month = this.parseInt(string, n, n3);
        return n3;
    }

    protected void parseTimeZone(String string, int n, int n2, DateTimeData dateTimeData) throws RuntimeException {
        if (n < n2) {
            if (!this.isNextCharUTCSign(string, n, n2)) {
                throw new RuntimeException("Error in month parsing");
            }
            this.getTimeZone(string, dateTimeData, n, n2);
        }
    }

    protected void getTimeZone(String string, DateTimeData dateTimeData, int n, int n2) throws RuntimeException {
        dateTimeData.utc = string.charAt(n);
        if (string.charAt(n) == 'Z') {
            if (n2 > ++n) {
                throw new RuntimeException("Error in parsing time zone");
            }
            return;
        }
        if (n <= n2 - 6) {
            int n3 = string.charAt(n) == '-' ? -1 : 1;
            int n4 = ++n + 2;
            dateTimeData.timezoneHr = n3 * this.parseInt(string, n, n4);
            if (string.charAt(n4++) != ':') {
                throw new RuntimeException("Error in parsing time zone");
            }
            dateTimeData.timezoneMin = n3 * this.parseInt(string, n4, n4 + 2);
            if (n4 + 2 != n2) {
                throw new RuntimeException("Error in parsing time zone");
            }
            if (dateTimeData.timezoneHr != 0 || dateTimeData.timezoneMin != 0) {
                dateTimeData.normalized = false;
            }
        } else {
            throw new RuntimeException("Error in parsing time zone");
        }
    }

    protected int indexOf(String string, int n, int n2, char c) {
        for (int i = n; i < n2; ++i) {
            if (string.charAt(i) != c) continue;
            return i;
        }
        return -1;
    }

    protected void validateDateTime(DateTimeData dateTimeData) {
        if (dateTimeData.year == 0) {
            throw new RuntimeException("The year \"0000\" is an illegal year value");
        }
        if (dateTimeData.month < 1 || dateTimeData.month > 12) {
            throw new RuntimeException("The month must have values 1 to 12");
        }
        if (dateTimeData.day > this.maxDayInMonthFor(dateTimeData.year, dateTimeData.month) || dateTimeData.day < 1) {
            throw new RuntimeException("The day must have values 1 to 31");
        }
        if (dateTimeData.hour > 23 || dateTimeData.hour < 0) {
            if (dateTimeData.hour == 24 && dateTimeData.minute == 0 && dateTimeData.second == 0.0) {
                dateTimeData.hour = 0;
                if (++dateTimeData.day > this.maxDayInMonthFor(dateTimeData.year, dateTimeData.month)) {
                    dateTimeData.day = 1;
                    if (++dateTimeData.month > 12) {
                        dateTimeData.month = 1;
                        if (++dateTimeData.year == 0) {
                            dateTimeData.year = 1;
                        }
                    }
                }
            } else {
                throw new RuntimeException("Hour must have values 0-23, unless 24:00:00");
            }
        }
        if (dateTimeData.minute > 59 || dateTimeData.minute < 0) {
            throw new RuntimeException("Minute must have values 0-59");
        }
        if (dateTimeData.second >= 60.0 || dateTimeData.second < 0.0) {
            throw new RuntimeException("Second must have values 0-59");
        }
        if (dateTimeData.timezoneHr > 14 || dateTimeData.timezoneHr < -14) {
            throw new RuntimeException("Time zone should have range -14:00 to +14:00");
        }
        if ((dateTimeData.timezoneHr == 14 || dateTimeData.timezoneHr == -14) && dateTimeData.timezoneMin != 0) {
            throw new RuntimeException("Time zone should have range -14:00 to +14:00");
        }
        if (dateTimeData.timezoneMin > 59 || dateTimeData.timezoneMin < -59) {
            throw new RuntimeException("Minute must have values 0-59");
        }
    }

    protected int findUTCSign(String string, int n, int n2) {
        for (int i = n; i < n2; ++i) {
            char c = string.charAt(i);
            if (c != 'Z' && c != '+' && c != '-') continue;
            return i;
        }
        return -1;
    }

    protected final boolean isNextCharUTCSign(String string, int n, int n2) {
        if (n < n2) {
            char c = string.charAt(n);
            return c == 'Z' || c == '+' || c == '-';
        }
        return false;
    }

    protected int parseInt(String string, int n, int n2) throws NumberFormatException {
        int n3 = 10;
        int n4 = 0;
        int n5 = 0;
        int n6 = -2147483647;
        int n7 = n6 / n3;
        int n8 = n;
        do {
            if ((n5 = AbstractDateTimeDV.getDigit(string.charAt(n8))) < 0) {
                throw new NumberFormatException("'" + string + "' has wrong format");
            }
            if (n4 < n7) {
                throw new NumberFormatException("'" + string + "' has wrong format");
            }
            if ((n4 *= n3) < n6 + n5) {
                throw new NumberFormatException("'" + string + "' has wrong format");
            }
            n4 -= n5;
        } while (++n8 < n2);
        return -n4;
    }

    protected int parseIntYear(String string, int n) {
        int n2;
        int n3 = 10;
        int n4 = 0;
        boolean bl = false;
        int n5 = 0;
        int n6 = 0;
        if (string.charAt(0) == '-') {
            bl = true;
            n2 = Integer.MIN_VALUE;
            ++n5;
        } else {
            n2 = -2147483647;
        }
        int n7 = n2 / n3;
        while (n5 < n) {
            if ((n6 = AbstractDateTimeDV.getDigit(string.charAt(n5++))) < 0) {
                throw new NumberFormatException("'" + string + "' has wrong format");
            }
            if (n4 < n7) {
                throw new NumberFormatException("'" + string + "' has wrong format");
            }
            if ((n4 *= n3) < n2 + n6) {
                throw new NumberFormatException("'" + string + "' has wrong format");
            }
            n4 -= n6;
        }
        if (bl) {
            if (n5 > 1) {
                return n4;
            }
            throw new NumberFormatException("'" + string + "' has wrong format");
        }
        return -n4;
    }

    protected void normalize(DateTimeData dateTimeData) {
        int n = -1;
        int n2 = dateTimeData.minute + n * dateTimeData.timezoneMin;
        int n3 = this.fQuotient(n2, 60);
        dateTimeData.minute = this.mod(n2, 60, n3);
        n2 = dateTimeData.hour + n * dateTimeData.timezoneHr + n3;
        n3 = this.fQuotient(n2, 24);
        dateTimeData.hour = this.mod(n2, 24, n3);
        dateTimeData.day += n3;
        while (true) {
            n2 = this.maxDayInMonthFor(dateTimeData.year, dateTimeData.month);
            if (dateTimeData.day < 1) {
                dateTimeData.day += this.maxDayInMonthFor(dateTimeData.year, dateTimeData.month - 1);
                n3 = -1;
            } else {
                if (dateTimeData.day <= n2) break;
                dateTimeData.day -= n2;
                n3 = 1;
            }
            n2 = dateTimeData.month + n3;
            dateTimeData.month = this.modulo(n2, 1, 13);
            dateTimeData.year += this.fQuotient(n2, 1, 13);
            if (dateTimeData.year != 0) continue;
            dateTimeData.year = dateTimeData.timezoneHr < 0 || dateTimeData.timezoneMin < 0 ? 1 : -1;
        }
        dateTimeData.utc = 90;
    }

    protected void saveUnnormalized(DateTimeData dateTimeData) {
        dateTimeData.unNormYear = dateTimeData.year;
        dateTimeData.unNormMonth = dateTimeData.month;
        dateTimeData.unNormDay = dateTimeData.day;
        dateTimeData.unNormHour = dateTimeData.hour;
        dateTimeData.unNormMinute = dateTimeData.minute;
        dateTimeData.unNormSecond = dateTimeData.second;
    }

    protected void resetDateObj(DateTimeData dateTimeData) {
        dateTimeData.year = 0;
        dateTimeData.month = 0;
        dateTimeData.day = 0;
        dateTimeData.hour = 0;
        dateTimeData.minute = 0;
        dateTimeData.second = 0.0;
        dateTimeData.utc = 0;
        dateTimeData.timezoneHr = 0;
        dateTimeData.timezoneMin = 0;
    }

    protected int maxDayInMonthFor(int n, int n2) {
        if (n2 == 4 || n2 == 6 || n2 == 9 || n2 == 11) {
            return 30;
        }
        if (n2 == 2) {
            if (this.isLeapYear(n)) {
                return 29;
            }
            return 28;
        }
        return 31;
    }

    private boolean isLeapYear(int n) {
        return n % 4 == 0 && (n % 100 != 0 || n % 400 == 0);
    }

    protected int mod(int n, int n2, int n3) {
        return n - n3 * n2;
    }

    protected int fQuotient(int n, int n2) {
        return (int)Math.floor((float)n / (float)n2);
    }

    protected int modulo(int n, int n2, int n3) {
        int n4 = n - n2;
        int n5 = n3 - n2;
        return this.mod(n4, n5, this.fQuotient(n4, n5)) + n2;
    }

    protected int fQuotient(int n, int n2, int n3) {
        return this.fQuotient(n - n2, n3 - n2);
    }

    protected String dateToString(DateTimeData dateTimeData) {
        StringBuffer stringBuffer = new StringBuffer(25);
        this.append(stringBuffer, dateTimeData.year, 4);
        stringBuffer.append('-');
        this.append(stringBuffer, dateTimeData.month, 2);
        stringBuffer.append('-');
        this.append(stringBuffer, dateTimeData.day, 2);
        stringBuffer.append('T');
        this.append(stringBuffer, dateTimeData.hour, 2);
        stringBuffer.append(':');
        this.append(stringBuffer, dateTimeData.minute, 2);
        stringBuffer.append(':');
        this.append(stringBuffer, dateTimeData.second);
        this.append(stringBuffer, (char)dateTimeData.utc, 0);
        return stringBuffer.toString();
    }

    protected final void append(StringBuffer stringBuffer, int n, int n2) {
        if (n == Integer.MIN_VALUE) {
            stringBuffer.append(n);
            return;
        }
        if (n < 0) {
            stringBuffer.append('-');
            n = -n;
        }
        if (n2 == 4) {
            if (n < 10) {
                stringBuffer.append("000");
            } else if (n < 100) {
                stringBuffer.append("00");
            } else if (n < 1000) {
                stringBuffer.append('0');
            }
            stringBuffer.append(n);
        } else if (n2 == 2) {
            if (n < 10) {
                stringBuffer.append('0');
            }
            stringBuffer.append(n);
        } else if (n != 0) {
            stringBuffer.append((char)n);
        }
    }

    protected final void append(StringBuffer stringBuffer, double d) {
        if (d < 0.0) {
            stringBuffer.append('-');
            d = -d;
        }
        if (d < 10.0) {
            stringBuffer.append('0');
        }
        this.append2(stringBuffer, d);
    }

    protected final void append2(StringBuffer stringBuffer, double d) {
        int n = (int)d;
        if (d == (double)n) {
            stringBuffer.append(n);
        } else {
            this.append3(stringBuffer, d);
        }
    }

    private void append3(StringBuffer stringBuffer, double d) {
        String string = String.valueOf(d);
        int n = string.indexOf(69);
        if (n == -1) {
            stringBuffer.append(string);
            return;
        }
        if (d < 1.0) {
            int n2;
            int n3;
            int n4;
            try {
                n4 = this.parseInt(string, n + 2, string.length());
            }
            catch (Exception exception) {
                stringBuffer.append(string);
                return;
            }
            stringBuffer.append("0.");
            for (n3 = 1; n3 < n4; ++n3) {
                stringBuffer.append('0');
            }
            for (n3 = n - 1; n3 > 0 && (n2 = string.charAt(n3)) == 48; --n3) {
            }
            for (n2 = 0; n2 <= n3; ++n2) {
                char c = string.charAt(n2);
                if (c == '.') continue;
                stringBuffer.append(c);
            }
        } else {
            int n5;
            int n6;
            try {
                n6 = this.parseInt(string, n + 1, string.length());
            }
            catch (Exception exception) {
                stringBuffer.append(string);
                return;
            }
            int n7 = n6 + 2;
            for (n5 = 0; n5 < n; ++n5) {
                char c = string.charAt(n5);
                if (c == '.') continue;
                if (n5 == n7) {
                    stringBuffer.append('.');
                }
                stringBuffer.append(c);
            }
            for (n5 = n7 - n; n5 > 0; --n5) {
                stringBuffer.append('0');
            }
        }
    }

    protected double parseSecond(String string, int n, int n2) throws NumberFormatException {
        int n3 = -1;
        for (int i = n; i < n2; ++i) {
            char c = string.charAt(i);
            if (c == '.') {
                n3 = i;
                continue;
            }
            if (c <= '9' && c >= '0') continue;
            throw new NumberFormatException("'" + string + "' has wrong format");
        }
        if (n3 == -1 ? n + 2 != n2 : n + 2 != n3 || n3 + 1 == n2) {
            throw new NumberFormatException("'" + string + "' has wrong format");
        }
        return Double.parseDouble(string.substring(n, n2));
    }

    private void cloneDate(DateTimeData dateTimeData, DateTimeData dateTimeData2) {
        dateTimeData2.year = dateTimeData.year;
        dateTimeData2.month = dateTimeData.month;
        dateTimeData2.day = dateTimeData.day;
        dateTimeData2.hour = dateTimeData.hour;
        dateTimeData2.minute = dateTimeData.minute;
        dateTimeData2.second = dateTimeData.second;
        dateTimeData2.utc = dateTimeData.utc;
        dateTimeData2.timezoneHr = dateTimeData.timezoneHr;
        dateTimeData2.timezoneMin = dateTimeData.timezoneMin;
    }

    protected XMLGregorianCalendar getXMLGregorianCalendar(DateTimeData dateTimeData) {
        return null;
    }

    protected Duration getDuration(DateTimeData dateTimeData) {
        return null;
    }

    protected final BigDecimal getFractionalSecondsAsBigDecimal(DateTimeData dateTimeData) {
        StringBuffer stringBuffer = new StringBuffer();
        this.append3(stringBuffer, dateTimeData.unNormSecond);
        String string = stringBuffer.toString();
        int n = string.indexOf(46);
        if (n == -1) {
            return null;
        }
        BigDecimal bigDecimal = new BigDecimal(string = string.substring(n));
        if (bigDecimal.compareTo(BigDecimal.valueOf(0L)) == 0) {
            return null;
        }
        return bigDecimal;
    }

    static final class DateTimeData
    implements XSDateTime {
        int year;
        int month;
        int day;
        int hour;
        int minute;
        int utc;
        double second;
        int timezoneHr;
        int timezoneMin;
        private String originalValue;
        boolean normalized = true;
        int unNormYear;
        int unNormMonth;
        int unNormDay;
        int unNormHour;
        int unNormMinute;
        double unNormSecond;
        int position;
        final AbstractDateTimeDV type;
        private String canonical;

        public DateTimeData(String string, AbstractDateTimeDV abstractDateTimeDV) {
            this.originalValue = string;
            this.type = abstractDateTimeDV;
        }

        public DateTimeData(int n, int n2, int n3, int n4, int n5, double d, int n6, String string, boolean bl, AbstractDateTimeDV abstractDateTimeDV) {
            this.year = n;
            this.month = n2;
            this.day = n3;
            this.hour = n4;
            this.minute = n5;
            this.second = d;
            this.utc = n6;
            this.type = abstractDateTimeDV;
            this.originalValue = string;
        }

        public boolean equals(Object object) {
            if (!(object instanceof DateTimeData)) {
                return false;
            }
            return this.type.compareDates(this, (DateTimeData)object, true) == 0;
        }

        public synchronized String toString() {
            if (this.canonical == null) {
                this.canonical = this.type.dateToString(this);
            }
            return this.canonical;
        }

        @Override
        public int getYears() {
            if (this.type instanceof DurationDV) {
                return 0;
            }
            return this.normalized ? this.year : this.unNormYear;
        }

        @Override
        public int getMonths() {
            if (this.type instanceof DurationDV) {
                return this.year * 12 + this.month;
            }
            return this.normalized ? this.month : this.unNormMonth;
        }

        @Override
        public int getDays() {
            if (this.type instanceof DurationDV) {
                return 0;
            }
            return this.normalized ? this.day : this.unNormDay;
        }

        @Override
        public int getHours() {
            if (this.type instanceof DurationDV) {
                return 0;
            }
            return this.normalized ? this.hour : this.unNormHour;
        }

        @Override
        public int getMinutes() {
            if (this.type instanceof DurationDV) {
                return 0;
            }
            return this.normalized ? this.minute : this.unNormMinute;
        }

        @Override
        public double getSeconds() {
            if (this.type instanceof DurationDV) {
                return (double)(this.day * 24 * 60 * 60 + this.hour * 60 * 60 + this.minute * 60) + this.second;
            }
            return this.normalized ? this.second : this.unNormSecond;
        }

        @Override
        public boolean hasTimeZone() {
            return this.utc != 0;
        }

        @Override
        public int getTimeZoneHours() {
            return this.timezoneHr;
        }

        @Override
        public int getTimeZoneMinutes() {
            return this.timezoneMin;
        }

        @Override
        public String getLexicalValue() {
            return this.originalValue;
        }

        @Override
        public XSDateTime normalize() {
            if (!this.normalized) {
                DateTimeData dateTimeData = (DateTimeData)this.clone();
                dateTimeData.normalized = true;
                return dateTimeData;
            }
            return this;
        }

        @Override
        public boolean isNormalized() {
            return this.normalized;
        }

        public Object clone() {
            DateTimeData dateTimeData = new DateTimeData(this.year, this.month, this.day, this.hour, this.minute, this.second, this.utc, this.originalValue, this.normalized, this.type);
            dateTimeData.canonical = this.canonical;
            dateTimeData.position = this.position;
            dateTimeData.timezoneHr = this.timezoneHr;
            dateTimeData.timezoneMin = this.timezoneMin;
            dateTimeData.unNormYear = this.unNormYear;
            dateTimeData.unNormMonth = this.unNormMonth;
            dateTimeData.unNormDay = this.unNormDay;
            dateTimeData.unNormHour = this.unNormHour;
            dateTimeData.unNormMinute = this.unNormMinute;
            dateTimeData.unNormSecond = this.unNormSecond;
            return dateTimeData;
        }

        @Override
        public XMLGregorianCalendar getXMLGregorianCalendar() {
            return this.type.getXMLGregorianCalendar(this);
        }

        @Override
        public Duration getDuration() {
            return this.type.getDuration(this);
        }
    }
}

