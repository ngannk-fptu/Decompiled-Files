/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.xs.AbstractDateTimeDV;
import org.apache.xerces.impl.dv.xs.SchemaDateTimeException;

public class MonthDV
extends AbstractDateTimeDV {
    @Override
    public Object getActualValue(String string, ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return this.parse(string);
        }
        catch (Exception exception) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{string, "gMonth"});
        }
    }

    protected AbstractDateTimeDV.DateTimeData parse(String string) throws SchemaDateTimeException {
        AbstractDateTimeDV.DateTimeData dateTimeData = new AbstractDateTimeDV.DateTimeData(string, this);
        int n = string.length();
        dateTimeData.year = 2000;
        dateTimeData.day = 1;
        if (string.charAt(0) != '-' || string.charAt(1) != '-') {
            throw new SchemaDateTimeException("Invalid format for gMonth: " + string);
        }
        int n2 = 4;
        dateTimeData.month = this.parseInt(string, 2, n2);
        if (string.length() >= n2 + 2 && string.charAt(n2) == '-' && string.charAt(n2 + 1) == '-') {
            n2 += 2;
        }
        if (n2 < n) {
            if (!this.isNextCharUTCSign(string, n2, n)) {
                throw new SchemaDateTimeException("Error in month parsing: " + string);
            }
            this.getTimeZone(string, dateTimeData, n2, n);
        }
        this.validateDateTime(dateTimeData);
        this.saveUnnormalized(dateTimeData);
        if (dateTimeData.utc != 0 && dateTimeData.utc != 90) {
            this.normalize(dateTimeData);
        }
        dateTimeData.position = 1;
        return dateTimeData;
    }

    @Override
    protected String dateToString(AbstractDateTimeDV.DateTimeData dateTimeData) {
        StringBuffer stringBuffer = new StringBuffer(5);
        stringBuffer.append('-');
        stringBuffer.append('-');
        this.append(stringBuffer, dateTimeData.month, 2);
        this.append(stringBuffer, (char)dateTimeData.utc, 0);
        return stringBuffer.toString();
    }

    @Override
    protected XMLGregorianCalendar getXMLGregorianCalendar(AbstractDateTimeDV.DateTimeData dateTimeData) {
        return datatypeFactory.newXMLGregorianCalendar(Integer.MIN_VALUE, dateTimeData.unNormMonth, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, dateTimeData.hasTimeZone() ? dateTimeData.timezoneHr * 60 + dateTimeData.timezoneMin : Integer.MIN_VALUE);
    }
}

