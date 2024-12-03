/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 */
package com.atlassian.cache.hazelcast.asyncinvalidation;

import com.atlassian.annotations.ExperimentalSpi;

@ExperimentalSpi
public interface CacheReplicator<K, V> {
    public void replicate(K var1, V var2);
}

