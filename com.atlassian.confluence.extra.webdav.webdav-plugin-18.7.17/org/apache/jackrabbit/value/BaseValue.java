/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.value;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Calendar;
import javax.jcr.Binary;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.util.ISO8601;
import org.apache.jackrabbit.value.BinaryImpl;

public abstract class BaseValue
implements Value {
    protected static final String DEFAULT_ENCODING = "UTF-8";
    protected final int type;
    protected InputStream stream = null;

    BaseValue(int type) {
        this.type = type;
    }

    protected abstract String getInternalString() throws ValueFormatException, RepositoryException;

    @Override
    public int getType() {
        return this.type;
    }

    @Override
    public Calendar getDate() throws ValueFormatException, IllegalStateException, RepositoryException {
        Calendar cal = ISO8601.parse(this.getInternalString());
        if (cal == null) {
            throw new ValueFormatException("not a valid date format");
        }
        return cal;
    }

    @Override
    public long getLong() throws ValueFormatException, IllegalStateException, RepositoryException {
        try {
            return Long.parseLong(this.getInternalString());
        }
        catch (NumberFormatException e) {
            throw new ValueFormatException("conversion to long failed", e);
        }
    }

    @Override
    public boolean getBoolean() throws ValueFormatException, IllegalStateException, RepositoryException {
        return Boolean.valueOf(this.getInternalString());
    }

    @Override
    public double getDouble() throws ValueFormatException, IllegalStateException, RepositoryException {
        try {
            return Double.parseDouble(this.getInternalString());
        }
        catch (NumberFormatException e) {
            throw new ValueFormatException("conversion to double failed", e);
        }
    }

    @Override
    public BigDecimal getDecimal() throws ValueFormatException, IllegalStateException, RepositoryException {
        try {
            return new BigDecimal(this.getInternalString());
        }
        catch (NumberFormatException e) {
            throw new ValueFormatException("conversion to Decimal failed", e);
        }
    }

    @Override
    public InputStream getStream() throws IllegalStateException, RepositoryException {
        if (this.stream != null) {
            return this.stream;
        }
        try {
            this.stream = new ByteArrayInputStream(this.getInternalString().getBytes(DEFAULT_ENCODING));
            return this.stream;
        }
        catch (UnsupportedEncodingException e) {
            throw new RepositoryException("UTF-8 not supported on this platform", e);
        }
    }

    @Override
    public Binary getBinary() throws ValueFormatException, IllegalStateException, RepositoryException {
        try {
            return new BinaryImpl(new ByteArrayInputStream(this.getInternalString().getBytes(DEFAULT_ENCODING)));
        }
        catch (UnsupportedEncodingException e) {
            throw new RepositoryException("UTF-8 not supported on this platform", e);
        }
        catch (IOException e) {
            throw new RepositoryException("failed to create Binary instance", e);
        }
    }

    @Override
    public String getString() throws ValueFormatException, IllegalStateException, RepositoryException {
        return this.getInternalString();
    }
}

