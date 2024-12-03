/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction.xa.commands;

import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.Element;
import net.sf.ehcache.transaction.xa.commands.AbstractStoreCommand;

public class StoreRemoveCommand
extends AbstractStoreCommand {
    private Object key;

    public StoreRemoveCommand(Object key, Element oldElement) {
        super(oldElement, null);
        this.key = key;
    }

    @Override
    public boolean isPut(Object key) {
        return false;
    }

    @Override
    public boolean isRemove(Object key) {
        return this.getObjectKey().equals(key);
    }

    @Override
    public Object getObjectKey() {
        return this.key;
    }

    public CacheEntry getEntry() {
        return new CacheEntry(this.key, this.getOldElement());
    }
}

