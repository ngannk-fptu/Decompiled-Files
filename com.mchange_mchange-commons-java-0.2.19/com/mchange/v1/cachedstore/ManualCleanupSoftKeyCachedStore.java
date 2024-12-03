/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.cachedstore;

import com.mchange.v1.cachedstore.CachedStore;
import com.mchange.v1.cachedstore.CachedStoreException;
import com.mchange.v1.cachedstore.KeyTransformingCachedStore;
import com.mchange.v1.cachedstore.SoftKey;
import com.mchange.v1.cachedstore.Vacuumable;
import java.lang.ref.ReferenceQueue;

class ManualCleanupSoftKeyCachedStore
extends KeyTransformingCachedStore
implements Vacuumable {
    ReferenceQueue queue = new ReferenceQueue();

    public ManualCleanupSoftKeyCachedStore(CachedStore.Manager manager) {
        super(manager);
    }

    @Override
    protected Object toUserKey(Object object) {
        return ((SoftKey)object).get();
    }

    @Override
    protected Object toCacheFetchKey(Object object) {
        return new SoftKey(object, null);
    }

    @Override
    protected Object toCachePutKey(Object object) {
        return new SoftKey(object, this.queue);
    }

    @Override
    public void vacuum() throws CachedStoreException {
        SoftKey softKey;
        while ((softKey = (SoftKey)this.queue.poll()) != null) {
            this.removeByTransformedKey(softKey);
        }
    }
}

