/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine;

import java.io.Closeable;
import java.util.Iterator;
import org.hibernate.JDBCException;

public interface HibernateIterator
extends Iterator,
AutoCloseable,
Closeable {
    @Override
    public void close() throws JDBCException;
}

