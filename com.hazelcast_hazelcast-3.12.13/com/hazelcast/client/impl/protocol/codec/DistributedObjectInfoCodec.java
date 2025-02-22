/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.client.DistributedObjectInfo;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;

public final class DistributedObjectInfoCodec {
    private DistributedObjectInfoCodec() {
    }

    public static DistributedObjectInfo decode(ClientMessage clientMessage) {
        String serviceName = clientMessage.getStringUtf8();
        String name = clientMessage.getStringUtf8();
        return new DistributedObjectInfo(serviceName, name);
    }

    public static void encode(DistributedObjectInfo info, ClientMessage clientMessage) {
        clientMessage.set(info.getServiceName()).set(info.getName());
    }

    public static int calculateDataSize(DistributedObjectInfo info) {
        return ParameterUtil.calculateDataSize(info.getServiceName()) + ParameterUtil.calculateDataSize(info.getName());
    }
}

