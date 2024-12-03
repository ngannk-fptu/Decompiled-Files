/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.collection;

import com.hazelcast.internal.util.collection.LongLongCursor;

public interface Long2LongMap {
    public long get(long var1);

    public long put(long var1, long var3);

    public long putIfAbsent(long var1, long var3);

    public void putAll(Long2LongMap var1);

    public boolean replace(long var1, long var3, long var5);

    public long replace(long var1, long var3);

    public long remove(long var1);

    public boolean remove(long var1, long var3);

    public boolean containsKey(long var1);

    public long size();

    public boolean isEmpty();

    public void clear();

    public void dispose();

    public LongLongCursor cursor();
}

