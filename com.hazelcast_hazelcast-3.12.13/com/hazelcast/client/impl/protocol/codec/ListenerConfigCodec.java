/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.ListenerConfigHolder;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.serialization.Data;

public final class ListenerConfigCodec {
    private ListenerConfigCodec() {
    }

    public static ListenerConfigHolder decode(ClientMessage clientMessage) {
        boolean isNullClassName;
        byte listenerType = clientMessage.getByte();
        boolean isNullListenerImplementation = clientMessage.getBoolean();
        Data implementation = null;
        String className = null;
        if (!isNullListenerImplementation) {
            implementation = clientMessage.getData();
        }
        if (!(isNullClassName = clientMessage.getBoolean())) {
            className = clientMessage.getStringUtf8();
        }
        boolean local = clientMessage.getBoolean();
        boolean includeValue = clientMessage.getBoolean();
        if (className == null) {
            return new ListenerConfigHolder((int)listenerType, implementation, includeValue, local);
        }
        return new ListenerConfigHolder((int)listenerType, className, includeValue, local);
    }

    public static void encode(ListenerConfigHolder listenerConfigHolder, ClientMessage clientMessage) {
        clientMessage.set((byte)listenerConfigHolder.getListenerType());
        boolean isNullImplementation = listenerConfigHolder.getListenerImplementation() == null;
        clientMessage.set(isNullImplementation);
        if (!isNullImplementation) {
            clientMessage.set(listenerConfigHolder.getListenerImplementation());
        }
        boolean isNullClassName = listenerConfigHolder.getClassName() == null;
        clientMessage.set(isNullClassName);
        if (!isNullClassName) {
            clientMessage.set(listenerConfigHolder.getClassName());
        }
        clientMessage.set(listenerConfigHolder.isLocal()).set(listenerConfigHolder.isIncludeValue());
    }

    public static int calculateDataSize(ListenerConfigHolder listenerConfig) {
        boolean hasImplementation = listenerConfig.getListenerImplementation() != null;
        int dataSize = 4;
        dataSize = hasImplementation ? (dataSize += ParameterUtil.calculateDataSize(listenerConfig.getListenerImplementation())) : (dataSize += ParameterUtil.calculateDataSize(listenerConfig.getClassName()));
        return dataSize;
    }
}

