/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.value;

import java.math.BigDecimal;
import java.util.Calendar;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.value.BaseValue;

public class BooleanValue
extends BaseValue {
    public static final int TYPE = 6;
    private final Boolean bool;

    public BooleanValue(Boolean bool) {
        super(6);
        this.bool = bool;
    }

    public BooleanValue(boolean bool) {
        super(6);
        this.bool = bool;
    }

    public static BooleanValue valueOf(String s) {
        return new BooleanValue(Boolean.valueOf(s));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof BooleanValue) {
            BooleanValue other = (BooleanValue)obj;
            if (this.bool == other.bool) {
                return true;
            }
            if (this.bool != null && other.bool != null) {
                return this.bool.equals(other.bool);
            }
        }
        return false;
    }

    public int hashCode() {
        return 0;
    }

    @Override
    protected String getInternalString() throws ValueFormatException {
        if (this.bool != null) {
            return this.bool.toString();
        }
        throw new ValueFormatException("empty value");
    }

    @Override
    public Calendar getDate() throws ValueFormatException, IllegalStateException, RepositoryException {
        throw new ValueFormatException("conversion to date failed: inconvertible types");
    }

    @Override
    public long getLong() throws ValueFormatException, IllegalStateException, RepositoryException {
        throw new ValueFormatException("conversion to long failed: inconvertible types");
    }

    @Override
    public boolean getBoolean() throws ValueFormatException, IllegalStateException, RepositoryException {
        if (this.bool != null) {
            return this.bool;
        }
        throw new ValueFormatException("empty value");
    }

    @Override
    public double getDouble() throws ValueFormatException, IllegalStateException, RepositoryException {
        throw new ValueFormatException("conversion to double failed: inconvertible types");
    }

    @Override
    public BigDecimal getDecimal() throws ValueFormatException, IllegalStateException, RepositoryException {
        throw new ValueFormatException("conversion to Decimal failed: inconvertible types");
    }
}

