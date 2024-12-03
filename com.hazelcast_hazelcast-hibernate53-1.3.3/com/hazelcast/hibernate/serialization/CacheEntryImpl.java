/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.cache.spi.entry.CacheEntry
 */
package com.hazelcast.hibernate.serialization;

import java.io.Serializable;
import org.hibernate.cache.spi.entry.CacheEntry;

public class CacheEntryImpl
implements CacheEntry {
    private final Serializable[] disassembledState;
    private final String subclass;
    private final Object version;

    public CacheEntryImpl(Serializable[] disassembledState, String subclass, Object version) {
        this.disassembledState = disassembledState;
        this.subclass = subclass;
        this.version = version;
    }

    public String getSubclass() {
        return this.subclass;
    }

    public Object getVersion() {
        return this.version;
    }

    public Serializable[] getDisassembledState() {
        return this.disassembledState;
    }

    public boolean isReferenceEntry() {
        return false;
    }
}

