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

public class DecimalValue
extends BaseValue {
    public static final int TYPE = 12;
    private final BigDecimal number;

    public DecimalValue(BigDecimal number) {
        super(12);
        this.number = number;
    }

    public static DecimalValue valueOf(String s) throws ValueFormatException {
        try {
            return new DecimalValue(new BigDecimal(s));
        }
        catch (NumberFormatException e) {
            throw new ValueFormatException("not a valid decimal format: " + s, e);
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DecimalValue) {
            DecimalValue other = (DecimalValue)obj;
            if (this.number == other.number) {
                return true;
            }
            if (this.number != null && other.number != null) {
                return this.number.compareTo(other.number) == 0;
            }
        }
        return false;
    }

    public int hashCode() {
        return 0;
    }

    @Override
    protected String getInternalString() throws ValueFormatException {
        if (this.number != null) {
            return this.number.toString();
        }
        throw new ValueFormatException("empty value");
    }

    @Override
    public Calendar getDate() throws ValueFormatException, IllegalStateException, RepositoryException {
        if (this.number != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(this.number.longValue()));
            return cal;
        }
        throw new ValueFormatException("empty value");
    }

    @Override
    public long getLong() throws ValueFormatException, IllegalStateException, RepositoryException {
        if (this.number != null) {
            return this.number.longValue();
        }
        throw new ValueFormatException("empty value");
    }

    @Override
    public boolean getBoolean() throws ValueFormatException, IllegalStateException, RepositoryException {
        throw new ValueFormatException("conversion to boolean failed: inconvertible types");
    }

    @Override
    public double getDouble() throws ValueFormatException, IllegalStateException, RepositoryException {
        if (this.number != null) {
            return this.number.doubleValue();
        }
        throw new ValueFormatException("empty value");
    }

    @Override
    public BigDecimal getDecimal() throws ValueFormatException, IllegalStateException, RepositoryException {
        if (this.number != null) {
            return this.number;
        }
        throw new ValueFormatException("empty value");
    }
}

