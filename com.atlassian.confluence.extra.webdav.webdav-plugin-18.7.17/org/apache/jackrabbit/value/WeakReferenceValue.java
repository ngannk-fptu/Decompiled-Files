/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.value;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.UUID;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.value.BaseValue;

public class WeakReferenceValue
extends BaseValue {
    public static final int TYPE = 10;
    private final String uuid;

    public WeakReferenceValue(Node target) throws RepositoryException {
        super(10);
        try {
            this.uuid = target.getUUID();
        }
        catch (UnsupportedRepositoryOperationException ure) {
            throw new IllegalArgumentException("target is nonreferenceable.");
        }
    }

    public static WeakReferenceValue valueOf(String s) throws ValueFormatException {
        if (s != null) {
            try {
                UUID.fromString(s);
            }
            catch (IllegalArgumentException iae) {
                throw new ValueFormatException("not a valid UUID format: " + s);
            }
            return new WeakReferenceValue(s);
        }
        throw new ValueFormatException("not a valid UUID format: " + s);
    }

    protected WeakReferenceValue(String uuid) {
        super(10);
        this.uuid = uuid;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof WeakReferenceValue) {
            WeakReferenceValue other = (WeakReferenceValue)obj;
            if (this.uuid == other.uuid) {
                return true;
            }
            if (this.uuid != null && other.uuid != null) {
                return this.uuid.equals(other.uuid);
            }
        }
        return false;
    }

    public int hashCode() {
        return 0;
    }

    @Override
    protected String getInternalString() throws ValueFormatException {
        if (this.uuid != null) {
            return this.uuid;
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

