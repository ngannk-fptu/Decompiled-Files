/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.quorum;

import com.hazelcast.core.Member;

public interface HeartbeatAware {
    public void onHeartbeat(Member var1, long var2);
}

