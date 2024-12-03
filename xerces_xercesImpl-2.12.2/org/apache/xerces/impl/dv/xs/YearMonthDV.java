/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.xs.AbstractDateTimeDV;
import org.apache.xerces.impl.dv.xs.SchemaDateTimeException;

public class YearMonthDV
extends AbstractDateTimeDV {
    @Override
    public Object getActualValue(String string, ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return this.parse(string);
        }
        catch (Exception exception) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{string, "gYearMonth"});
        }
    }

    protected AbstractDateTimeDV.DateTimeData parse(String string) throws SchemaDateTimeException {
        AbstractDateTimeDV.DateTimeData dateTimeData = new AbstractDateTimeDV.DateTimeData(string, this);
        int n = string.length();
        int n2 = this.getYearMonth(string, 0, n, dateTimeData);
        dateTimeData.day = 1;
        this.parseTimeZone(string, n2, n, dateTimeData);
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
        StringBuffer stringBuffer = new StringBuffer(25);
        this.append(stringBuffer, dateTimeData.year, 4);
        stringBuffer.append('-');
        this.append(stringBuffer, dateTimeData.month, 2);
        this.append(stringBuffer, (char)dateTimeData.utc, 0);
        return stringBuffer.toString();
    }

    @Override
    protected XMLGregorianCalendar getXMLGregorianCalendar(AbstractDateTimeDV.DateTimeData dateTimeData) {
        return datatypeFactory.newXMLGregorianCalendar(dateTimeData.unNormYear, dateTimeData.unNormMonth, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, dateTimeData.hasTimeZone() ? dateTimeData.timezoneHr * 60 + dateTimeData.timezoneMin : Integer.MIN_VALUE);
    }
}

