/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.cache.ElementTypesAreNonnullByDefault;

@ElementTypesAreNonnullByDefault
@GwtCompatible
interface LongAddable {
    public void increment();

    public void add(long var1);

    public long sum();
}

