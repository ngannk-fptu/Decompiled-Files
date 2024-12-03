/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking;

import com.hazelcast.internal.networking.Channel;

public interface ChannelErrorHandler {
    public void onError(Channel var1, Throwable var2);
}

