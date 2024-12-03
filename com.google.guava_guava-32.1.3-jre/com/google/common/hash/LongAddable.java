/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.hash;

import com.google.common.hash.ElementTypesAreNonnullByDefault;

@ElementTypesAreNonnullByDefault
interface LongAddable {
    public void increment();

    public void add(long var1);

    public long sum();
}

