/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.core.ICompletableFuture;

public interface InternalCompletableFuture<E>
extends ICompletableFuture<E> {
    public E join();

    public boolean complete(Object var1);
}

