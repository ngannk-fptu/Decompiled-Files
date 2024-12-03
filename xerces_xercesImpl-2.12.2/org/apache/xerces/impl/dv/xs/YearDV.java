/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.xs.AbstractDateTimeDV;
import org.apache.xerces.impl.dv.xs.SchemaDateTimeException;

public class YearDV
extends AbstractDateTimeDV {
    @Override
    public Object getActualValue(String string, ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return this.parse(string);
        }
        catch (Exception exception) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{string, "gYear"});
        }
    }

    protected AbstractDateTimeDV.DateTimeData parse(String string) throws SchemaDateTimeException {
        int n;
        int n2;
        AbstractDateTimeDV.DateTimeData dateTimeData = new AbstractDateTimeDV.DateTimeData(string, this);
        int n3 = string.length();
        int n4 = 0;
        if (string.charAt(0) == '-') {
            n4 = 1;
        }
        if ((n2 = ((n = this.findUTCSign(string, n4, n3)) == -1 ? n3 : n) - n4) < 4) {
            throw new RuntimeException("Year must have 'CCYY' format");
        }
        if (n2 > 4 && string.charAt(n4) == '0') {
            throw new RuntimeException("Leading zeros are required if the year value would otherwise have fewer than four digits; otherwise they are forbidden");
        }
        if (n == -1) {
            dateTimeData.year = this.parseIntYear(string, n3);
        } else {
            dateTimeData.year = this.parseIntYear(string, n);
            this.getTimeZone(string, dateTimeData, n, n3);
        }
        dateTimeData.month = 1;
        dateTimeData.day = 1;
        this.validateDateTime(dateTimeData);
        this.saveUnnormalized(dateTimeData);
        if (dateTimeData.utc != 0 && dateTimeData.utc != 90) {
            this.normalize(dateTimeData);
        }
        dateTimeData.position = 0;
        return dateTimeData;
    }

    @Override
    protected String dateToString(AbstractDateTimeDV.DateTimeData dateTimeData) {
        StringBuffer stringBuffer = new StringBuffer(5);
        this.append(stringBuffer, dateTimeData.year, 4);
        this.append(stringBuffer, (char)dateTimeData.utc, 0);
        return stringBuffer.toString();
    }

    @Override
    protected XMLGregorianCalendar getXMLGregorianCalendar(AbstractDateTimeDV.DateTimeData dateTimeData) {
        return datatypeFactory.newXMLGregorianCalendar(dateTimeData.unNormYear, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, dateTimeData.hasTimeZone() ? dateTimeData.timezoneHr * 60 + dateTimeData.timezoneMin : Integer.MIN_VALUE);
    }
}

