/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.failurecache.updates;

import com.atlassian.failurecache.MutableCache;
import com.atlassian.failurecache.updates.MutateCacheAction;

public class NoOpAction<K, V>
implements MutateCacheAction<K, V> {
    private static final NoOpAction INSTANCE = new NoOpAction();

    public static <K, V> MutateCacheAction<K, V> instance() {
        return INSTANCE;
    }

    @Override
    public void apply(MutableCache<K, V> cache) {
    }
}

