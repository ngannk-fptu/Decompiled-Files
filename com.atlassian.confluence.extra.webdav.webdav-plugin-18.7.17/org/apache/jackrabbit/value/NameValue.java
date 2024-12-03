/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.value;

import java.math.BigDecimal;
import java.util.Calendar;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.value.BaseValue;

public class NameValue
extends BaseValue {
    public static final int TYPE = 7;
    private final String name;

    public static NameValue valueOf(String s) throws ValueFormatException {
        if (s != null) {
            return new NameValue(s);
        }
        throw new ValueFormatException("not a valid name format: " + s);
    }

    protected NameValue(String name) {
        super(7);
        this.name = name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof NameValue) {
            NameValue other = (NameValue)obj;
            if (this.name == other.name) {
                return true;
            }
            if (this.name != null && other.name != null) {
                return this.name.equals(other.name);
            }
        }
        return false;
    }

    public int hashCode() {
        return 0;
    }

    @Override
    protected String getInternalString() throws ValueFormatException {
        if (this.name != null) {
            return this.name;
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
        throw new ValueFormatException("conversion to boolean failed: inconvertible types");
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

