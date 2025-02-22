/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum CacheMessageType {
    CACHE_ADDENTRYLISTENER(5377),
    CACHE_ADDINVALIDATIONLISTENER(5378),
    CACHE_CLEAR(5379),
    CACHE_REMOVEALLKEYS(5380),
    CACHE_REMOVEALL(5381),
    CACHE_CONTAINSKEY(5382),
    CACHE_CREATECONFIG(5383),
    CACHE_DESTROY(5384),
    CACHE_ENTRYPROCESSOR(5385),
    CACHE_GETALL(5386),
    CACHE_GETANDREMOVE(5387),
    CACHE_GETANDREPLACE(5388),
    CACHE_GETCONFIG(5389),
    CACHE_GET(5390),
    CACHE_ITERATE(5391),
    CACHE_LISTENERREGISTRATION(5392),
    CACHE_LOADALL(5393),
    CACHE_MANAGEMENTCONFIG(5394),
    CACHE_PUTIFABSENT(5395),
    CACHE_PUT(5396),
    CACHE_REMOVEENTRYLISTENER(5397),
    CACHE_REMOVEINVALIDATIONLISTENER(5398),
    CACHE_REMOVE(5399),
    CACHE_REPLACE(5400),
    CACHE_SIZE(5401),
    CACHE_ADDPARTITIONLOSTLISTENER(5402),
    CACHE_REMOVEPARTITIONLOSTLISTENER(5403),
    CACHE_PUTALL(5404),
    CACHE_ITERATEENTRIES(5405),
    CACHE_ADDNEARCACHEINVALIDATIONLISTENER(5406),
    CACHE_FETCHNEARCACHEINVALIDATIONMETADATA(5407),
    CACHE_ASSIGNANDGETUUIDS(5408),
    CACHE_EVENTJOURNALSUBSCRIBE(5409),
    CACHE_EVENTJOURNALREAD(5410),
    CACHE_SETEXPIRYPOLICY(5411);

    private final int id;

    private CacheMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

