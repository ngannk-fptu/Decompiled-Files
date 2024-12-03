/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking;

import com.hazelcast.internal.networking.ChannelOption;

public interface ChannelOptions {
    public <T> ChannelOptions setOption(ChannelOption<T> var1, T var2);

    public <T> T getOption(ChannelOption<T> var1);
}

