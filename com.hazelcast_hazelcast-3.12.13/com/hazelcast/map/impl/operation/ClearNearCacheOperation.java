/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.spi.impl.MutatingOperation;

@Deprecated
public class ClearNearCacheOperation
extends MapOperation
implements MutatingOperation {
    public ClearNearCacheOperation() {
    }

    public ClearNearCacheOperation(String mapName) {
        super(mapName);
    }

    @Override
    public void run() {
    }

    @Override
    public Object getResponse() {
        return Boolean.TRUE;
    }

    @Override
    public int getId() {
        return 27;
    }
}

