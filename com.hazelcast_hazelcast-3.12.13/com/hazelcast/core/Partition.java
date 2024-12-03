/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.Member;

public interface Partition {
    public int getPartitionId();

    public Member getOwner();
}

