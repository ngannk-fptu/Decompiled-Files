/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import java.math.BigInteger;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.xs.AbstractDateTimeDV;
import org.apache.xerces.impl.dv.xs.SchemaDateTimeException;

public class DateTimeDV
extends AbstractDateTimeDV {
    @Override
    public Object getActualValue(String string, ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return this.parse(string);
        }
        catch (Exception exception) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{string, "dateTime"});
        }
    }

    protected AbstractDateTimeDV.DateTimeData parse(String string) throws SchemaDateTimeException {
        AbstractDateTimeDV.DateTimeData dateTimeData = new AbstractDateTimeDV.DateTimeData(string, this);
        int n = string.length();
        int n2 = this.indexOf(string, 0, n, 'T');
        int n3 = this.getDate(string, 0, n2, dateTimeData);
        this.getTime(string, n2 + 1, n, dateTimeData);
        if (n3 != n2) {
            throw new RuntimeException(string + " is an invalid dateTime dataype value. " + "Invalid character(s) seprating date and time values.");
        }
        this.validateDateTime(dateTimeData);
        this.saveUnnormalized(dateTimeData);
        if (dateTimeData.utc != 0 && dateTimeData.utc != 90) {
            this.normalize(dateTimeData);
        }
        return dateTimeData;
    }

    @Override
    protected XMLGregorianCalendar getXMLGregorianCalendar(AbstractDateTimeDV.DateTimeData dateTimeData) {
        return datatypeFactory.newXMLGregorianCalendar(BigInteger.valueOf(dateTimeData.unNormYear), dateTimeData.unNormMonth, dateTimeData.unNormDay, dateTimeData.unNormHour, dateTimeData.unNormMinute, (int)dateTimeData.unNormSecond, dateTimeData.unNormSecond != 0.0 ? this.getFractionalSecondsAsBigDecimal(dateTimeData) : null, dateTimeData.hasTimeZone() ? dateTimeData.timezoneHr * 60 + dateTimeData.timezoneMin : Integer.MIN_VALUE);
    }
}

