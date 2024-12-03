/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.cache.hazelcast.asyncinvalidation;

public interface CacheInvalidator<K> {
    public void invalidateEntry(K var1);

    public void invalidateAllEntries();
}

