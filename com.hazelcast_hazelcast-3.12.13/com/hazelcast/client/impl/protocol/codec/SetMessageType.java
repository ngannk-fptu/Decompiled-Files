/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum SetMessageType {
    SET_SIZE(1537),
    SET_CONTAINS(1538),
    SET_CONTAINSALL(1539),
    SET_ADD(1540),
    SET_REMOVE(1541),
    SET_ADDALL(1542),
    SET_COMPAREANDREMOVEALL(1543),
    SET_COMPAREANDRETAINALL(1544),
    SET_CLEAR(1545),
    SET_GETALL(1546),
    SET_ADDLISTENER(1547),
    SET_REMOVELISTENER(1548),
    SET_ISEMPTY(1549);

    private final int id;

    private SetMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

