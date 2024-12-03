/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import java.math.BigInteger;
import javax.xml.datatype.Duration;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.xs.AbstractDateTimeDV;
import org.apache.xerces.impl.dv.xs.DurationDV;

class YearMonthDurationDV
extends DurationDV {
    YearMonthDurationDV() {
    }

    @Override
    public Object getActualValue(String string, ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return this.parse(string, 1);
        }
        catch (Exception exception) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{string, "yearMonthDuration"});
        }
    }

    @Override
    protected Duration getDuration(AbstractDateTimeDV.DateTimeData dateTimeData) {
        int n = 1;
        if (dateTimeData.year < 0 || dateTimeData.month < 0) {
            n = -1;
        }
        return datatypeFactory.newDuration(n == 1, dateTimeData.year != Integer.MIN_VALUE ? BigInteger.valueOf(n * dateTimeData.year) : null, dateTimeData.month != Integer.MIN_VALUE ? BigInteger.valueOf(n * dateTimeData.month) : null, null, null, null, null);
    }
}

