/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store.disk;

import net.sf.ehcache.writer.CacheWriterManagerException;

public class StoreUpdateException
extends CacheWriterManagerException {
    private final boolean update;

    public StoreUpdateException(RuntimeException e, boolean update) {
        super(e);
        this.update = update;
    }

    public boolean isUpdate() {
        return this.update;
    }
}

