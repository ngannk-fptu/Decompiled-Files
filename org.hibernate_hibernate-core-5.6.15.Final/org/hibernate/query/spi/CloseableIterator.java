/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.spi;

import java.util.Iterator;
import org.hibernate.Incubating;

@Incubating
public interface CloseableIterator<T>
extends Iterator<T>,
AutoCloseable {
    @Override
    public void close();
}

