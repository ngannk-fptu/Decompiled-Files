/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.hashslot;

import com.hazelcast.nio.Disposable;

public interface HashSlotArray
extends Disposable {
    public long address();

    public void gotoAddress(long var1);

    public long gotoNew();

    public long size();

    public long capacity();

    public long expansionThreshold();

    public void clear();

    public boolean trimToSize();
}

