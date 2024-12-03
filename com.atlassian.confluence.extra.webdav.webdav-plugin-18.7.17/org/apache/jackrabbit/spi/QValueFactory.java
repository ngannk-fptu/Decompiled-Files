/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Calendar;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.QPropertyDefinition;
import org.apache.jackrabbit.spi.QValue;

public interface QValueFactory {
    public QValue create(String var1, int var2) throws ValueFormatException, RepositoryException;

    public QValue create(Calendar var1) throws RepositoryException;

    public QValue create(double var1) throws RepositoryException;

    public QValue create(long var1) throws RepositoryException;

    public QValue create(boolean var1) throws RepositoryException;

    public QValue create(Name var1) throws RepositoryException;

    public QValue create(Path var1) throws RepositoryException;

    public QValue create(BigDecimal var1) throws RepositoryException;

    public QValue create(URI var1) throws RepositoryException;

    public QValue create(byte[] var1) throws RepositoryException;

    public QValue create(InputStream var1) throws RepositoryException, IOException;

    public QValue create(File var1) throws RepositoryException, IOException;

    public QValue[] computeAutoValues(QPropertyDefinition var1) throws RepositoryException;
}

