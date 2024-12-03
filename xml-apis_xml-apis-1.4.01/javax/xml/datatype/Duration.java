/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.namespace.QName;

public abstract class Duration {
    public QName getXMLSchemaType() {
        boolean bl = this.isSet(DatatypeConstants.YEARS);
        boolean bl2 = this.isSet(DatatypeConstants.MONTHS);
        boolean bl3 = this.isSet(DatatypeConstants.DAYS);
        boolean bl4 = this.isSet(DatatypeConstants.HOURS);
        boolean bl5 = this.isSet(DatatypeConstants.MINUTES);
        boolean bl6 = this.isSet(DatatypeConstants.SECONDS);
        if (bl && bl2 && bl3 && bl4 && bl5 && bl6) {
            return DatatypeConstants.DURATION;
        }
        if (!bl && !bl2 && bl3 && bl4 && bl5 && bl6) {
            return DatatypeConstants.DURATION_DAYTIME;
        }
        if (bl && bl2 && !bl3 && !bl4 && !bl5 && !bl6) {
            return DatatypeConstants.DURATION_YEARMONTH;
        }
        throw new IllegalStateException("javax.xml.datatype.Duration#getXMLSchemaType(): this Duration does not match one of the XML Schema date/time datatypes: year set = " + bl + " month set = " + bl2 + " day set = " + bl3 + " hour set = " + bl4 + " minute set = " + bl5 + " second set = " + bl6);
    }

    public abstract int getSign();

    public int getYears() {
        return this.getFieldValueAsInt(DatatypeConstants.YEARS);
    }

    public int getMonths() {
        return this.getFieldValueAsInt(DatatypeConstants.MONTHS);
    }

    public int getDays() {
        return this.getFieldValueAsInt(DatatypeConstants.DAYS);
    }

    public int getHours() {
        return this.getFieldValueAsInt(DatatypeConstants.HOURS);
    }

    public int getMinutes() {
        return this.getFieldValueAsInt(DatatypeConstants.MINUTES);
    }

    public int getSeconds() {
        return this.getFieldValueAsInt(DatatypeConstants.SECONDS);
    }

    public long getTimeInMillis(Calendar calendar) {
        Calendar calendar2 = (Calendar)calendar.clone();
        this.addTo(calendar2);
        return Duration.getCalendarTimeInMillis(calendar2) - Duration.getCalendarTimeInMillis(calendar);
    }

    public long getTimeInMillis(Date date) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        this.addTo(gregorianCalendar);
        return Duration.getCalendarTimeInMillis(gregorianCalendar) - date.getTime();
    }

    public abstract Number getField(DatatypeConstants.Field var1);

    private int getFieldValueAsInt(DatatypeConstants.Field field) {
        Number number = this.getField(field);
        if (number != null) {
            return number.intValue();
        }
        return 0;
    }

    public abstract boolean isSet(DatatypeConstants.Field var1);

    public abstract Duration add(Duration var1);

    public abstract void addTo(Calendar var1);

    public void addTo(Date date) {
        if (date == null) {
            throw new NullPointerException("Cannot call " + this.getClass().getName() + "#addTo(Date date) with date == null.");
        }
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        this.addTo(gregorianCalendar);
        date.setTime(Duration.getCalendarTimeInMillis(gregorianCalendar));
    }

    public Duration subtract(Duration duration) {
        return this.add(duration.negate());
    }

    public Duration multiply(int n) {
        return this.multiply(BigDecimal.valueOf(n));
    }

    public abstract Duration multiply(BigDecimal var1);

    public abstract Duration negate();

    public abstract Duration normalizeWith(Calendar var1);

    public abstract int compare(Duration var1);

    public boolean isLongerThan(Duration duration) {
        return this.compare(duration) == 1;
    }

    public boolean isShorterThan(Duration duration) {
        return this.compare(duration) == -1;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Duration) {
            return this.compare((Duration)object) == 0;
        }
        return false;
    }

    public abstract int hashCode();

    public String toString() {
        BigInteger bigInteger;
        BigInteger bigInteger2;
        StringBuffer stringBuffer = new StringBuffer();
        if (this.getSign() < 0) {
            stringBuffer.append('-');
        }
        stringBuffer.append('P');
        BigInteger bigInteger3 = (BigInteger)this.getField(DatatypeConstants.YEARS);
        if (bigInteger3 != null) {
            stringBuffer.append(bigInteger3).append('Y');
        }
        if ((bigInteger2 = (BigInteger)this.getField(DatatypeConstants.MONTHS)) != null) {
            stringBuffer.append(bigInteger2).append('M');
        }
        if ((bigInteger = (BigInteger)this.getField(DatatypeConstants.DAYS)) != null) {
            stringBuffer.append(bigInteger).append('D');
        }
        BigInteger bigInteger4 = (BigInteger)this.getField(DatatypeConstants.HOURS);
        BigInteger bigInteger5 = (BigInteger)this.getField(DatatypeConstants.MINUTES);
        BigDecimal bigDecimal = (BigDecimal)this.getField(DatatypeConstants.SECONDS);
        if (bigInteger4 != null || bigInteger5 != null || bigDecimal != null) {
            stringBuffer.append('T');
            if (bigInteger4 != null) {
                stringBuffer.append(bigInteger4).append('H');
            }
            if (bigInteger5 != null) {
                stringBuffer.append(bigInteger5).append('M');
            }
            if (bigDecimal != null) {
                stringBuffer.append(this.toString(bigDecimal)).append('S');
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
            int n3 = 0;
            while (n3 < -n2) {
                stringBuffer.append('0');
                ++n3;
            }
            stringBuffer.append(string);
        }
        return stringBuffer.toString();
    }

    private static long getCalendarTimeInMillis(Calendar calendar) {
        return calendar.getTime().getTime();
    }
}

