/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import java.util.UUID;

public final class UUIDCodec {
    private static final int UUID_LONG_FIELD_COUNT = 2;
    private static final int UUID_DATA_SIZE = 16;

    private UUIDCodec() {
    }

    public static UUID decode(ClientMessage clientMessage) {
        return new UUID(clientMessage.getLong(), clientMessage.getLong());
    }

    public static void encode(UUID uuid, ClientMessage clientMessage) {
        clientMessage.set(uuid.getMostSignificantBits());
        clientMessage.set(uuid.getLeastSignificantBits());
    }

    public static int calculateDataSize(UUID uuid) {
        return 16;
    }
}

