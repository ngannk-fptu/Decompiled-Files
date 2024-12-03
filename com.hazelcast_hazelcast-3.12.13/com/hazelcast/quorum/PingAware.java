/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.quorum;

import com.hazelcast.core.Member;

public interface PingAware {
    public void onPingLost(Member var1);

    public void onPingRestored(Member var1);
}

