/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.entry;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.hibernate.cache.spi.entry.CacheEntryStructure;
import org.hibernate.cache.spi.entry.CollectionCacheEntry;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public class StructuredCollectionCacheEntry
implements CacheEntryStructure {
    public static final StructuredCollectionCacheEntry INSTANCE = new StructuredCollectionCacheEntry();

    @Override
    public Object structure(Object item) {
        CollectionCacheEntry entry = (CollectionCacheEntry)item;
        return Arrays.asList(entry.getState());
    }

    @Override
    public Object destructure(Object structured, SessionFactoryImplementor factory) {
        List list = (List)structured;
        return new CollectionCacheEntry((Serializable)list.toArray(new Serializable[list.size()]));
    }

    private StructuredCollectionCacheEntry() {
    }
}

