/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.hashslot;

import com.hazelcast.internal.util.hashslot.HashSlotArray;
import com.hazelcast.internal.util.hashslot.HashSlotCursor16byteKey;
import com.hazelcast.internal.util.hashslot.SlotAssignmentResult;

public interface HashSlotArray16byteKey
extends HashSlotArray {
    public SlotAssignmentResult ensure(long var1, long var3);

    public long get(long var1, long var3);

    public boolean remove(long var1, long var3);

    public HashSlotCursor16byteKey cursor();
}

