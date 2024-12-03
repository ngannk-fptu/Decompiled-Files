/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.entry;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.cache.spi.entry.CacheEntry;
import org.hibernate.cache.spi.entry.CacheEntryStructure;
import org.hibernate.cache.spi.entry.StandardCacheEntryImpl;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.EntityPersister;

public class StructuredCacheEntry
implements CacheEntryStructure {
    public static final String SUBCLASS_KEY = "_subclass";
    public static final String VERSION_KEY = "_version";
    private EntityPersister persister;

    public StructuredCacheEntry(EntityPersister persister) {
        this.persister = persister;
    }

    @Override
    public Object destructure(Object structured, SessionFactoryImplementor factory) {
        Map map = (Map)structured;
        String subclass = (String)map.get(SUBCLASS_KEY);
        Object version = map.get(VERSION_KEY);
        EntityPersister subclassPersister = factory.getEntityPersister(subclass);
        String[] names = subclassPersister.getPropertyNames();
        Serializable[] disassembledState = new Serializable[names.length];
        for (int i = 0; i < names.length; ++i) {
            disassembledState[i] = (Serializable)map.get(names[i]);
        }
        return new StandardCacheEntryImpl(disassembledState, subclass, version);
    }

    @Override
    public Object structure(Object item) {
        CacheEntry entry = (CacheEntry)item;
        String[] names = this.persister.getPropertyNames();
        HashMap<String, Object> map = new HashMap<String, Object>(names.length + 3, 1.0f);
        map.put(SUBCLASS_KEY, entry.getSubclass());
        map.put(VERSION_KEY, entry.getVersion());
        for (int i = 0; i < names.length; ++i) {
            map.put(names[i], entry.getDisassembledState()[i]);
        }
        return map;
    }
}

