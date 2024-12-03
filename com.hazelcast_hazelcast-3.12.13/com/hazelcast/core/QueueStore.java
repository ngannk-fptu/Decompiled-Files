/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface QueueStore<T> {
    public void store(Long var1, T var2);

    public void storeAll(Map<Long, T> var1);

    public void delete(Long var1);

    public void deleteAll(Collection<Long> var1);

    public T load(Long var1);

    public Map<Long, T> loadAll(Collection<Long> var1);

    public Set<Long> loadAllKeys();
}

