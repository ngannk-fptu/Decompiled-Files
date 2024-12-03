/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache;

public final class ListenerRegistrationHelper {
    private static final String PLACE_HOLDER = "::::";

    private ListenerRegistrationHelper() {
    }

    public static String generateListenerName(String mapName, String cacheId) {
        return mapName + PLACE_HOLDER + cacheId;
    }
}

