/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import java.util.Iterator;

public abstract class UnmodifiableIterator<E>
implements Iterator<E> {
    @Override
    public final void remove() {
        throw new UnsupportedOperationException();
    }
}

