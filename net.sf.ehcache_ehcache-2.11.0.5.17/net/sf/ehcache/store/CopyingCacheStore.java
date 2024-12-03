/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.AbstractCopyingCacheStore;
import net.sf.ehcache.store.CopyStrategyHandler;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.store.compound.ReadWriteCopyStrategy;

public final class CopyingCacheStore<T extends Store>
extends AbstractCopyingCacheStore<T> {
    public CopyingCacheStore(T store, boolean copyOnRead, boolean copyOnWrite, ReadWriteCopyStrategy<Element> copyStrategyInstance, ClassLoader loader) {
        super(store, copyOnRead, copyOnWrite, copyStrategyInstance, loader);
    }

    public static Store wrapIfCopy(Store cacheStore, CacheConfiguration cacheConfiguration) {
        if (CopyingCacheStore.requiresCopy(cacheConfiguration)) {
            return CopyingCacheStore.wrap(cacheStore, cacheConfiguration);
        }
        return cacheStore;
    }

    private static <T extends Store> CopyingCacheStore<T> wrap(T cacheStore, CacheConfiguration cacheConfiguration) {
        ReadWriteCopyStrategy<Element> copyStrategyInstance = cacheConfiguration.getCopyStrategyConfiguration().getCopyStrategyInstance(cacheConfiguration.getClassLoader());
        return new CopyingCacheStore<T>(cacheStore, cacheConfiguration.isCopyOnRead(), cacheConfiguration.isCopyOnWrite(), copyStrategyInstance, cacheConfiguration.getClassLoader());
    }

    static boolean requiresCopy(CacheConfiguration cacheConfiguration) {
        return cacheConfiguration.isCopyOnRead() || cacheConfiguration.isCopyOnWrite();
    }

    private static boolean isCopyOnReadAndCopyOnWrite(CacheConfiguration cacheConfiguration) {
        return cacheConfiguration.isCopyOnRead() && cacheConfiguration.isCopyOnWrite();
    }

    public static ElementValueComparator wrapIfCopy(ElementValueComparator comparator, CacheConfiguration cacheConfiguration) {
        if (CopyingCacheStore.isCopyOnReadAndCopyOnWrite(cacheConfiguration)) {
            ReadWriteCopyStrategy<Element> copyStrategyInstance = cacheConfiguration.getCopyStrategyConfiguration().getCopyStrategyInstance(cacheConfiguration.getClassLoader());
            CopyStrategyHandler copyStrategyHandler = new CopyStrategyHandler(cacheConfiguration.isCopyOnRead(), cacheConfiguration.isCopyOnWrite(), copyStrategyInstance, cacheConfiguration.getClassLoader());
            return new CopyingElementValueComparator(comparator, copyStrategyHandler);
        }
        return comparator;
    }

    private static class CopyingElementValueComparator
    implements ElementValueComparator {
        private final ElementValueComparator delegate;
        private final CopyStrategyHandler copyStrategyHandler;

        public CopyingElementValueComparator(ElementValueComparator delegate, CopyStrategyHandler copyStrategyHandler) {
            this.delegate = delegate;
            this.copyStrategyHandler = copyStrategyHandler;
        }

        @Override
        public boolean equals(Element e1, Element e2) {
            return this.delegate.equals(this.copyStrategyHandler.copyElementForReadIfNeeded(e1), this.copyStrategyHandler.copyElementForReadIfNeeded(e2));
        }
    }
}

