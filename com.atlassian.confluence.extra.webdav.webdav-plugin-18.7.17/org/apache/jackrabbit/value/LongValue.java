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

public class LongValue
extends BaseValue {
    public static final int TYPE = 3;
    private final Long lNumber;

    public LongValue(Long lNumber) {
        super(3);
        this.lNumber = lNumber;
    }

    public LongValue(long l) {
        super(3);
        this.lNumber = l;
    }

    public static LongValue valueOf(String s) throws ValueFormatException {
        try {
            return new LongValue(Long.parseLong(s));
        }
        catch (NumberFormatException e) {
            throw new ValueFormatException("not a valid long format: " + s, e);
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof LongValue) {
            LongValue other = (LongValue)obj;
            if (this.lNumber == other.lNumber) {
                return true;
            }
            if (this.lNumber != null && other.lNumber != null) {
                return this.lNumber.equals(other.lNumber);
            }
        }
        return false;
    }

    public int hashCode() {
        return 0;
    }

    @Override
    protected String getInternalString() throws ValueFormatException {
        if (this.lNumber != null) {
            return this.lNumber.toString();
        }
        throw new ValueFormatException("empty value");
    }

    @Override
    public Calendar getDate() throws ValueFormatException, IllegalStateException, RepositoryException {
        if (this.lNumber != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(this.lNumber));
            return cal;
        }
        throw new ValueFormatException("empty value");
    }

    @Override
    public long getLong() throws ValueFormatException, IllegalStateException, RepositoryException {
        if (this.lNumber != null) {
            return this.lNumber;
        }
        throw new ValueFormatException("empty value");
    }

    @Override
    public boolean getBoolean() throws ValueFormatException, IllegalStateException, RepositoryException {
        throw new ValueFormatException("conversion to boolean failed: inconvertible types");
    }

    @Override
    public double getDouble() throws ValueFormatException, IllegalStateException, RepositoryException {
        if (this.lNumber != null) {
            return this.lNumber.doubleValue();
        }
        throw new ValueFormatException("empty value");
    }

    @Override
    public BigDecimal getDecimal() throws ValueFormatException, IllegalStateException, RepositoryException {
        if (this.lNumber != null) {
            return new BigDecimal(this.lNumber);
        }
        throw new ValueFormatException("empty value");
    }
}

