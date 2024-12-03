/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.value;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.value.BaseValue;

public class URIValue
extends BaseValue {
    public static final int TYPE = 11;
    private final URI uri;

    public static URIValue valueOf(String s) throws ValueFormatException {
        if (s != null) {
            try {
                return new URIValue(new URI(s));
            }
            catch (URISyntaxException e) {
                throw new ValueFormatException(e.getMessage());
            }
        }
        throw new ValueFormatException("not a valid uri format: " + s);
    }

    public URIValue(URI uri) {
        super(11);
        this.uri = uri;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof URIValue) {
            URIValue other = (URIValue)obj;
            if (this.uri == other.uri) {
                return true;
            }
            if (this.uri != null && other.uri != null) {
                return this.uri.equals(other.uri);
            }
        }
        return false;
    }

    public int hashCode() {
        return 0;
    }

    @Override
    protected String getInternalString() throws ValueFormatException {
        if (this.uri != null) {
            return this.uri.toString();
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

