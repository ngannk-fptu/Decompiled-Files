/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.entry;

import org.hibernate.cache.spi.entry.CacheEntryStructure;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public class UnstructuredCacheEntry
implements CacheEntryStructure {
    public static final UnstructuredCacheEntry INSTANCE = new UnstructuredCacheEntry();

    @Override
    public Object structure(Object item) {
        return item;
    }

    @Override
    public Object destructure(Object structured, SessionFactoryImplementor factory) {
        return structured;
    }

    private UnstructuredCacheEntry() {
    }
}

