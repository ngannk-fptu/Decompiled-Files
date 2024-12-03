/*
 * Decompiled with CFR 0.152.
 */
package com.mysema.commons.lang;

import java.io.Closeable;
import java.util.Iterator;

public interface CloseableIterator<T>
extends Iterator<T>,
Closeable {
    @Override
    public void close();
}

