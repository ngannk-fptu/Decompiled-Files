/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum ListMessageType {
    LIST_SIZE(1281),
    LIST_CONTAINS(1282),
    LIST_CONTAINSALL(1283),
    LIST_ADD(1284),
    LIST_REMOVE(1285),
    LIST_ADDALL(1286),
    LIST_COMPAREANDREMOVEALL(1287),
    LIST_COMPAREANDRETAINALL(1288),
    LIST_CLEAR(1289),
    LIST_GETALL(1290),
    LIST_ADDLISTENER(1291),
    LIST_REMOVELISTENER(1292),
    LIST_ISEMPTY(1293),
    LIST_ADDALLWITHINDEX(1294),
    LIST_GET(1295),
    LIST_SET(1296),
    LIST_ADDWITHINDEX(1297),
    LIST_REMOVEWITHINDEX(1298),
    LIST_LASTINDEXOF(1299),
    LIST_INDEXOF(1300),
    LIST_SUB(1301),
    LIST_ITERATOR(1302),
    LIST_LISTITERATOR(1303);

    private final int id;

    private ListMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

