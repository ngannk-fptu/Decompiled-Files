/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.entry;

import java.io.Serializable;
import org.hibernate.cache.spi.entry.CacheEntry;
import org.hibernate.persister.entity.EntityPersister;

public class ReferenceCacheEntryImpl
implements CacheEntry {
    private final Object reference;
    private final EntityPersister subclassPersister;

    public ReferenceCacheEntryImpl(Object reference, EntityPersister subclassPersister) {
        this.reference = reference;
        this.subclassPersister = subclassPersister;
    }

    public Object getReference() {
        return this.reference;
    }

    @Override
    public boolean isReferenceEntry() {
        return true;
    }

    @Override
    public String getSubclass() {
        return this.subclassPersister.getEntityName();
    }

    public EntityPersister getSubclassPersister() {
        return this.subclassPersister;
    }

    @Override
    public Object getVersion() {
        return null;
    }

    @Override
    public Serializable[] getDisassembledState() {
        return null;
    }
}

