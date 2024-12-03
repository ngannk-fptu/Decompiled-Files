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
import org.apache.xerces.impl.dv.xs.DurationDV;

class DayTimeDurationDV
extends DurationDV {
    DayTimeDurationDV() {
    }

    @Override
    public Object getActualValue(String string, ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return this.parse(string, 2);
        }
        catch (Exception exception) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{string, "dayTimeDuration"});
        }
    }

    @Override
    protected Duration getDuration(AbstractDateTimeDV.DateTimeData dateTimeData) {
        int n = 1;
        if (dateTimeData.day < 0 || dateTimeData.hour < 0 || dateTimeData.minute < 0 || dateTimeData.second < 0.0) {
            n = -1;
        }
        return datatypeFactory.newDuration(n == 1, null, null, dateTimeData.day != Integer.MIN_VALUE ? BigInteger.valueOf(n * dateTimeData.day) : null, dateTimeData.hour != Integer.MIN_VALUE ? BigInteger.valueOf(n * dateTimeData.hour) : null, dateTimeData.minute != Integer.MIN_VALUE ? BigInteger.valueOf(n * dateTimeData.minute) : null, dateTimeData.second != -2.147483648E9 ? new BigDecimal(String.valueOf((double)n * dateTimeData.second)) : null);
    }
}

