/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal;

import com.hazelcast.cp.CPGroupId;

public interface RaftNodeLifecycleAwareService {
    public void onRaftGroupDestroyed(CPGroupId var1);

    public void onRaftNodeSteppedDown(CPGroupId var1);
}

