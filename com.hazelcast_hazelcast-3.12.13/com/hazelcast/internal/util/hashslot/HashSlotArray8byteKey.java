/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.hashslot;

import com.hazelcast.internal.util.hashslot.HashSlotArray;
import com.hazelcast.internal.util.hashslot.HashSlotCursor8byteKey;
import com.hazelcast.internal.util.hashslot.SlotAssignmentResult;

public interface HashSlotArray8byteKey
extends HashSlotArray {
    public SlotAssignmentResult ensure(long var1);

    public long get(long var1);

    public boolean remove(long var1);

    public HashSlotCursor8byteKey cursor();
}

