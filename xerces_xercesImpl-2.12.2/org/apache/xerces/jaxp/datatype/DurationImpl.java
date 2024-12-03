/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.datatype;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.xerces.jaxp.datatype.SerializedDuration;
import org.apache.xerces.jaxp.datatype.XMLGregorianCalendarImpl;
import org.apache.xerces.util.DatatypeMessageFormatter;

class DurationImpl
extends Duration
implements Serializable {
    private static final long serialVersionUID = -2650025807136350131L;
    private static final DatatypeConstants.Field[] FIELDS = new DatatypeConstants.Field[]{DatatypeConstants.YEARS, DatatypeConstants.MONTHS, DatatypeConstants.DAYS, DatatypeConstants.HOURS, DatatypeConstants.MINUTES, DatatypeConstants.SECONDS};
    private static final BigDecimal ZERO = BigDecimal.valueOf(0L);
    private final int signum;
    private final BigInteger years;
    private final BigInteger months;
    private final BigInteger days;
    private final BigInteger hours;
    private final BigInteger minutes;
    private final BigDecimal seconds;
    private static final XMLGregorianCalendar[] TEST_POINTS = new XMLGregorianCalendar[]{XMLGregorianCalendarImpl.parse("1696-09-01T00:00:00Z"), XMLGregorianCalendarImpl.parse("1697-02-01T00:00:00Z"), XMLGregorianCalendarImpl.parse("1903-03-01T00:00:00Z"), XMLGregorianCalendarImpl.parse("1903-07-01T00:00:00Z")};
    private static final BigDecimal[] FACTORS = new BigDecimal[]{BigDecimal.valueOf(12L), null, BigDecimal.valueOf(24L), BigDecimal.valueOf(60L), BigDecimal.valueOf(60L)};

    @Override
    public int getSign() {
        return this.signum;
    }

    private int calcSignum(boolean bl) {
        if (!(this.years != null && this.years.signum() != 0 || this.months != null && this.months.signum() != 0 || this.days != null && this.days.signum() != 0 || this.hours != null && this.hours.signum() != 0 || this.minutes != null && this.minutes.signum() != 0 || this.seconds != null && this.seconds.signum() != 0)) {
            return 0;
        }
        if (bl) {
            return 1;
        }
        return -1;
    }

    protected DurationImpl(boolean bl, BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3, BigInteger bigInteger4, BigInteger bigInteger5, BigDecimal bigDecimal) {
        this.years = bigInteger;
        this.months = bigInteger2;
        this.days = bigInteger3;
        this.hours = bigInteger4;
        this.minutes = bigInteger5;
        this.seconds = bigDecimal;
        this.signum = this.calcSignum(bl);
        if (bigInteger == null && bigInteger2 == null && bigInteger3 == null && bigInteger4 == null && bigInteger5 == null && bigDecimal == null) {
            throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "AllFieldsNull", null));
        }
        DurationImpl.testNonNegative(bigInteger, DatatypeConstants.YEARS);
        DurationImpl.testNonNegative(bigInteger2, DatatypeConstants.MONTHS);
        DurationImpl.testNonNegative(bigInteger3, DatatypeConstants.DAYS);
        DurationImpl.testNonNegative(bigInteger4, DatatypeConstants.HOURS);
        DurationImpl.testNonNegative(bigInteger5, DatatypeConstants.MINUTES);
        DurationImpl.testNonNegative(bigDecimal, DatatypeConstants.SECONDS);
    }

    private static void testNonNegative(BigInteger bigInteger, DatatypeConstants.Field field) {
        if (bigInteger != null && bigInteger.signum() < 0) {
            throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "NegativeField", new Object[]{field.toString()}));
        }
    }

    private static void testNonNegative(BigDecimal bigDecimal, DatatypeConstants.Field field) {
        if (bigDecimal != null && bigDecimal.signum() < 0) {
            throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "NegativeField", new Object[]{field.toString()}));
        }
    }

    protected DurationImpl(boolean bl, int n, int n2, int n3, int n4, int n5, int n6) {
        this(bl, DurationImpl.wrap(n), DurationImpl.wrap(n2), DurationImpl.wrap(n3), DurationImpl.wrap(n4), DurationImpl.wrap(n5), n6 != 0 ? BigDecimal.valueOf(n6) : null);
    }

    private static BigInteger wrap(int n) {
        if (n == Integer.MIN_VALUE) {
            return null;
        }
        return BigInteger.valueOf(n);
    }

    protected DurationImpl(long l) {
        boolean bl = false;
        long l2 = l;
        if (l2 > 0L) {
            this.signum = 1;
        } else if (l2 < 0L) {
            this.signum = -1;
            if (l2 == Long.MIN_VALUE) {
                ++l2;
                bl = true;
            }
            l2 *= -1L;
        } else {
            this.signum = 0;
        }
        this.years = null;
        this.months = null;
        this.seconds = BigDecimal.valueOf(l2 % 60000L + (long)(bl ? 1 : 0), 3);
        this.minutes = (l2 /= 60000L) == 0L ? null : BigInteger.valueOf(l2 % 60L);
        this.hours = (l2 /= 60L) == 0L ? null : BigInteger.valueOf(l2 % 24L);
        this.days = (l2 /= 24L) == 0L ? null : BigInteger.valueOf(l2);
    }

    protected DurationImpl(String string) throws IllegalArgumentException {
        boolean bl;
        String string2 = string;
        int[] nArray = new int[1];
        int n = string2.length();
        boolean bl2 = false;
        if (string == null) {
            throw new NullPointerException();
        }
        nArray[0] = 0;
        if (n != nArray[0] && string2.charAt(nArray[0]) == '-') {
            nArray[0] = nArray[0] + 1;
            bl = false;
        } else {
            bl = true;
        }
        if (n != nArray[0]) {
            int n2 = nArray[0];
            nArray[0] = n2 + 1;
            if (string2.charAt(n2) != 'P') {
                throw new IllegalArgumentException(string2);
            }
        }
        int n3 = 0;
        String[] stringArray = new String[3];
        int[] nArray2 = new int[3];
        while (n != nArray[0] && DurationImpl.isDigit(string2.charAt(nArray[0])) && n3 < 3) {
            nArray2[n3] = nArray[0];
            stringArray[n3++] = DurationImpl.parsePiece(string2, nArray);
        }
        if (n != nArray[0]) {
            int n4 = nArray[0];
            nArray[0] = n4 + 1;
            if (string2.charAt(n4) == 'T') {
                bl2 = true;
            } else {
                throw new IllegalArgumentException(string2);
            }
        }
        int n5 = 0;
        String[] stringArray2 = new String[3];
        int[] nArray3 = new int[3];
        while (n != nArray[0] && DurationImpl.isDigitOrPeriod(string2.charAt(nArray[0])) && n5 < 3) {
            nArray3[n5] = nArray[0];
            stringArray2[n5++] = DurationImpl.parsePiece(string2, nArray);
        }
        if (bl2 && n5 == 0) {
            throw new IllegalArgumentException(string2);
        }
        if (n != nArray[0]) {
            throw new IllegalArgumentException(string2);
        }
        if (n3 == 0 && n5 == 0) {
            throw new IllegalArgumentException(string2);
        }
        DurationImpl.organizeParts(string2, stringArray, nArray2, n3, "YMD");
        DurationImpl.organizeParts(string2, stringArray2, nArray3, n5, "HMS");
        this.years = DurationImpl.parseBigInteger(string2, stringArray[0], nArray2[0]);
        this.months = DurationImpl.parseBigInteger(string2, stringArray[1], nArray2[1]);
        this.days = DurationImpl.parseBigInteger(string2, stringArray[2], nArray2[2]);
        this.hours = DurationImpl.parseBigInteger(string2, stringArray2[0], nArray3[0]);
        this.minutes = DurationImpl.parseBigInteger(string2, stringArray2[1], nArray3[1]);
        this.seconds = DurationImpl.parseBigDecimal(string2, stringArray2[2], nArray3[2]);
        this.signum = this.calcSignum(bl);
    }

    private static boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    private static boolean isDigitOrPeriod(char c) {
        return DurationImpl.isDigit(c) || c == '.';
    }

    private static String parsePiece(String string, int[] nArray) throws IllegalArgumentException {
        int n = nArray[0];
        while (nArray[0] < string.length() && DurationImpl.isDigitOrPeriod(string.charAt(nArray[0]))) {
            nArray[0] = nArray[0] + 1;
        }
        if (nArray[0] == string.length()) {
            throw new IllegalArgumentException(string);
        }
        nArray[0] = nArray[0] + 1;
        return string.substring(n, nArray[0]);
    }

    private static void organizeParts(String string, String[] stringArray, int[] nArray, int n, String string2) throws IllegalArgumentException {
        int n2 = string2.length();
        for (int i = n - 1; i >= 0; --i) {
            if (stringArray[i] == null) {
                throw new IllegalArgumentException(string);
            }
            int n3 = string2.lastIndexOf(stringArray[i].charAt(stringArray[i].length() - 1), n2 - 1);
            if (n3 == -1) {
                throw new IllegalArgumentException(string);
            }
            for (int j = n3 + 1; j < n2; ++j) {
                stringArray[j] = null;
            }
            n2 = n3;
            stringArray[n2] = stringArray[i];
            nArray[n2] = nArray[i];
        }
        --n2;
        while (n2 >= 0) {
            stringArray[n2] = null;
            --n2;
        }
    }

    private static BigInteger parseBigInteger(String string, String string2, int n) throws IllegalArgumentException {
        if (string2 == null) {
            return null;
        }
        string2 = string2.substring(0, string2.length() - 1);
        return new BigInteger(string2);
    }

    private static BigDecimal parseBigDecimal(String string, String string2, int n) throws IllegalArgumentException {
        if (string2 == null) {
            return null;
        }
        string2 = string2.substring(0, string2.length() - 1);
        return new BigDecimal(string2);
    }

    @Override
    public int compare(Duration duration) {
        BigInteger bigInteger = BigInteger.valueOf(Integer.MAX_VALUE);
        if (this.years != null && this.years.compareTo(bigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.YEARS.toString(), this.years.toString()}));
        }
        if (this.months != null && this.months.compareTo(bigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MONTHS.toString(), this.months.toString()}));
        }
        if (this.days != null && this.days.compareTo(bigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.DAYS.toString(), this.days.toString()}));
        }
        if (this.hours != null && this.hours.compareTo(bigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.HOURS.toString(), this.hours.toString()}));
        }
        if (this.minutes != null && this.minutes.compareTo(bigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MINUTES.toString(), this.minutes.toString()}));
        }
        if (this.seconds != null && this.seconds.toBigInteger().compareTo(bigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.SECONDS.toString(), this.toString(this.seconds)}));
        }
        BigInteger bigInteger2 = (BigInteger)duration.getField(DatatypeConstants.YEARS);
        if (bigInteger2 != null && bigInteger2.compareTo(bigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.YEARS.toString(), bigInteger2.toString()}));
        }
        BigInteger bigInteger3 = (BigInteger)duration.getField(DatatypeConstants.MONTHS);
        if (bigInteger3 != null && bigInteger3.compareTo(bigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MONTHS.toString(), bigInteger3.toString()}));
        }
        BigInteger bigInteger4 = (BigInteger)duration.getField(DatatypeConstants.DAYS);
        if (bigInteger4 != null && bigInteger4.compareTo(bigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.DAYS.toString(), bigInteger4.toString()}));
        }
        BigInteger bigInteger5 = (BigInteger)duration.getField(DatatypeConstants.HOURS);
        if (bigInteger5 != null && bigInteger5.compareTo(bigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.HOURS.toString(), bigInteger5.toString()}));
        }
        BigInteger bigInteger6 = (BigInteger)duration.getField(DatatypeConstants.MINUTES);
        if (bigInteger6 != null && bigInteger6.compareTo(bigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MINUTES.toString(), bigInteger6.toString()}));
        }
        BigDecimal bigDecimal = (BigDecimal)duration.getField(DatatypeConstants.SECONDS);
        BigInteger bigInteger7 = null;
        if (bigDecimal != null) {
            bigInteger7 = bigDecimal.toBigInteger();
        }
        if (bigInteger7 != null && bigInteger7.compareTo(bigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.SECONDS.toString(), bigInteger7.toString()}));
        }
        GregorianCalendar gregorianCalendar = new GregorianCalendar(1970, 1, 1, 0, 0, 0);
        gregorianCalendar.add(1, this.getYears() * this.getSign());
        gregorianCalendar.add(2, this.getMonths() * this.getSign());
        gregorianCalendar.add(6, this.getDays() * this.getSign());
        gregorianCalendar.add(11, this.getHours() * this.getSign());
        gregorianCalendar.add(12, this.getMinutes() * this.getSign());
        gregorianCalendar.add(13, this.getSeconds() * this.getSign());
        GregorianCalendar gregorianCalendar2 = new GregorianCalendar(1970, 1, 1, 0, 0, 0);
        gregorianCalendar2.add(1, duration.getYears() * duration.getSign());
        gregorianCalendar2.add(2, duration.getMonths() * duration.getSign());
        gregorianCalendar2.add(6, duration.getDays() * duration.getSign());
        gregorianCalendar2.add(11, duration.getHours() * duration.getSign());
        gregorianCalendar2.add(12, duration.getMinutes() * duration.getSign());
        gregorianCalendar2.add(13, duration.getSeconds() * duration.getSign());
        if (gregorianCalendar.equals(gregorianCalendar2)) {
            return 0;
        }
        return this.compareDates(this, duration);
    }

    private int compareDates(Duration duration, Duration duration2) {
        int n = 2;
        int n2 = 2;
        XMLGregorianCalendar xMLGregorianCalendar = (XMLGregorianCalendar)TEST_POINTS[0].clone();
        XMLGregorianCalendar xMLGregorianCalendar2 = (XMLGregorianCalendar)TEST_POINTS[0].clone();
        xMLGregorianCalendar.add(duration);
        xMLGregorianCalendar2.add(duration2);
        n = xMLGregorianCalendar.compare(xMLGregorianCalendar2);
        if (n == 2) {
            return 2;
        }
        xMLGregorianCalendar = (XMLGregorianCalendar)TEST_POINTS[1].clone();
        xMLGregorianCalendar2 = (XMLGregorianCalendar)TEST_POINTS[1].clone();
        xMLGregorianCalendar.add(duration);
        xMLGregorianCalendar2.add(duration2);
        n2 = xMLGregorianCalendar.compare(xMLGregorianCalendar2);
        n = this.compareResults(n, n2);
        if (n == 2) {
            return 2;
        }
        xMLGregorianCalendar = (XMLGregorianCalendar)TEST_POINTS[2].clone();
        xMLGregorianCalendar2 = (XMLGregorianCalendar)TEST_POINTS[2].clone();
        xMLGregorianCalendar.add(duration);
        xMLGregorianCalendar2.add(duration2);
        n2 = xMLGregorianCalendar.compare(xMLGregorianCalendar2);
        n = this.compareResults(n, n2);
        if (n == 2) {
            return 2;
        }
        xMLGregorianCalendar = (XMLGregorianCalendar)TEST_POINTS[3].clone();
        xMLGregorianCalendar2 = (XMLGregorianCalendar)TEST_POINTS[3].clone();
        xMLGregorianCalendar.add(duration);
        xMLGregorianCalendar2.add(duration2);
        n2 = xMLGregorianCalendar.compare(xMLGregorianCalendar2);
        n = this.compareResults(n, n2);
        return n;
    }

    private int compareResults(int n, int n2) {
        if (n2 == 2) {
            return 2;
        }
        if (n != n2) {
            return 2;
        }
        return n;
    }

    @Override
    public int hashCode() {
        GregorianCalendar gregorianCalendar = TEST_POINTS[0].toGregorianCalendar();
        this.addTo(gregorianCalendar);
        return (int)DurationImpl.getCalendarTimeInMillis(gregorianCalendar);
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        if (this.signum < 0) {
            stringBuffer.append('-');
        }
        stringBuffer.append('P');
        if (this.years != null) {
            stringBuffer.append(this.years).append('Y');
        }
        if (this.months != null) {
            stringBuffer.append(this.months).append('M');
        }
        if (this.days != null) {
            stringBuffer.append(this.days).append('D');
        }
        if (this.hours != null || this.minutes != null || this.seconds != null) {
            stringBuffer.append('T');
            if (this.hours != null) {
                stringBuffer.append(this.hours).append('H');
            }
            if (this.minutes != null) {
                stringBuffer.append(this.minutes).append('M');
            }
            if (this.seconds != null) {
                stringBuffer.append(this.toString(this.seconds)).append('S');
            }
        }
        return stringBuffer.toString();
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

    @Override
    public boolean isSet(DatatypeConstants.Field field) {
        if (field == null) {
            String string = "javax.xml.datatype.Duration#isSet(DatatypeConstants.Field field)";
            throw new NullPointerException(DatatypeMessageFormatter.formatMessage(null, "FieldCannotBeNull", new Object[]{string}));
        }
        if (field == DatatypeConstants.YEARS) {
            return this.years != null;
        }
        if (field == DatatypeConstants.MONTHS) {
            return this.months != null;
        }
        if (field == DatatypeConstants.DAYS) {
            return this.days != null;
        }
        if (field == DatatypeConstants.HOURS) {
            return this.hours != null;
        }
        if (field == DatatypeConstants.MINUTES) {
            return this.minutes != null;
        }
        if (field == DatatypeConstants.SECONDS) {
            return this.seconds != null;
        }
        String string = "javax.xml.datatype.Duration#isSet(DatatypeConstants.Field field)";
        throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "UnknownField", new Object[]{string, field.toString()}));
    }

    @Override
    public Number getField(DatatypeConstants.Field field) {
        if (field == null) {
            String string = "javax.xml.datatype.Duration#isSet(DatatypeConstants.Field field) ";
            throw new NullPointerException(DatatypeMessageFormatter.formatMessage(null, "FieldCannotBeNull", new Object[]{string}));
        }
        if (field == DatatypeConstants.YEARS) {
            return this.years;
        }
        if (field == DatatypeConstants.MONTHS) {
            return this.months;
        }
        if (field == DatatypeConstants.DAYS) {
            return this.days;
        }
        if (field == DatatypeConstants.HOURS) {
            return this.hours;
        }
        if (field == DatatypeConstants.MINUTES) {
            return this.minutes;
        }
        if (field == DatatypeConstants.SECONDS) {
            return this.seconds;
        }
        String string = "javax.xml.datatype.Duration#(getSet(DatatypeConstants.Field field)";
        throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "UnknownField", new Object[]{string, field.toString()}));
    }

    @Override
    public int getYears() {
        return this.getInt(DatatypeConstants.YEARS);
    }

    @Override
    public int getMonths() {
        return this.getInt(DatatypeConstants.MONTHS);
    }

    @Override
    public int getDays() {
        return this.getInt(DatatypeConstants.DAYS);
    }

    @Override
    public int getHours() {
        return this.getInt(DatatypeConstants.HOURS);
    }

    @Override
    public int getMinutes() {
        return this.getInt(DatatypeConstants.MINUTES);
    }

    @Override
    public int getSeconds() {
        return this.getInt(DatatypeConstants.SECONDS);
    }

    private int getInt(DatatypeConstants.Field field) {
        Number number = this.getField(field);
        if (number == null) {
            return 0;
        }
        return number.intValue();
    }

    @Override
    public long getTimeInMillis(Calendar calendar) {
        Calendar calendar2 = (Calendar)calendar.clone();
        this.addTo(calendar2);
        return DurationImpl.getCalendarTimeInMillis(calendar2) - DurationImpl.getCalendarTimeInMillis(calendar);
    }

    @Override
    public long getTimeInMillis(Date date) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        this.addTo(gregorianCalendar);
        return DurationImpl.getCalendarTimeInMillis(gregorianCalendar) - date.getTime();
    }

    @Override
    public Duration normalizeWith(Calendar calendar) {
        Calendar calendar2 = (Calendar)calendar.clone();
        calendar2.add(1, this.getYears() * this.signum);
        calendar2.add(2, this.getMonths() * this.signum);
        calendar2.add(5, this.getDays() * this.signum);
        long l = DurationImpl.getCalendarTimeInMillis(calendar2) - DurationImpl.getCalendarTimeInMillis(calendar);
        int n = (int)(l / 86400000L);
        return new DurationImpl(n >= 0, null, null, DurationImpl.wrap(Math.abs(n)), (BigInteger)this.getField(DatatypeConstants.HOURS), (BigInteger)this.getField(DatatypeConstants.MINUTES), (BigDecimal)this.getField(DatatypeConstants.SECONDS));
    }

    @Override
    public Duration multiply(int n) {
        return this.multiply(BigDecimal.valueOf(n));
    }

    @Override
    public Duration multiply(BigDecimal bigDecimal) {
        BigDecimal bigDecimal2 = ZERO;
        int n = bigDecimal.signum();
        bigDecimal = bigDecimal.abs();
        BigDecimal[] bigDecimalArray = new BigDecimal[6];
        for (int i = 0; i < 5; ++i) {
            BigDecimal bigDecimal3 = this.getFieldAsBigDecimal(FIELDS[i]);
            bigDecimal3 = bigDecimal3.multiply(bigDecimal).add(bigDecimal2);
            bigDecimalArray[i] = bigDecimal3.setScale(0, 1);
            bigDecimal3 = bigDecimal3.subtract(bigDecimalArray[i]);
            if (i == 1) {
                if (bigDecimal3.signum() != 0) {
                    throw new IllegalStateException();
                }
                bigDecimal2 = ZERO;
                continue;
            }
            bigDecimal2 = bigDecimal3.multiply(FACTORS[i]);
        }
        bigDecimalArray[5] = this.seconds != null ? this.seconds.multiply(bigDecimal).add(bigDecimal2) : bigDecimal2;
        return new DurationImpl(this.signum * n >= 0, DurationImpl.toBigInteger(bigDecimalArray[0], null == this.years), DurationImpl.toBigInteger(bigDecimalArray[1], null == this.months), DurationImpl.toBigInteger(bigDecimalArray[2], null == this.days), DurationImpl.toBigInteger(bigDecimalArray[3], null == this.hours), DurationImpl.toBigInteger(bigDecimalArray[4], null == this.minutes), (BigDecimal)(bigDecimalArray[5].signum() == 0 && this.seconds == null ? null : bigDecimalArray[5]));
    }

    private BigDecimal getFieldAsBigDecimal(DatatypeConstants.Field field) {
        if (field == DatatypeConstants.SECONDS) {
            if (this.seconds != null) {
                return this.seconds;
            }
            return ZERO;
        }
        BigInteger bigInteger = (BigInteger)this.getField(field);
        if (bigInteger == null) {
            return ZERO;
        }
        return new BigDecimal(bigInteger);
    }

    private static BigInteger toBigInteger(BigDecimal bigDecimal, boolean bl) {
        if (bl && bigDecimal.signum() == 0) {
            return null;
        }
        return bigDecimal.unscaledValue();
    }

    @Override
    public Duration add(Duration duration) {
        DurationImpl durationImpl = this;
        BigDecimal[] bigDecimalArray = new BigDecimal[]{DurationImpl.sanitize((BigInteger)((Duration)durationImpl).getField(DatatypeConstants.YEARS), ((Duration)durationImpl).getSign()).add(DurationImpl.sanitize((BigInteger)duration.getField(DatatypeConstants.YEARS), duration.getSign())), DurationImpl.sanitize((BigInteger)((Duration)durationImpl).getField(DatatypeConstants.MONTHS), ((Duration)durationImpl).getSign()).add(DurationImpl.sanitize((BigInteger)duration.getField(DatatypeConstants.MONTHS), duration.getSign())), DurationImpl.sanitize((BigInteger)((Duration)durationImpl).getField(DatatypeConstants.DAYS), ((Duration)durationImpl).getSign()).add(DurationImpl.sanitize((BigInteger)duration.getField(DatatypeConstants.DAYS), duration.getSign())), DurationImpl.sanitize((BigInteger)((Duration)durationImpl).getField(DatatypeConstants.HOURS), ((Duration)durationImpl).getSign()).add(DurationImpl.sanitize((BigInteger)duration.getField(DatatypeConstants.HOURS), duration.getSign())), DurationImpl.sanitize((BigInteger)((Duration)durationImpl).getField(DatatypeConstants.MINUTES), ((Duration)durationImpl).getSign()).add(DurationImpl.sanitize((BigInteger)duration.getField(DatatypeConstants.MINUTES), duration.getSign())), DurationImpl.sanitize((BigDecimal)((Duration)durationImpl).getField(DatatypeConstants.SECONDS), ((Duration)durationImpl).getSign()).add(DurationImpl.sanitize((BigDecimal)duration.getField(DatatypeConstants.SECONDS), duration.getSign()))};
        DurationImpl.alignSigns(bigDecimalArray, 0, 2);
        DurationImpl.alignSigns(bigDecimalArray, 2, 6);
        int n = 0;
        for (int i = 0; i < 6; ++i) {
            if (n * bigDecimalArray[i].signum() < 0) {
                throw new IllegalStateException();
            }
            if (n != 0) continue;
            n = bigDecimalArray[i].signum();
        }
        return new DurationImpl(n >= 0, DurationImpl.toBigInteger(DurationImpl.sanitize(bigDecimalArray[0], n), ((Duration)durationImpl).getField(DatatypeConstants.YEARS) == null && duration.getField(DatatypeConstants.YEARS) == null), DurationImpl.toBigInteger(DurationImpl.sanitize(bigDecimalArray[1], n), ((Duration)durationImpl).getField(DatatypeConstants.MONTHS) == null && duration.getField(DatatypeConstants.MONTHS) == null), DurationImpl.toBigInteger(DurationImpl.sanitize(bigDecimalArray[2], n), ((Duration)durationImpl).getField(DatatypeConstants.DAYS) == null && duration.getField(DatatypeConstants.DAYS) == null), DurationImpl.toBigInteger(DurationImpl.sanitize(bigDecimalArray[3], n), ((Duration)durationImpl).getField(DatatypeConstants.HOURS) == null && duration.getField(DatatypeConstants.HOURS) == null), DurationImpl.toBigInteger(DurationImpl.sanitize(bigDecimalArray[4], n), ((Duration)durationImpl).getField(DatatypeConstants.MINUTES) == null && duration.getField(DatatypeConstants.MINUTES) == null), (BigDecimal)(bigDecimalArray[5].signum() == 0 && ((Duration)durationImpl).getField(DatatypeConstants.SECONDS) == null && duration.getField(DatatypeConstants.SECONDS) == null ? null : DurationImpl.sanitize(bigDecimalArray[5], n)));
    }

    private static void alignSigns(BigDecimal[] bigDecimalArray, int n, int n2) {
        boolean bl;
        do {
            bl = false;
            int n3 = 0;
            for (int i = n; i < n2; ++i) {
                if (n3 * bigDecimalArray[i].signum() < 0) {
                    bl = true;
                    BigDecimal bigDecimal = bigDecimalArray[i].abs().divide(FACTORS[i - 1], 0);
                    if (bigDecimalArray[i].signum() > 0) {
                        bigDecimal = bigDecimal.negate();
                    }
                    bigDecimalArray[i - 1] = bigDecimalArray[i - 1].subtract(bigDecimal);
                    bigDecimalArray[i] = bigDecimalArray[i].add(bigDecimal.multiply(FACTORS[i - 1]));
                }
                if (bigDecimalArray[i].signum() == 0) continue;
                n3 = bigDecimalArray[i].signum();
            }
        } while (bl);
    }

    private static BigDecimal sanitize(BigInteger bigInteger, int n) {
        if (n == 0 || bigInteger == null) {
            return ZERO;
        }
        if (n > 0) {
            return new BigDecimal(bigInteger);
        }
        return new BigDecimal(bigInteger.negate());
    }

    static BigDecimal sanitize(BigDecimal bigDecimal, int n) {
        if (n == 0 || bigDecimal == null) {
            return ZERO;
        }
        if (n > 0) {
            return bigDecimal;
        }
        return bigDecimal.negate();
    }

    @Override
    public Duration subtract(Duration duration) {
        return this.add(duration.negate());
    }

    @Override
    public Duration negate() {
        return new DurationImpl(this.signum <= 0, this.years, this.months, this.days, this.hours, this.minutes, this.seconds);
    }

    public int signum() {
        return this.signum;
    }

    @Override
    public void addTo(Calendar calendar) {
        calendar.add(1, this.getYears() * this.signum);
        calendar.add(2, this.getMonths() * this.signum);
        calendar.add(5, this.getDays() * this.signum);
        calendar.add(10, this.getHours() * this.signum);
        calendar.add(12, this.getMinutes() * this.signum);
        calendar.add(13, this.getSeconds() * this.signum);
        if (this.seconds != null) {
            BigDecimal bigDecimal = this.seconds.subtract(this.seconds.setScale(0, 1));
            int n = bigDecimal.movePointRight(3).intValue();
            calendar.add(14, n * this.signum);
        }
    }

    @Override
    public void addTo(Date date) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        this.addTo(gregorianCalendar);
        date.setTime(DurationImpl.getCalendarTimeInMillis(gregorianCalendar));
    }

    private static long getCalendarTimeInMillis(Calendar calendar) {
        return calendar.getTime().getTime();
    }

    private Object writeReplace() throws IOException {
        return new SerializedDuration(this.toString());
    }
}

