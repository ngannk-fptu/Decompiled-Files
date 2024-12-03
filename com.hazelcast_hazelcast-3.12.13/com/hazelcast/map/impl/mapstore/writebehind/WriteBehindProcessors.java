/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore.writebehind;

import com.hazelcast.map.impl.mapstore.MapStoreContext;
import com.hazelcast.map.impl.mapstore.writebehind.DefaultWriteBehindProcessor;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindProcessor;

public final class WriteBehindProcessors {
    private WriteBehindProcessors() {
    }

    public static WriteBehindProcessor createWriteBehindProcessor(MapStoreContext mapStoreContext) {
        return new DefaultWriteBehindProcessor(mapStoreContext);
    }
}

