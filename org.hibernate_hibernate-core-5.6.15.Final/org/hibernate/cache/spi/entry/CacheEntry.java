/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.entry;

import java.io.Serializable;

public interface CacheEntry
extends Serializable {
    public boolean isReferenceEntry();

    public String getSubclass();

    public Object getVersion();

    public Serializable[] getDisassembledState();
}

