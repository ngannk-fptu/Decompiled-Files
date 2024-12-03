/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.LockAware;
import com.hazelcast.map.impl.LazyMapEntry;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.getters.Extractors;

public class LockAwareLazyMapEntry
extends LazyMapEntry
implements LockAware {
    private static final long serialVersionUID = 0L;
    private final transient Boolean locked;

    public LockAwareLazyMapEntry() {
        this.locked = null;
    }

    public LockAwareLazyMapEntry(Data key, Object value, InternalSerializationService serializationService, Extractors extractors, Boolean locked) {
        super(key, value, serializationService, extractors);
        this.locked = locked;
    }

    @Override
    public Boolean isLocked() {
        return this.locked;
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 137;
    }
}

