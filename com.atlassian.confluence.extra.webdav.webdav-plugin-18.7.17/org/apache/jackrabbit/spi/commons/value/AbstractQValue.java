/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.value;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.TimeZone;
import javax.jcr.Binary;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.commons.value.AbstractQValueFactory;
import org.apache.jackrabbit.util.ISO8601;

public abstract class AbstractQValue
implements QValue,
Serializable {
    private static final long serialVersionUID = 6976433831974695272L;
    protected final Object val;
    protected final int type;

    protected AbstractQValue(Object value, int type) {
        if (value == null) {
            throw new IllegalArgumentException("null value");
        }
        this.val = value;
        this.type = type;
    }

    protected AbstractQValue(String value, int type) {
        if (value == null) {
            throw new IllegalArgumentException("null value");
        }
        if (type != 1 && type != 5 && type != 9 && type != 10) {
            throw new IllegalArgumentException();
        }
        this.val = value;
        this.type = type;
    }

    protected AbstractQValue(Long value) {
        this(value, 3);
    }

    protected AbstractQValue(Double value) {
        this(value, 4);
    }

    protected AbstractQValue(Boolean value) {
        this(value, 6);
    }

    protected AbstractQValue(Calendar value) {
        this.val = ISO8601.format(value);
        this.type = 5;
    }

    protected AbstractQValue(Name value) {
        this(value, 7);
    }

    protected AbstractQValue(Path value) {
        this(value, 8);
    }

    protected AbstractQValue(BigDecimal value) {
        this(value, 12);
    }

    protected AbstractQValue(URI value) {
        this(value, 11);
    }

    @Override
    public int getType() {
        return this.type;
    }

    @Override
    public long getLength() throws RepositoryException {
        return this.getString().length();
    }

    @Override
    public Name getName() throws RepositoryException {
        if (this.type == 7) {
            return (Name)this.val;
        }
        try {
            return AbstractQValueFactory.NAME_FACTORY.create(this.getString());
        }
        catch (IllegalArgumentException e) {
            throw new ValueFormatException("not a valid Name value: " + this.getString(), e);
        }
    }

    @Override
    public Calendar getCalendar() throws RepositoryException {
        if (this.type == 4) {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+00:00"));
            cal.setTimeInMillis(((Double)this.val).longValue());
            return cal;
        }
        if (this.type == 3) {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+00:00"));
            cal.setTimeInMillis((Long)this.val);
            return cal;
        }
        if (this.type == 12) {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+00:00"));
            cal.setTimeInMillis(((BigDecimal)this.val).longValue());
            return cal;
        }
        Calendar cal = ISO8601.parse(this.getString());
        if (cal == null) {
            throw new ValueFormatException("not a date string: " + this.getString());
        }
        return cal;
    }

    @Override
    public BigDecimal getDecimal() throws RepositoryException {
        if (this.type == 12) {
            return (BigDecimal)this.val;
        }
        if (this.type == 4) {
            return new BigDecimal((Double)this.val);
        }
        if (this.type == 3) {
            return new BigDecimal((Long)this.val);
        }
        if (this.type == 5) {
            return new BigDecimal(((Calendar)this.val).getTimeInMillis());
        }
        try {
            return new BigDecimal(this.getString());
        }
        catch (NumberFormatException e) {
            throw new ValueFormatException("not a valid decimal string: " + this.getString(), e);
        }
    }

    @Override
    public URI getURI() throws RepositoryException {
        if (this.type == 11) {
            return (URI)this.val;
        }
        try {
            return URI.create(this.getString());
        }
        catch (IllegalArgumentException e) {
            throw new ValueFormatException("not a valid uri: " + this.getString(), e);
        }
    }

    @Override
    public double getDouble() throws RepositoryException {
        if (this.type == 4) {
            return (Double)this.val;
        }
        if (this.type == 3) {
            return ((Long)this.val).doubleValue();
        }
        if (this.type == 5) {
            return this.getCalendar().getTimeInMillis();
        }
        if (this.type == 12) {
            return ((BigDecimal)this.val).doubleValue();
        }
        try {
            return Double.parseDouble(this.getString());
        }
        catch (NumberFormatException ex) {
            throw new ValueFormatException("not a double: " + this.getString(), ex);
        }
    }

    @Override
    public long getLong() throws RepositoryException {
        if (this.type == 3) {
            return (Long)this.val;
        }
        if (this.type == 4) {
            return ((Double)this.val).longValue();
        }
        if (this.type == 12) {
            return ((BigDecimal)this.val).longValue();
        }
        if (this.type == 5) {
            return this.getCalendar().getTimeInMillis();
        }
        try {
            return Long.parseLong(this.getString());
        }
        catch (NumberFormatException ex) {
            throw new ValueFormatException("not a long: " + this.getString(), ex);
        }
    }

    @Override
    public boolean getBoolean() throws RepositoryException {
        if (this.type == 6) {
            return (Boolean)this.val;
        }
        return Boolean.valueOf(this.getString());
    }

    @Override
    public Path getPath() throws RepositoryException {
        if (this.type == 8) {
            return (Path)this.val;
        }
        try {
            return AbstractQValueFactory.PATH_FACTORY.create(this.getString());
        }
        catch (IllegalArgumentException e) {
            throw new ValueFormatException("not a valid Path: " + this.getString(), e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getString() throws RepositoryException {
        if (this.type == 2) {
            String string;
            InputStream stream = this.getStream();
            try {
                InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                StringWriter writer = new StringWriter();
                char[] buffer = new char[1024];
                int n = reader.read(buffer);
                while (n != -1) {
                    ((Writer)writer).write(buffer, 0, n);
                    n = reader.read(buffer);
                }
                string = ((Object)writer).toString();
            }
            catch (Throwable throwable) {
                try {
                    stream.close();
                    throw throwable;
                }
                catch (IOException e) {
                    throw new RepositoryException("conversion from stream to string failed", e);
                }
            }
            stream.close();
            return string;
        }
        if (this.type == 5) {
            return (String)this.val;
        }
        return this.val.toString();
    }

    @Override
    public Binary getBinary() throws RepositoryException {
        return new Binary(){

            @Override
            public InputStream getStream() throws RepositoryException {
                return AbstractQValue.this.getStream();
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public int read(byte[] b, long position) throws IOException, RepositoryException {
                try (InputStream in = this.getStream();){
                    long skipped;
                    for (long skip = position; skip > 0L; skip -= skipped) {
                        skipped = in.skip(skip);
                        if (skipped > 0L) continue;
                        int n = -1;
                        return n;
                    }
                    int n = in.read(b);
                    return n;
                }
            }

            @Override
            public long getSize() throws RepositoryException {
                return AbstractQValue.this.getLength();
            }

            @Override
            public void dispose() {
            }
        };
    }

    @Override
    public void discard() {
    }

    public String toString() {
        if (this.type == 5) {
            return (String)this.val;
        }
        return this.val.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof AbstractQValue) {
            AbstractQValue other = (AbstractQValue)obj;
            if (this.type != other.type) {
                return false;
            }
            return this.val.equals(other.val);
        }
        return false;
    }

    public int hashCode() {
        return this.val.hashCode();
    }
}

