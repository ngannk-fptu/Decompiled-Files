/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.cachedstore;

import com.mchange.v1.cachedstore.CachedStore;
import com.mchange.v1.cachedstore.ValueTransformingCachedStore;
import java.lang.ref.SoftReference;

class SoftReferenceCachedStore
extends ValueTransformingCachedStore {
    public SoftReferenceCachedStore(CachedStore.Manager manager) {
        super(manager);
    }

    @Override
    protected Object toUserValue(Object object) {
        return object == null ? null : ((SoftReference)object).get();
    }

    @Override
    protected Object toCacheValue(Object object) {
        return object == null ? null : new SoftReference<Object>(object);
    }
}

