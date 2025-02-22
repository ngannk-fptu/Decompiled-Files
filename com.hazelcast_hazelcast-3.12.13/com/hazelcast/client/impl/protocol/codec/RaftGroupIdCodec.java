/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.cp.internal.RaftGroupId;

public final class RaftGroupIdCodec {
    private RaftGroupIdCodec() {
    }

    public static RaftGroupId decode(ClientMessage clientMessage) {
        String name = clientMessage.getStringUtf8();
        long seed = clientMessage.getLong();
        long commitIndex = clientMessage.getLong();
        return new RaftGroupId(name, seed, commitIndex);
    }

    public static void encode(RaftGroupId groupId, ClientMessage clientMessage) {
        clientMessage.set(groupId.name()).set(groupId.seed()).set(groupId.id());
    }

    public static int calculateDataSize(RaftGroupId groupId) {
        return ParameterUtil.calculateDataSize(groupId.name()) + 16;
    }
}

