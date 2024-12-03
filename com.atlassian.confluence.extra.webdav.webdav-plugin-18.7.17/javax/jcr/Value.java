/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import javax.jcr.Binary;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

public interface Value {
    public String getString() throws ValueFormatException, IllegalStateException, RepositoryException;

    public InputStream getStream() throws RepositoryException;

    public Binary getBinary() throws RepositoryException;

    public long getLong() throws ValueFormatException, RepositoryException;

    public double getDouble() throws ValueFormatException, RepositoryException;

    public BigDecimal getDecimal() throws ValueFormatException, RepositoryException;

    public Calendar getDate() throws ValueFormatException, RepositoryException;

    public boolean getBoolean() throws ValueFormatException, RepositoryException;

    public int getType();
}

