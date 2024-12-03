/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.value;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.value.BaseValue;

public class DoubleValue
extends BaseValue {
    public static final int TYPE = 4;
    private final Double dblNumber;

    public DoubleValue(Double dblNumber) {
        super(4);
        this.dblNumber = dblNumber;
    }

    public DoubleValue(double dbl) {
        super(4);
        this.dblNumber = dbl;
    }

    public static DoubleValue valueOf(String s) throws ValueFormatException {
        try {
            return new DoubleValue(Double.parseDouble(s));
        }
        catch (NumberFormatException e) {
            throw new ValueFormatException("not a valid double format: " + s, e);
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DoubleValue) {
            DoubleValue other = (DoubleValue)obj;
            if (this.dblNumber == other.dblNumber) {
                return true;
            }
            if (this.dblNumber != null && other.dblNumber != null) {
                return this.dblNumber.equals(other.dblNumber);
            }
        }
        return false;
    }

    public int hashCode() {
        return 0;
    }

    @Override
    protected String getInternalString() throws ValueFormatException {
        if (this.dblNumber != null) {
            return this.dblNumber.toString();
        }
        throw new ValueFormatException("empty value");
    }

    @Override
    public Calendar getDate() throws ValueFormatException, IllegalStateException, RepositoryException {
        if (this.dblNumber != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(this.dblNumber.longValue()));
            return cal;
        }
        throw new ValueFormatException("empty value");
    }

    @Override
    public long getLong() throws ValueFormatException, IllegalStateException, RepositoryException {
        if (this.dblNumber != null) {
            return this.dblNumber.longValue();
        }
        throw new ValueFormatException("empty value");
    }

    @Override
    public boolean getBoolean() throws ValueFormatException, IllegalStateException, RepositoryException {
        throw new ValueFormatException("conversion to boolean failed: inconvertible types");
    }

    @Override
    public double getDouble() throws ValueFormatException, IllegalStateException, RepositoryException {
        if (this.dblNumber != null) {
            return this.dblNumber;
        }
        throw new ValueFormatException("empty value");
    }

    @Override
    public BigDecimal getDecimal() throws ValueFormatException, IllegalStateException, RepositoryException {
        if (this.dblNumber != null) {
            return new BigDecimal(this.dblNumber);
        }
        throw new ValueFormatException("empty value");
    }
}

