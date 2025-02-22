/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.map.impl.SimpleEntryView;
import com.hazelcast.nio.serialization.Data;

public final class EntryViewCodec {
    private static final int DATA_SIZE_FACTOR = 10;

    private EntryViewCodec() {
    }

    public static SimpleEntryView<Data, Data> decode(ClientMessage clientMessage) {
        SimpleEntryView<Data, Data> dataEntryView = new SimpleEntryView<Data, Data>();
        dataEntryView.setKey(clientMessage.getData());
        dataEntryView.setValue(clientMessage.getData());
        dataEntryView.setCost(clientMessage.getLong());
        dataEntryView.setCreationTime(clientMessage.getLong());
        dataEntryView.setExpirationTime(clientMessage.getLong());
        dataEntryView.setHits(clientMessage.getLong());
        dataEntryView.setLastAccessTime(clientMessage.getLong());
        dataEntryView.setLastStoredTime(clientMessage.getLong());
        dataEntryView.setLastUpdateTime(clientMessage.getLong());
        dataEntryView.setVersion(clientMessage.getLong());
        dataEntryView.setEvictionCriteriaNumber(clientMessage.getLong());
        dataEntryView.setTtl(clientMessage.getLong());
        return dataEntryView;
    }

    public static void encode(SimpleEntryView<Data, Data> dataEntryView, ClientMessage clientMessage) {
        Data key = dataEntryView.getKey();
        Data value = dataEntryView.getValue();
        long cost = dataEntryView.getCost();
        long creationTime = dataEntryView.getCreationTime();
        long expirationTime = dataEntryView.getExpirationTime();
        long hits = dataEntryView.getHits();
        long lastAccessTime = dataEntryView.getLastAccessTime();
        long lastStoredTime = dataEntryView.getLastStoredTime();
        long lastUpdateTime = dataEntryView.getLastUpdateTime();
        long version = dataEntryView.getVersion();
        long ttl = dataEntryView.getTtl();
        long evictionCriteriaNumber = dataEntryView.getEvictionCriteriaNumber();
        clientMessage.set(key).set(value).set(cost).set(creationTime).set(expirationTime).set(hits).set(lastAccessTime).set(lastStoredTime).set(lastUpdateTime).set(version).set(evictionCriteriaNumber).set(ttl);
    }

    public static int calculateDataSize(SimpleEntryView<Data, Data> entryView) {
        Data key = entryView.getKey();
        Data value = entryView.getValue();
        return ParameterUtil.calculateDataSize(key) + ParameterUtil.calculateDataSize(value) + 80;
    }
}

