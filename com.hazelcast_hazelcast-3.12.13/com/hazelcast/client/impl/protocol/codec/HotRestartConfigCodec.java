/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.config.HotRestartConfig;

public final class HotRestartConfigCodec {
    private HotRestartConfigCodec() {
    }

    public static HotRestartConfig decode(ClientMessage clientMessage) {
        boolean enabled = clientMessage.getBoolean();
        boolean fsync = clientMessage.getBoolean();
        HotRestartConfig config = new HotRestartConfig();
        config.setEnabled(enabled);
        config.setFsync(fsync);
        return config;
    }

    public static void encode(HotRestartConfig config, ClientMessage clientMessage) {
        clientMessage.set(config.isEnabled()).set(config.isFsync());
    }

    public static int calculateDataSize(HotRestartConfig config) {
        return 2;
    }
}

