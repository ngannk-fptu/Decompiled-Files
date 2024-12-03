/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.entry;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.cache.spi.entry.CacheEntryStructure;
import org.hibernate.cache.spi.entry.CollectionCacheEntry;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public class StructuredMapCacheEntry
implements CacheEntryStructure {
    public static final StructuredMapCacheEntry INSTANCE = new StructuredMapCacheEntry();

    @Override
    public Object structure(Object item) {
        CollectionCacheEntry entry = (CollectionCacheEntry)item;
        Serializable[] state = entry.getState();
        HashMap<Serializable, Serializable> map = new HashMap<Serializable, Serializable>(state.length);
        int i = 0;
        while (i < state.length) {
            map.put(state[i++], state[i++]);
        }
        return map;
    }

    @Override
    public Object destructure(Object structured, SessionFactoryImplementor factory) {
        Map map = (Map)structured;
        Serializable[] state = new Serializable[map.size() * 2];
        int i = 0;
        for (Map.Entry me : map.entrySet()) {
            state[i++] = (Serializable)me.getKey();
            state[i++] = (Serializable)me.getValue();
        }
        return new CollectionCacheEntry((Serializable)state);
    }

    private StructuredMapCacheEntry() {
    }
}

