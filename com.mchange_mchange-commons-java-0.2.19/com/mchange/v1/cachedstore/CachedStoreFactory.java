/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.cachedstore;

import com.mchange.v1.cachedstore.CachedStore;
import com.mchange.v1.cachedstore.ManualCleanupSoftKeyCachedStore;
import com.mchange.v1.cachedstore.NoCacheCachedStore;
import com.mchange.v1.cachedstore.NoCacheWritableCachedStore;
import com.mchange.v1.cachedstore.NoCleanupCachedStore;
import com.mchange.v1.cachedstore.SimpleWritableCachedStore;
import com.mchange.v1.cachedstore.SoftReferenceCachedStore;
import com.mchange.v1.cachedstore.TweakableCachedStore;
import com.mchange.v1.cachedstore.WritableCachedStore;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public final class CachedStoreFactory {
    public static TweakableCachedStore createNoCleanupCachedStore(CachedStore.Manager manager) {
        return new NoCleanupCachedStore(manager);
    }

    public static TweakableCachedStore createSoftValueCachedStore(CachedStore.Manager manager) {
        return new SoftReferenceCachedStore(manager);
    }

    public static TweakableCachedStore createSynchronousCleanupSoftKeyCachedStore(CachedStore.Manager manager) {
        final ManualCleanupSoftKeyCachedStore manualCleanupSoftKeyCachedStore = new ManualCleanupSoftKeyCachedStore(manager);
        InvocationHandler invocationHandler = new InvocationHandler(){

            @Override
            public Object invoke(Object object, Method method, Object[] objectArray) throws Throwable {
                manualCleanupSoftKeyCachedStore.vacuum();
                return method.invoke((Object)manualCleanupSoftKeyCachedStore, objectArray);
            }
        };
        return (TweakableCachedStore)Proxy.newProxyInstance(CachedStoreFactory.class.getClassLoader(), new Class[]{TweakableCachedStore.class}, invocationHandler);
    }

    public static TweakableCachedStore createNoCacheCachedStore(CachedStore.Manager manager) {
        return new NoCacheCachedStore(manager);
    }

    public static WritableCachedStore createDefaultWritableCachedStore(WritableCachedStore.Manager manager) {
        TweakableCachedStore tweakableCachedStore = CachedStoreFactory.createSynchronousCleanupSoftKeyCachedStore(manager);
        return new SimpleWritableCachedStore(tweakableCachedStore, manager);
    }

    public static WritableCachedStore cacheWritesOnlyWritableCachedStore(WritableCachedStore.Manager manager) {
        TweakableCachedStore tweakableCachedStore = CachedStoreFactory.createNoCacheCachedStore(manager);
        return new SimpleWritableCachedStore(tweakableCachedStore, manager);
    }

    public static WritableCachedStore createNoCacheWritableCachedStore(WritableCachedStore.Manager manager) {
        return new NoCacheWritableCachedStore(manager);
    }
}

