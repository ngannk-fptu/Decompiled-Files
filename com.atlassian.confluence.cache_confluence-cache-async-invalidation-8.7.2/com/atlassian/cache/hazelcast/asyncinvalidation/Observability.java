/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.ManagedCache
 */
package com.atlassian.cache.hazelcast.asyncinvalidation;

import com.atlassian.cache.ManagedCache;

public interface Observability {
    public void sequenceSnapshotInconsistent(ManagedCache var1);

    public void cacheInvalidationOutOfSequence(ManagedCache var1);
}

