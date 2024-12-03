/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import java.util.Iterator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class $UnmodifiableIterator<E>
implements Iterator<E> {
    @Override
    public final void remove() {
        throw new UnsupportedOperationException();
    }
}

