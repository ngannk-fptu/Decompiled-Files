/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import java.util.Set;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.AbstractCopyingCacheStore;
import net.sf.ehcache.store.TerracottaStore;
import net.sf.ehcache.store.compound.ReadWriteCopyStrategy;
import net.sf.ehcache.writer.writebehind.WriteBehind;

public final class TerracottaTransactionalCopyingCacheStore
extends AbstractCopyingCacheStore<TerracottaStore>
implements TerracottaStore {
    public TerracottaTransactionalCopyingCacheStore(TerracottaStore store, ReadWriteCopyStrategy<Element> copyStrategyInstance, ClassLoader loader) {
        super(store, true, false, copyStrategyInstance, loader);
    }

    @Override
    public Element unsafeGet(Object key) {
        return this.getCopyStrategyHandler().copyElementForReadIfNeeded(((TerracottaStore)this.getUnderlyingStore()).unsafeGet(key));
    }

    @Override
    public void quickClear() {
        ((TerracottaStore)this.getUnderlyingStore()).quickClear();
    }

    @Override
    public int quickSize() {
        return ((TerracottaStore)this.getUnderlyingStore()).quickSize();
    }

    @Override
    public Set getLocalKeys() {
        return ((TerracottaStore)this.getUnderlyingStore()).getLocalKeys();
    }

    @Override
    public CacheConfiguration.TransactionalMode getTransactionalMode() {
        return ((TerracottaStore)this.getUnderlyingStore()).getTransactionalMode();
    }

    @Override
    public WriteBehind createWriteBehind() {
        return ((TerracottaStore)this.getUnderlyingStore()).createWriteBehind();
    }

    @Override
    public void notifyCacheEventListenersChanged() {
        ((TerracottaStore)this.getUnderlyingStore()).notifyCacheEventListenersChanged();
    }
}

