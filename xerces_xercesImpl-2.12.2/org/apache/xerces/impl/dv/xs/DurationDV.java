/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.datatype.Duration;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.xs.AbstractDateTimeDV;
import org.apache.xerces.impl.dv.xs.SchemaDateTimeException;

public class DurationDV
extends AbstractDateTimeDV {
    public static final int DURATION_TYPE = 0;
    public static final int YEARMONTHDURATION_TYPE = 1;
    public static final int DAYTIMEDURATION_TYPE = 2;
    private static final AbstractDateTimeDV.DateTimeData[] DATETIMES = new AbstractDateTimeDV.DateTimeData[]{new AbstractDateTimeDV.DateTimeData(1696, 9, 1, 0, 0, 0.0, 90, null, true, null), new AbstractDateTimeDV.DateTimeData(1697, 2, 1, 0, 0, 0.0, 90, null, true, null), new AbstractDateTimeDV.DateTimeData(1903, 3, 1, 0, 0, 0.0, 90, null, true, null), new AbstractDateTimeDV.DateTimeData(1903, 7, 1, 0, 0, 0.0, 90, null, true, null)};

    @Override
    public Object getActualValue(String string, ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return this.parse(string, 0);
        }
        catch (Exception exception) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{string, "duration"});
        }
    }

    protected AbstractDateTimeDV.DateTimeData parse(String string, int n) throws SchemaDateTimeException {
        char c;
        int n2 = string.length();
        AbstractDateTimeDV.DateTimeData dateTimeData = new AbstractDateTimeDV.DateTimeData(string, this);
        int n3 = 0;
        if ((c = string.charAt(n3++)) != 'P' && c != '-') {
            throw new SchemaDateTimeException();
        }
        int n4 = dateTimeData.utc = c == '-' ? 45 : 0;
        if (c == '-' && string.charAt(n3++) != 'P') {
            throw new SchemaDateTimeException();
        }
        int n5 = 1;
        if (dateTimeData.utc == 45) {
            n5 = -1;
        }
        boolean bl = false;
        int n6 = this.indexOf(string, n3, n2, 'T');
        if (n6 == -1) {
            n6 = n2;
        } else if (n == 1) {
            throw new SchemaDateTimeException();
        }
        int n7 = this.indexOf(string, n3, n6, 'Y');
        if (n7 != -1) {
            if (n == 2) {
                throw new SchemaDateTimeException();
            }
            dateTimeData.year = n5 * this.parseInt(string, n3, n7);
            n3 = n7 + 1;
            bl = true;
        }
        if ((n7 = this.indexOf(string, n3, n6, 'M')) != -1) {
            if (n == 2) {
                throw new SchemaDateTimeException();
            }
            dateTimeData.month = n5 * this.parseInt(string, n3, n7);
            n3 = n7 + 1;
            bl = true;
        }
        if ((n7 = this.indexOf(string, n3, n6, 'D')) != -1) {
            if (n == 1) {
                throw new SchemaDateTimeException();
            }
            dateTimeData.day = n5 * this.parseInt(string, n3, n7);
            n3 = n7 + 1;
            bl = true;
        }
        if (n2 == n6 && n3 != n2) {
            throw new SchemaDateTimeException();
        }
        if (n2 != n6) {
            if ((n7 = this.indexOf(string, ++n3, n2, 'H')) != -1) {
                dateTimeData.hour = n5 * this.parseInt(string, n3, n7);
                n3 = n7 + 1;
                bl = true;
            }
            if ((n7 = this.indexOf(string, n3, n2, 'M')) != -1) {
                dateTimeData.minute = n5 * this.parseInt(string, n3, n7);
                n3 = n7 + 1;
                bl = true;
            }
            if ((n7 = this.indexOf(string, n3, n2, 'S')) != -1) {
                dateTimeData.second = (double)n5 * this.parseSecond(string, n3, n7);
                n3 = n7 + 1;
                bl = true;
            }
            if (n3 != n2 || string.charAt(--n3) == 'T') {
                throw new SchemaDateTimeException();
            }
        }
        if (!bl) {
            throw new SchemaDateTimeException();
        }
        return dateTimeData;
    }

    @Override
    protected short compareDates(AbstractDateTimeDV.DateTimeData dateTimeData, AbstractDateTimeDV.DateTimeData dateTimeData2, boolean bl) {
        AbstractDateTimeDV.DateTimeData dateTimeData3;
        short s = 2;
        short s2 = this.compareOrder(dateTimeData, dateTimeData2);
        if (s2 == 0) {
            return 0;
        }
        AbstractDateTimeDV.DateTimeData[] dateTimeDataArray = new AbstractDateTimeDV.DateTimeData[]{new AbstractDateTimeDV.DateTimeData(null, this), new AbstractDateTimeDV.DateTimeData(null, this)};
        AbstractDateTimeDV.DateTimeData dateTimeData4 = this.addDuration(dateTimeData, DATETIMES[0], dateTimeDataArray[0]);
        s2 = this.compareOrder(dateTimeData4, dateTimeData3 = this.addDuration(dateTimeData2, DATETIMES[0], dateTimeDataArray[1]));
        if (s2 == 2) {
            return 2;
        }
        dateTimeData4 = this.addDuration(dateTimeData, DATETIMES[1], dateTimeDataArray[0]);
        s = this.compareOrder(dateTimeData4, dateTimeData3 = this.addDuration(dateTimeData2, DATETIMES[1], dateTimeDataArray[1]));
        if ((s2 = this.compareResults(s2, s, bl)) == 2) {
            return 2;
        }
        dateTimeData4 = this.addDuration(dateTimeData, DATETIMES[2], dateTimeDataArray[0]);
        s = this.compareOrder(dateTimeData4, dateTimeData3 = this.addDuration(dateTimeData2, DATETIMES[2], dateTimeDataArray[1]));
        if ((s2 = this.compareResults(s2, s, bl)) == 2) {
            return 2;
        }
        dateTimeData4 = this.addDuration(dateTimeData, DATETIMES[3], dateTimeDataArray[0]);
        dateTimeData3 = this.addDuration(dateTimeData2, DATETIMES[3], dateTimeDataArray[1]);
        s = this.compareOrder(dateTimeData4, dateTimeData3);
        s2 = this.compareResults(s2, s, bl);
        return s2;
    }

    private short compareResults(short s, short s2, boolean bl) {
        if (s2 == 2) {
            return 2;
        }
        if (s != s2 && bl) {
            return 2;
        }
        if (s != s2 && !bl) {
            if (s != 0 && s2 != 0) {
                return 2;
            }
            return s != 0 ? s : s2;
        }
        return s;
    }

    private AbstractDateTimeDV.DateTimeData addDuration(AbstractDateTimeDV.DateTimeData dateTimeData, AbstractDateTimeDV.DateTimeData dateTimeData2, AbstractDateTimeDV.DateTimeData dateTimeData3) {
        this.resetDateObj(dateTimeData3);
        int n = dateTimeData2.month + dateTimeData.month;
        dateTimeData3.month = this.modulo(n, 1, 13);
        int n2 = this.fQuotient(n, 1, 13);
        dateTimeData3.year = dateTimeData2.year + dateTimeData.year + n2;
        double d = dateTimeData2.second + dateTimeData.second;
        n2 = (int)Math.floor(d / 60.0);
        dateTimeData3.second = d - (double)(n2 * 60);
        n = dateTimeData2.minute + dateTimeData.minute + n2;
        n2 = this.fQuotient(n, 60);
        dateTimeData3.minute = this.mod(n, 60, n2);
        n = dateTimeData2.hour + dateTimeData.hour + n2;
        n2 = this.fQuotient(n, 24);
        dateTimeData3.hour = this.mod(n, 24, n2);
        dateTimeData3.day = dateTimeData2.day + dateTimeData.day + n2;
        while (true) {
            n = this.maxDayInMonthFor(dateTimeData3.year, dateTimeData3.month);
            if (dateTimeData3.day < 1) {
                dateTimeData3.day += this.maxDayInMonthFor(dateTimeData3.year, dateTimeData3.month - 1);
                n2 = -1;
            } else {
                if (dateTimeData3.day <= n) break;
                dateTimeData3.day -= n;
                n2 = 1;
            }
            n = dateTimeData3.month + n2;
            dateTimeData3.month = this.modulo(n, 1, 13);
            dateTimeData3.year += this.fQuotient(n, 1, 13);
        }
        dateTimeData3.utc = 90;
        return dateTimeData3;
    }

    @Override
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
        if (n3 + 1 == n2) {
            throw new NumberFormatException("'" + string + "' has wrong format");
        }
        double d = Double.parseDouble(string.substring(n, n2));
        if (d == Double.POSITIVE_INFINITY) {
            throw new NumberFormatException("'" + string + "' has wrong format");
        }
        return d;
    }

    @Override
    protected String dateToString(AbstractDateTimeDV.DateTimeData dateTimeData) {
        StringBuffer stringBuffer = new StringBuffer(30);
        if (dateTimeData.year < 0 || dateTimeData.month < 0 || dateTimeData.day < 0 || dateTimeData.hour < 0 || dateTimeData.minute < 0 || dateTimeData.second < 0.0) {
            stringBuffer.append('-');
        }
        stringBuffer.append('P');
        stringBuffer.append((dateTimeData.year < 0 ? -1 : 1) * dateTimeData.year);
        stringBuffer.append('Y');
        stringBuffer.append((dateTimeData.month < 0 ? -1 : 1) * dateTimeData.month);
        stringBuffer.append('M');
        stringBuffer.append((dateTimeData.day < 0 ? -1 : 1) * dateTimeData.day);
        stringBuffer.append('D');
        stringBuffer.append('T');
        stringBuffer.append((dateTimeData.hour < 0 ? -1 : 1) * dateTimeData.hour);
        stringBuffer.append('H');
        stringBuffer.append((dateTimeData.minute < 0 ? -1 : 1) * dateTimeData.minute);
        stringBuffer.append('M');
        this.append2(stringBuffer, (double)(dateTimeData.second < 0.0 ? -1 : 1) * dateTimeData.second);
        stringBuffer.append('S');
        return stringBuffer.toString();
    }

    @Override
    protected Duration getDuration(AbstractDateTimeDV.DateTimeData dateTimeData) {
        int n = 1;
        if (dateTimeData.year < 0 || dateTimeData.month < 0 || dateTimeData.day < 0 || dateTimeData.hour < 0 || dateTimeData.minute < 0 || dateTimeData.second < 0.0) {
            n = -1;
        }
        return datatypeFactory.newDuration(n == 1, dateTimeData.year != Integer.MIN_VALUE ? BigInteger.valueOf(n * dateTimeData.year) : null, dateTimeData.month != Integer.MIN_VALUE ? BigInteger.valueOf(n * dateTimeData.month) : null, dateTimeData.day != Integer.MIN_VALUE ? BigInteger.valueOf(n * dateTimeData.day) : null, dateTimeData.hour != Integer.MIN_VALUE ? BigInteger.valueOf(n * dateTimeData.hour) : null, dateTimeData.minute != Integer.MIN_VALUE ? BigInteger.valueOf(n * dateTimeData.minute) : null, dateTimeData.second != -2.147483648E9 ? new BigDecimal(String.valueOf((double)n * dateTimeData.second)) : null);
    }
}

