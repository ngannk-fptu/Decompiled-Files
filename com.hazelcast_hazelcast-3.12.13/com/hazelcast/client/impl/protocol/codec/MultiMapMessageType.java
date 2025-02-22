/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum MultiMapMessageType {
    MULTIMAP_PUT(513),
    MULTIMAP_GET(514),
    MULTIMAP_REMOVE(515),
    MULTIMAP_KEYSET(516),
    MULTIMAP_VALUES(517),
    MULTIMAP_ENTRYSET(518),
    MULTIMAP_CONTAINSKEY(519),
    MULTIMAP_CONTAINSVALUE(520),
    MULTIMAP_CONTAINSENTRY(521),
    MULTIMAP_SIZE(522),
    MULTIMAP_CLEAR(523),
    MULTIMAP_VALUECOUNT(524),
    MULTIMAP_ADDENTRYLISTENERTOKEY(525),
    MULTIMAP_ADDENTRYLISTENER(526),
    MULTIMAP_REMOVEENTRYLISTENER(527),
    MULTIMAP_LOCK(528),
    MULTIMAP_TRYLOCK(529),
    MULTIMAP_ISLOCKED(530),
    MULTIMAP_UNLOCK(531),
    MULTIMAP_FORCEUNLOCK(532),
    MULTIMAP_REMOVEENTRY(533),
    MULTIMAP_DELETE(534);

    private final int id;

    private MultiMapMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

