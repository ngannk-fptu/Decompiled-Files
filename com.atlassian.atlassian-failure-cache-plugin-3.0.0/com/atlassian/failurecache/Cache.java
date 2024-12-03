/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.ListenableFuture
 */
package com.atlassian.failurecache;

import com.google.common.util.concurrent.ListenableFuture;

public interface Cache<V> {
    public void clear();

    public ListenableFuture<?> refresh();

    public Iterable<V> getValues();
}

