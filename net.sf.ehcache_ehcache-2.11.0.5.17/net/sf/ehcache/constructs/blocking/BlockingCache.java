/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.constructs.blocking;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.concurrent.CacheLockProvider;
import net.sf.ehcache.concurrent.LockType;
import net.sf.ehcache.concurrent.StripedReadWriteLockSync;
import net.sf.ehcache.concurrent.Sync;
import net.sf.ehcache.constructs.EhcacheDecoratorAdapter;
import net.sf.ehcache.constructs.blocking.BlockingCacheOperationOutcomes;
import net.sf.ehcache.constructs.blocking.LockTimeoutException;
import net.sf.ehcache.loader.CacheLoader;
import net.sf.ehcache.statistics.StatisticBuilder;
import org.terracotta.statistics.observer.OperationObserver;

public class BlockingCache
extends EhcacheDecoratorAdapter {
    protected volatile int timeoutMillis;
    private final int stripes;
    private final AtomicReference<CacheLockProvider> cacheLockProviderReference;
    private final OperationObserver<BlockingCacheOperationOutcomes.GetOutcome> getObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(BlockingCacheOperationOutcomes.GetOutcome.class).named("get")).of(this)).tag(new String[]{"blocking-cache"})).build();

    public BlockingCache(Ehcache cache, int numberOfStripes) throws CacheException {
        super(cache);
        this.stripes = numberOfStripes;
        this.cacheLockProviderReference = new AtomicReference();
    }

    public BlockingCache(Ehcache cache) throws CacheException {
        this(cache, 2048);
    }

    private CacheLockProvider getCacheLockProvider() {
        CacheLockProvider provider = this.cacheLockProviderReference.get();
        while (provider == null) {
            this.cacheLockProviderReference.compareAndSet(null, this.createCacheLockProvider());
            provider = this.cacheLockProviderReference.get();
        }
        return provider;
    }

    private CacheLockProvider createCacheLockProvider() {
        Object context = this.underlyingCache.getInternalContext();
        if (this.underlyingCache.getCacheConfiguration().isTerracottaClustered() && context != null) {
            return (CacheLockProvider)context;
        }
        return new StripedReadWriteLockSync(this.stripes);
    }

    protected Ehcache getCache() {
        return this.underlyingCache;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element get(Object key) throws RuntimeException, LockTimeoutException {
        Element element;
        this.getObserver.begin();
        Sync lock = this.getLockForKey(key);
        this.acquiredLockForKey(key, lock, LockType.READ);
        try {
            element = this.underlyingCache.get(key);
        }
        finally {
            lock.unlock(LockType.READ);
        }
        if (element == null) {
            this.acquiredLockForKey(key, lock, LockType.WRITE);
            element = this.underlyingCache.get(key);
            if (element != null) {
                lock.unlock(LockType.WRITE);
                this.getObserver.end(BlockingCacheOperationOutcomes.GetOutcome.HIT);
            } else {
                this.getObserver.end(BlockingCacheOperationOutcomes.GetOutcome.MISS_AND_LOCKED);
            }
            return element;
        }
        this.getObserver.end(BlockingCacheOperationOutcomes.GetOutcome.HIT);
        return element;
    }

    private void acquiredLockForKey(Object key, Sync lock, LockType lockType) {
        block4: {
            if (this.timeoutMillis > 0) {
                try {
                    boolean acquired = lock.tryLock(lockType, this.timeoutMillis);
                    if (!acquired) {
                        StringBuilder message = new StringBuilder("Lock timeout. Waited more than ").append(this.timeoutMillis).append("ms to acquire lock for key ").append(key).append(" on blocking cache ").append(this.underlyingCache.getName());
                        throw new LockTimeoutException(message.toString());
                    }
                    break block4;
                }
                catch (InterruptedException e) {
                    throw new LockTimeoutException("Got interrupted while trying to acquire lock for key " + key, e);
                }
            }
            lock.lock(lockType);
        }
    }

    protected Sync getLockForKey(Object key) {
        return this.getCacheLockProvider().getSyncForKey(key);
    }

    @Override
    public void put(final Element element) {
        this.doAndReleaseWriteLock(new PutAction<Void>(element){

            @Override
            public Void put() {
                if (element.getObjectValue() != null) {
                    BlockingCache.this.underlyingCache.put(element);
                } else {
                    BlockingCache.this.underlyingCache.remove(element.getObjectKey());
                }
                return null;
            }
        });
    }

    @Override
    public void put(final Element element, final boolean doNotNotifyCacheReplicators) throws IllegalArgumentException, IllegalStateException, CacheException {
        this.doAndReleaseWriteLock(new PutAction<Void>(element){

            @Override
            public Void put() {
                BlockingCache.this.underlyingCache.put(element, doNotNotifyCacheReplicators);
                return null;
            }
        });
    }

    @Override
    public void putQuiet(final Element element) throws IllegalArgumentException, IllegalStateException, CacheException {
        this.doAndReleaseWriteLock(new PutAction<Void>(element){

            @Override
            public Void put() {
                BlockingCache.this.underlyingCache.putQuiet(element);
                return null;
            }
        });
    }

    @Override
    public void putWithWriter(final Element element) throws IllegalArgumentException, IllegalStateException, CacheException {
        this.doAndReleaseWriteLock(new PutAction<Void>(element){

            @Override
            public Void put() {
                BlockingCache.this.underlyingCache.putWithWriter(element);
                return null;
            }
        });
    }

    @Override
    public Element putIfAbsent(final Element element) throws NullPointerException {
        return this.doAndReleaseWriteLock(new PutAction<Element>(element){

            @Override
            public Element put() {
                return BlockingCache.this.underlyingCache.putIfAbsent(element);
            }
        });
    }

    @Override
    public Element putIfAbsent(final Element element, final boolean doNotNotifyCacheReplicators) throws NullPointerException {
        return this.doAndReleaseWriteLock(new PutAction<Element>(element){

            @Override
            public Element put() {
                return BlockingCache.this.underlyingCache.putIfAbsent(element, doNotNotifyCacheReplicators);
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private <V> V doAndReleaseWriteLock(PutAction<V> putAction) {
        if (putAction.element == null) {
            return null;
        }
        Object key = putAction.element.getObjectKey();
        Sync lock = this.getLockForKey(key);
        if (!lock.isHeldByCurrentThread(LockType.WRITE)) {
            lock.lock(LockType.WRITE);
        }
        try {
            V v = putAction.put();
            return v;
        }
        finally {
            lock.unlock(LockType.WRITE);
        }
    }

    @Override
    public Element get(Serializable key) throws IllegalStateException, CacheException {
        return this.get((Object)key);
    }

    public synchronized String liveness() {
        return this.getName();
    }

    public void setTimeoutMillis(int timeoutMillis) {
        if (timeoutMillis < 0) {
            throw new CacheException("The lock timeout must be a positive number of ms. Value was " + timeoutMillis);
        }
        this.timeoutMillis = timeoutMillis;
    }

    public int getTimeoutMillis() {
        return this.timeoutMillis;
    }

    @Override
    public void registerCacheLoader(CacheLoader cacheLoader) {
        throw new CacheException("This method is not appropriate for a blocking cache.");
    }

    @Override
    public void unregisterCacheLoader(CacheLoader cacheLoader) {
        throw new CacheException("This method is not appropriate for a blocking cache.");
    }

    @Override
    public Element getWithLoader(Object key, CacheLoader loader, Object loaderArgument) throws CacheException {
        throw new CacheException("This method is not appropriate for a Blocking Cache");
    }

    @Override
    public Map getAllWithLoader(Collection keys, Object loaderArgument) throws CacheException {
        throw new CacheException("This method is not appropriate for a Blocking Cache");
    }

    @Override
    public void load(Object key) throws CacheException {
        throw new CacheException("This method is not appropriate for a Blocking Cache");
    }

    @Override
    public void loadAll(Collection keys, Object argument) throws CacheException {
        throw new CacheException("This method is not appropriate for a Blocking Cache");
    }

    private static abstract class PutAction<V> {
        private final Element element;

        private PutAction(Element element) {
            this.element = element;
        }

        abstract V put();
    }
}

