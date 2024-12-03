/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.value;

import java.math.BigDecimal;
import java.util.Calendar;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.util.ISO8601;
import org.apache.jackrabbit.value.BaseValue;

public class DateValue
extends BaseValue {
    public static final int TYPE = 5;
    private final Calendar date;

    public DateValue(Calendar date) throws IllegalArgumentException {
        super(5);
        this.date = date;
        ISO8601.getYear(date);
    }

    public static DateValue valueOf(String s) throws ValueFormatException {
        Calendar cal = ISO8601.parse(s);
        if (cal != null) {
            return new DateValue(cal);
        }
        throw new ValueFormatException("not a valid date format: " + s);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DateValue) {
            DateValue other = (DateValue)obj;
            if (this.date == other.date) {
                return true;
            }
            if (this.date != null && other.date != null) {
                return ISO8601.format(this.date).equals(ISO8601.format(other.date));
            }
        }
        return false;
    }

    public int hashCode() {
        return 0;
    }

    @Override
    protected String getInternalString() throws ValueFormatException {
        if (this.date != null) {
            return ISO8601.format(this.date);
        }
        throw new ValueFormatException("empty value");
    }

    @Override
    public Calendar getDate() throws ValueFormatException, IllegalStateException, RepositoryException {
        if (this.date != null) {
            return (Calendar)this.date.clone();
        }
        throw new ValueFormatException("empty value");
    }

    @Override
    public long getLong() throws ValueFormatException, IllegalStateException, RepositoryException {
        if (this.date != null) {
            return this.date.getTimeInMillis();
        }
        throw new ValueFormatException("empty value");
    }

    @Override
    public boolean getBoolean() throws ValueFormatException, IllegalStateException, RepositoryException {
        if (this.date != null) {
            throw new ValueFormatException("cannot convert date to boolean");
        }
        throw new ValueFormatException("empty value");
    }

    @Override
    public double getDouble() throws ValueFormatException, IllegalStateException, RepositoryException {
        if (this.date != null) {
            long ms = this.date.getTimeInMillis();
            if ((double)ms <= Double.MAX_VALUE) {
                return ms;
            }
            throw new ValueFormatException("conversion from date to double failed: inconvertible types");
        }
        throw new ValueFormatException("empty value");
    }

    @Override
    public BigDecimal getDecimal() throws ValueFormatException, IllegalStateException, RepositoryException {
        if (this.date != null) {
            return new BigDecimal(this.date.getTimeInMillis());
        }
        throw new ValueFormatException("empty value");
    }
}

