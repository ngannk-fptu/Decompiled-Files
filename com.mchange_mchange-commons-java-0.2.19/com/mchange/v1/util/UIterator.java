/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import com.mchange.v1.util.ClosableResource;

public interface UIterator
extends ClosableResource {
    public boolean hasNext() throws Exception;

    public Object next() throws Exception;

    public void remove() throws Exception;

    @Override
    public void close() throws Exception;
}

