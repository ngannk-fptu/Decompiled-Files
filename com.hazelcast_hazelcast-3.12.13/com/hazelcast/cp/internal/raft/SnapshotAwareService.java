/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft;

import com.hazelcast.cp.CPGroupId;

public interface SnapshotAwareService<T> {
    public T takeSnapshot(CPGroupId var1, long var2);

    public void restoreSnapshot(CPGroupId var1, long var2, T var4);
}

