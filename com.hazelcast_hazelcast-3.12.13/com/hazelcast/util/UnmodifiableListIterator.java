/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.util.UnmodifiableIterator;
import java.util.ListIterator;

public abstract class UnmodifiableListIterator<E>
extends UnmodifiableIterator<E>
implements ListIterator<E> {
    @Override
    public final void set(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void add(E e) {
        throw new UnsupportedOperationException();
    }
}

