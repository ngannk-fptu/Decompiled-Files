/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Calendar;
import javax.jcr.Binary;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;

public interface QValue {
    public static final QValue[] EMPTY_ARRAY = new QValue[0];

    public int getType();

    public long getLength() throws RepositoryException;

    public String getString() throws RepositoryException;

    public InputStream getStream() throws RepositoryException;

    public Binary getBinary() throws RepositoryException;

    public Calendar getCalendar() throws RepositoryException;

    public BigDecimal getDecimal() throws RepositoryException;

    public double getDouble() throws RepositoryException;

    public long getLong() throws RepositoryException;

    public boolean getBoolean() throws RepositoryException;

    public Name getName() throws RepositoryException;

    public Path getPath() throws RepositoryException;

    public URI getURI() throws RepositoryException;

    public void discard();
}

