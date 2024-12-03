/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.hashslot;

public interface HashSlotCursor {
    public void reset();

    public boolean advance();

    public long valueAddress();
}

