/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.cachedstore;

import com.mchange.lang.PotentiallySecondary;
import com.mchange.v1.cachedstore.CacheFlushException;
import com.mchange.v1.cachedstore.CachedStore;
import com.mchange.v1.cachedstore.CachedStoreException;
import com.mchange.v1.cachedstore.TweakableCachedStore;
import com.mchange.v1.cachedstore.WritableCachedStore;
import com.mchange.v1.lang.Synchronizer;

public final class CachedStoreUtils {
    static final boolean DEBUG = true;

    public static CachedStore synchronizedCachedStore(CachedStore cachedStore) {
        return (CachedStore)Synchronizer.createSynchronizedWrapper(cachedStore);
    }

    public static TweakableCachedStore synchronizedTweakableCachedStore(TweakableCachedStore tweakableCachedStore) {
        return (TweakableCachedStore)Synchronizer.createSynchronizedWrapper(tweakableCachedStore);
    }

    public static WritableCachedStore synchronizedWritableCachedStore(WritableCachedStore writableCachedStore) {
        return (WritableCachedStore)Synchronizer.createSynchronizedWrapper(writableCachedStore);
    }

    public static CachedStore untweakableCachedStore(final TweakableCachedStore tweakableCachedStore) {
        return new CachedStore(){

            @Override
            public Object find(Object object) throws CachedStoreException {
                return tweakableCachedStore.find(object);
            }

            @Override
            public void reset() throws CachedStoreException {
                tweakableCachedStore.reset();
            }
        };
    }

    static CachedStoreException toCachedStoreException(Throwable throwable) {
        Throwable throwable2;
        throwable.printStackTrace();
        if (throwable instanceof CachedStoreException) {
            return (CachedStoreException)throwable;
        }
        if (throwable instanceof PotentiallySecondary && (throwable2 = ((PotentiallySecondary)((Object)throwable)).getNestedThrowable()) instanceof CachedStoreException) {
            return (CachedStoreException)throwable2;
        }
        return new CachedStoreException(throwable);
    }

    static CacheFlushException toCacheFlushException(Throwable throwable) {
        throwable.printStackTrace();
        if (throwable instanceof CacheFlushException) {
            return (CacheFlushException)throwable;
        }
        return new CacheFlushException(throwable);
    }

    private CachedStoreUtils() {
    }
}

