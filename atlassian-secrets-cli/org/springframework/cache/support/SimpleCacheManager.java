/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.support;

import java.util.Collection;
import java.util.Collections;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

public class SimpleCacheManager
extends AbstractCacheManager {
    private Collection<? extends Cache> caches = Collections.emptySet();

    public void setCaches(Collection<? extends Cache> caches) {
        this.caches = caches;
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return this.caches;
    }
}

