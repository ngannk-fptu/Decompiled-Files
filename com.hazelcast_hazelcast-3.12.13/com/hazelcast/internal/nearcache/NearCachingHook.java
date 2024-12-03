/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache;

import com.hazelcast.nio.serialization.Data;

public interface NearCachingHook<K, V> {
    public static final NearCachingHook EMPTY_HOOK = new NearCachingHook(){

        public void beforeRemoteCall(Object key, Data keyData, Object value, Data valueData) {
        }

        @Override
        public void onRemoteCallSuccess() {
        }

        @Override
        public void onRemoteCallFailure() {
        }
    };

    public void beforeRemoteCall(K var1, Data var2, V var3, Data var4);

    public void onRemoteCallSuccess();

    public void onRemoteCallFailure();
}

