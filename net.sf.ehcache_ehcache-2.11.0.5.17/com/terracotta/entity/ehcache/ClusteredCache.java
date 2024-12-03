/*
 * Decompiled with CFR 0.152.
 */
package com.terracotta.entity.ehcache;

import com.terracotta.entity.ClusteredEntity;
import com.terracotta.entity.ehcache.ClusteredCacheConfiguration;

public interface ClusteredCache
extends ClusteredEntity<ClusteredCacheConfiguration> {
    public String getName();

    public long getSize();
}

