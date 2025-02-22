/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.config.WanReplicationRef;
import java.util.ArrayList;

public final class WanReplicationRefCodec {
    private WanReplicationRefCodec() {
    }

    public static WanReplicationRef decode(ClientMessage clientMessage) {
        String name = clientMessage.getStringUtf8();
        String mergePolicy = clientMessage.getStringUtf8();
        boolean republishingEnabled = clientMessage.getBoolean();
        boolean isNullFilters = clientMessage.getBoolean();
        ArrayList<String> filters = null;
        if (!isNullFilters) {
            int filtersCount = clientMessage.getInt();
            filters = new ArrayList<String>(filtersCount);
            for (int i = 0; i < filtersCount; ++i) {
                filters.add(clientMessage.getStringUtf8());
            }
        }
        return new WanReplicationRef(name, mergePolicy, filters, republishingEnabled);
    }

    public static void encode(WanReplicationRef ref, ClientMessage clientMessage) {
        boolean isNullFilters = ref.getFilters() == null;
        clientMessage.set(ref.getName()).set(ref.getMergePolicy()).set(ref.isRepublishingEnabled()).set(isNullFilters);
        if (!isNullFilters) {
            clientMessage.set(ref.getFilters().size());
            for (String filter : ref.getFilters()) {
                clientMessage.set(filter);
            }
        }
    }

    public static int calculateDataSize(WanReplicationRef ref) {
        boolean hasFilters;
        int dataSize = 2;
        dataSize += ParameterUtil.calculateDataSize(ref.getName());
        dataSize += ParameterUtil.calculateDataSize(ref.getMergePolicy());
        boolean bl = hasFilters = ref.getFilters() != null && !ref.getFilters().isEmpty();
        if (hasFilters) {
            dataSize += 4;
            for (String filter : ref.getFilters()) {
                dataSize += ParameterUtil.calculateDataSize(filter);
            }
        }
        return dataSize;
    }
}

