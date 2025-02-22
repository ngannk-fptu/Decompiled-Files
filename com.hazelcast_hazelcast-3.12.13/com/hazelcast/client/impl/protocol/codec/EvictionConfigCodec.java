/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.EvictionConfigHolder;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.serialization.Data;

public final class EvictionConfigCodec {
    private EvictionConfigCodec() {
    }

    public static EvictionConfigHolder decode(ClientMessage clientMessage) {
        int size = clientMessage.getInt();
        String maxSizePolicy = clientMessage.getStringUtf8();
        String evictionPolicy = clientMessage.getStringUtf8();
        boolean isNullComparatorClassName = clientMessage.getBoolean();
        String comparatorClassName = null;
        if (!isNullComparatorClassName) {
            comparatorClassName = clientMessage.getStringUtf8();
        }
        boolean isNullComparator = clientMessage.getBoolean();
        Data comparator = null;
        if (!isNullComparator) {
            comparator = clientMessage.getData();
        }
        return new EvictionConfigHolder(size, maxSizePolicy, evictionPolicy, comparatorClassName, comparator);
    }

    public static void encode(EvictionConfigHolder holder, ClientMessage clientMessage) {
        clientMessage.set(holder.getSize()).set(holder.getMaxSizePolicy()).set(holder.getEvictionPolicy());
        boolean isNullComparatorClassName = holder.getComparatorClassName() == null;
        clientMessage.set(isNullComparatorClassName);
        if (!isNullComparatorClassName) {
            clientMessage.set(holder.getComparatorClassName());
        }
        boolean isNullComparator = holder.getComparator() == null;
        clientMessage.set(isNullComparator);
        if (!isNullComparator) {
            clientMessage.set(holder.getComparator());
        }
    }

    public static int calculateDataSize(EvictionConfigHolder holder) {
        int dataSize = 6;
        dataSize += ParameterUtil.calculateDataSize(holder.getMaxSizePolicy());
        dataSize += ParameterUtil.calculateDataSize(holder.getEvictionPolicy());
        if (holder.getComparator() != null) {
            dataSize += ParameterUtil.calculateDataSize(holder.getComparator());
        }
        if (holder.getComparatorClassName() != null) {
            dataSize += ParameterUtil.calculateDataSize(holder.getComparatorClassName());
        }
        return dataSize;
    }
}

