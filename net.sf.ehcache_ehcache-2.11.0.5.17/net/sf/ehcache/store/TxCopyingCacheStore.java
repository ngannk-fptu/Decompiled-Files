/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.AbstractCopyingCacheStore;
import net.sf.ehcache.store.CopyStrategyHandler;
import net.sf.ehcache.store.CopyingCacheStore;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.store.compound.ReadWriteCopyStrategy;
import net.sf.ehcache.transaction.AbstractTransactionStore;
import net.sf.ehcache.transaction.SoftLockID;

public final class TxCopyingCacheStore<T extends Store>
extends AbstractCopyingCacheStore<T> {
    public TxCopyingCacheStore(T store, boolean copyOnRead, boolean copyOnWrite, ReadWriteCopyStrategy<Element> copyStrategyInstance, ClassLoader loader) {
        super(store, copyOnRead, copyOnWrite, copyStrategyInstance, loader);
        if (!(store instanceof AbstractTransactionStore)) {
            throw new IllegalArgumentException("TxCopyingCacheStore can only wrap a transactional store");
        }
    }

    public Element getOldElement(Object key) {
        return this.getCopyStrategyHandler().copyElementForReadIfNeeded(((AbstractTransactionStore)this.getUnderlyingStore()).getOldElement(key));
    }

    public static Store wrapTxStore(AbstractTransactionStore cacheStore, CacheConfiguration cacheConfiguration) {
        if (CopyingCacheStore.requiresCopy(cacheConfiguration)) {
            return TxCopyingCacheStore.wrap(cacheStore, cacheConfiguration);
        }
        return cacheStore;
    }

    private static <T extends Store> TxCopyingCacheStore<T> wrap(T cacheStore, CacheConfiguration cacheConfiguration) {
        ReadWriteCopyStrategy<Element> copyStrategyInstance = cacheConfiguration.getCopyStrategyConfiguration().getCopyStrategyInstance(cacheConfiguration.getClassLoader());
        return new TxCopyingCacheStore<T>(cacheStore, cacheConfiguration.isCopyOnRead(), cacheConfiguration.isCopyOnWrite(), copyStrategyInstance, cacheConfiguration.getClassLoader());
    }

    public static ElementValueComparator wrap(ElementValueComparator comparator, CacheConfiguration cacheConfiguration) {
        ReadWriteCopyStrategy<Element> copyStrategyInstance = cacheConfiguration.getCopyStrategyConfiguration().getCopyStrategyInstance(cacheConfiguration.getClassLoader());
        CopyStrategyHandler copyStrategyHandler = new CopyStrategyHandler(cacheConfiguration.isCopyOnRead(), cacheConfiguration.isCopyOnWrite(), copyStrategyInstance, cacheConfiguration.getClassLoader());
        return new TxCopyingElementValueComparator(comparator, copyStrategyHandler);
    }

    private static class TxCopyingElementValueComparator
    implements ElementValueComparator {
        private final ElementValueComparator delegate;
        private final CopyStrategyHandler copyStrategyHandler;

        public TxCopyingElementValueComparator(ElementValueComparator delegate, CopyStrategyHandler copyStrategyHandler) {
            this.delegate = delegate;
            this.copyStrategyHandler = copyStrategyHandler;
        }

        @Override
        public boolean equals(Element e1, Element e2) {
            if (e1 == null && e2 == null) {
                return true;
            }
            if (e1 == null || e2 == null) {
                return false;
            }
            if (!(e1.getObjectValue() instanceof SoftLockID)) {
                e1 = this.copyStrategyHandler.copyElementForReadIfNeeded(e1);
            }
            if (!(e2.getObjectValue() instanceof SoftLockID)) {
                e2 = this.copyStrategyHandler.copyElementForReadIfNeeded(e2);
            }
            return this.delegate.equals(e1, e2);
        }
    }
}

