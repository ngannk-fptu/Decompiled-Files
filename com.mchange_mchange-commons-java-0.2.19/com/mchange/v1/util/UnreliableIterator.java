/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import com.mchange.v1.util.UIterator;
import com.mchange.v1.util.UnreliableIteratorException;

public interface UnreliableIterator
extends UIterator {
    @Override
    public boolean hasNext() throws UnreliableIteratorException;

    @Override
    public Object next() throws UnreliableIteratorException;

    @Override
    public void remove() throws UnreliableIteratorException;

    @Override
    public void close() throws UnreliableIteratorException;
}

