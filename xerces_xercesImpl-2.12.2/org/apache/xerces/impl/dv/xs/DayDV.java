/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.xs.AbstractDateTimeDV;
import org.apache.xerces.impl.dv.xs.SchemaDateTimeException;

public class DayDV
extends AbstractDateTimeDV {
    private static final int DAY_SIZE = 5;

    @Override
    public Object getActualValue(String string, ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return this.parse(string);
        }
        catch (Exception exception) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{string, "gDay"});
        }
    }

    protected AbstractDateTimeDV.DateTimeData parse(String string) throws SchemaDateTimeException {
        AbstractDateTimeDV.DateTimeData dateTimeData = new AbstractDateTimeDV.DateTimeData(string, this);
        int n = string.length();
        if (string.charAt(0) != '-' || string.charAt(1) != '-' || string.charAt(2) != '-') {
            throw new SchemaDateTimeException("Error in day parsing");
        }
        dateTimeData.year = 2000;
        dateTimeData.month = 1;
        dateTimeData.day = this.parseInt(string, 3, 5);
        if (5 < n) {
            if (!this.isNextCharUTCSign(string, 5, n)) {
                throw new SchemaDateTimeException("Error in day parsing");
            }
            this.getTimeZone(string, dateTimeData, 5, n);
        }
        this.validateDateTime(dateTimeData);
        this.saveUnnormalized(dateTimeData);
        if (dateTimeData.utc != 0 && dateTimeData.utc != 90) {
            this.normalize(dateTimeData);
        }
        dateTimeData.position = 2;
        return dateTimeData;
    }

    @Override
    protected String dateToString(AbstractDateTimeDV.DateTimeData dateTimeData) {
        StringBuffer stringBuffer = new StringBuffer(6);
        stringBuffer.append('-');
        stringBuffer.append('-');
        stringBuffer.append('-');
        this.append(stringBuffer, dateTimeData.day, 2);
        this.append(stringBuffer, (char)dateTimeData.utc, 0);
        return stringBuffer.toString();
    }

    @Override
    protected XMLGregorianCalendar getXMLGregorianCalendar(AbstractDateTimeDV.DateTimeData dateTimeData) {
        return datatypeFactory.newXMLGregorianCalendar(Integer.MIN_VALUE, Integer.MIN_VALUE, dateTimeData.unNormDay, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, dateTimeData.hasTimeZone() ? dateTimeData.timezoneHr * 60 + dateTimeData.timezoneMin : Integer.MIN_VALUE);
    }
}

