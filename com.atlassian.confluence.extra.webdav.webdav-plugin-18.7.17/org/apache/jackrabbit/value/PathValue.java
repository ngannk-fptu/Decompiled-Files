/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.value;

import java.math.BigDecimal;
import java.util.Calendar;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.value.BaseValue;

public class PathValue
extends BaseValue {
    public static final int TYPE = 8;
    private final String path;

    public static PathValue valueOf(String s) throws ValueFormatException {
        if (s != null) {
            return new PathValue(s);
        }
        throw new ValueFormatException("not a valid path format: " + s);
    }

    protected PathValue(String path) {
        super(8);
        this.path = path;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof PathValue) {
            PathValue other = (PathValue)obj;
            if (this.path == other.path) {
                return true;
            }
            if (this.path != null && other.path != null) {
                return this.path.equals(other.path);
            }
        }
        return false;
    }

    public int hashCode() {
        return 0;
    }

    @Override
    protected String getInternalString() throws ValueFormatException {
        if (this.path != null) {
            return this.path;
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

