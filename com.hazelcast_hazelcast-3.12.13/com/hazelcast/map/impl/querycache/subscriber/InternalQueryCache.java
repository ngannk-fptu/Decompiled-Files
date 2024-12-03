/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.IMap;
import com.hazelcast.map.QueryCache;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.getters.Extractors;

public interface InternalQueryCache<K, V>
extends QueryCache<K, V> {
    public void set(K var1, V var2, EntryEventType var3);

    public void prepopulate(K var1, V var2);

    public void delete(Object var1, EntryEventType var2);

    public int removeEntriesOf(int var1);

    public IMap<K, V> getDelegate();

    public Indexes getIndexes();

    public void clear();

    public void setPublisherListenerId(String var1);

    public String getPublisherListenerId();

    public String getCacheId();

    public boolean reachedMaxCapacity();

    public Extractors getExtractors();

    public void recreate();
}

