/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.collection;

import com.hazelcast.internal.util.collection.LongCursor;
import com.hazelcast.nio.Disposable;

public interface LongSet
extends Disposable {
    public boolean add(long var1);

    public boolean remove(long var1);

    public boolean contains(long var1);

    public long size();

    public boolean isEmpty();

    public void clear();

    public LongCursor cursor();
}

