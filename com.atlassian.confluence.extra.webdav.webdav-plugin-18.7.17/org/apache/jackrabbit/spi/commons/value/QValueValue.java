/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.value;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import javax.jcr.Binary;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;

public final class QValueValue
implements Value {
    private final QValue qvalue;
    private InputStream stream = null;
    private final NamePathResolver resolver;

    public QValueValue(QValue qvalue, NamePathResolver resolver) {
        this.qvalue = qvalue;
        this.resolver = resolver;
    }

    public QValue getQValue() {
        return this.qvalue;
    }

    @Override
    public boolean getBoolean() throws RepositoryException {
        if (this.getType() == 1 || this.getType() == 2 || this.getType() == 6) {
            return Boolean.valueOf(this.qvalue.getString());
        }
        throw new ValueFormatException("incompatible type " + PropertyType.nameFromValue(this.qvalue.getType()));
    }

    @Override
    public BigDecimal getDecimal() throws ValueFormatException, IllegalStateException, RepositoryException {
        switch (this.getType()) {
            case 1: 
            case 3: 
            case 4: 
            case 5: 
            case 12: {
                return this.qvalue.getDecimal();
            }
        }
        throw new ValueFormatException("incompatible type " + PropertyType.nameFromValue(this.qvalue.getType()));
    }

    @Override
    public Binary getBinary() throws RepositoryException {
        if (this.getType() == 7 || this.getType() == 8) {
            final byte[] value = this.getString().getBytes(StandardCharsets.UTF_8);
            return new Binary(){

                @Override
                public int read(byte[] b, long position) {
                    if (position >= (long)value.length) {
                        return -1;
                    }
                    int p = (int)position;
                    int n = Math.min(b.length, value.length - p);
                    System.arraycopy(value, p, b, 0, n);
                    return n;
                }

                @Override
                public InputStream getStream() {
                    return new ByteArrayInputStream(value);
                }

                @Override
                public long getSize() {
                    return value.length;
                }

                @Override
                public void dispose() {
                }
            };
        }
        return this.qvalue.getBinary();
    }

    @Override
    public Calendar getDate() throws RepositoryException {
        return this.qvalue.getCalendar();
    }

    @Override
    public double getDouble() throws RepositoryException {
        return this.qvalue.getDouble();
    }

    @Override
    public long getLong() throws RepositoryException {
        return this.qvalue.getLong();
    }

    @Override
    public InputStream getStream() throws IllegalStateException, RepositoryException {
        if (this.stream == null) {
            this.stream = this.getType() == 7 || this.getType() == 8 ? new ByteArrayInputStream(this.getString().getBytes(StandardCharsets.UTF_8)) : this.qvalue.getStream();
        }
        return this.stream;
    }

    @Override
    public String getString() throws RepositoryException {
        if (this.getType() == 7) {
            return this.resolver.getJCRName(this.qvalue.getName());
        }
        if (this.getType() == 8) {
            return this.resolver.getJCRPath(this.qvalue.getPath());
        }
        return this.qvalue.getString();
    }

    @Override
    public int getType() {
        return this.qvalue.getType();
    }

    public boolean equals(Object obj) {
        if (obj instanceof QValueValue) {
            return this.qvalue.equals(((QValueValue)obj).qvalue);
        }
        return false;
    }

    public int hashCode() {
        return this.qvalue.hashCode();
    }
}

