/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.concurrent.LazyReference
 *  io.atlassian.util.concurrent.LazyReference
 */
package com.atlassian.confluence.util.misc;

import io.atlassian.util.concurrent.LazyReference;

public class LazyReferenceAdapter<T>
extends LazyReference<T> {
    private final com.atlassian.util.concurrent.LazyReference<T> origin;

    public LazyReferenceAdapter(com.atlassian.util.concurrent.LazyReference<T> origin) {
        this.origin = origin;
    }

    protected T create() throws Exception {
        return (T)this.origin.get();
    }
}

