/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum QueueMessageType {
    QUEUE_OFFER(769),
    QUEUE_PUT(770),
    QUEUE_SIZE(771),
    QUEUE_REMOVE(772),
    QUEUE_POLL(773),
    QUEUE_TAKE(774),
    QUEUE_PEEK(775),
    QUEUE_ITERATOR(776),
    QUEUE_DRAINTO(777),
    QUEUE_DRAINTOMAXSIZE(778),
    QUEUE_CONTAINS(779),
    QUEUE_CONTAINSALL(780),
    QUEUE_COMPAREANDREMOVEALL(781),
    QUEUE_COMPAREANDRETAINALL(782),
    QUEUE_CLEAR(783),
    QUEUE_ADDALL(784),
    QUEUE_ADDLISTENER(785),
    QUEUE_REMOVELISTENER(786),
    QUEUE_REMAININGCAPACITY(787),
    QUEUE_ISEMPTY(788);

    private final int id;

    private QueueMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

