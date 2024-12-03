/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 */
package com.atlassian.cache;

import com.atlassian.annotations.PublicSpi;

@PublicSpi
public interface Supplier<T>
extends java.util.function.Supplier<T> {
    @Override
    public T get();
}

