/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.confluence.impl.cache.hibernate.HibernateManagedCacheSupplier
 *  io.atlassian.fugue.Option
 */
package com.atlassian.confluence.impl.cache.hazelcast;

import com.atlassian.cache.ManagedCache;
import com.atlassian.confluence.impl.cache.hibernate.HibernateManagedCacheSupplier;
import io.atlassian.fugue.Option;
import java.util.Collection;
import java.util.Collections;

@Deprecated(forRemoval=true)
public interface HibernateManagedRegionCacheLookup
extends HibernateManagedCacheSupplier {
    @Deprecated(forRemoval=true)
    public static HibernateManagedRegionCacheLookup empty() {
        return new HibernateManagedRegionCacheLookup(){

            public Collection<ManagedCache> getAllManagedCaches() {
                return Collections.emptyList();
            }

            public Option<ManagedCache> getManagedCache(String name) {
                return Option.none();
            }
        };
    }
}

