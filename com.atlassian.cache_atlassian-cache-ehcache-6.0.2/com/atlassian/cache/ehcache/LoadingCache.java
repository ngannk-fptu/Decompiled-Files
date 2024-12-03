/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheLoader
 *  com.google.common.base.Function
 *  com.google.common.base.Throwables
 *  com.google.common.collect.ImmutableListMultimap
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Multimaps
 *  net.sf.ehcache.CacheException
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.Element
 *  net.sf.ehcache.concurrent.LockType
 *  net.sf.ehcache.concurrent.Sync
 *  net.sf.ehcache.constructs.blocking.BlockingCache
 */
package com.atlassian.cache.ehcache;

import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.ehcache.SynchronizedLoadingCacheDecorator;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.concurrent.LockType;
import net.sf.ehcache.concurrent.Sync;
import net.sf.ehcache.constructs.blocking.BlockingCache;

public class LoadingCache<K, V>
extends BlockingCache {
    private static final int DEFAULT_NUMBER_OF_MUTEXES = Integer.getInteger(LoadingCache.class.getName() + '.' + "DEFAULT_NUMBER_OF_MUTEXES", 2048);
    private final CacheLoader<K, V> loader;
    private final SynchronizedLoadingCacheDecorator delegate;
    private final ReadWriteLock loadVsRemoveAllLock = new ReentrantReadWriteLock(true);

    public LoadingCache(SynchronizedLoadingCacheDecorator cache, CacheLoader<K, V> loader) throws CacheException {
        super((Ehcache)cache, DEFAULT_NUMBER_OF_MUTEXES);
        this.loader = Objects.requireNonNull(loader, "loader");
        this.delegate = cache;
    }

    public void acquireReadLockOnKey(Object key) {
        this.getLockForKey(key).lock(LockType.READ);
    }

    public void acquireWriteLockOnKey(Object key) {
        this.getLockForKey(key).lock(LockType.WRITE);
    }

    public void releaseReadLockOnKey(Object key) {
        this.getLockForKey(key).unlock(LockType.READ);
    }

    public void releaseWriteLockOnKey(Object key) {
        this.getLockForKey(key).unlock(LockType.WRITE);
    }

    public boolean tryReadLockOnKey(Object key, long timeout) throws InterruptedException {
        return this.getLockForKey(key).tryLock(LockType.READ, timeout);
    }

    public boolean tryWriteLockOnKey(Object key, long timeout) throws InterruptedException {
        return this.getLockForKey(key).tryLock(LockType.WRITE, timeout);
    }

    public boolean isReadLockedByCurrentThread(Object key) {
        return this.getLockForKey(key).isHeldByCurrentThread(LockType.READ);
    }

    public boolean isWriteLockedByCurrentThread(Object key) {
        return this.getLockForKey(key).isHeldByCurrentThread(LockType.WRITE);
    }

    Lock loadLock() {
        return this.loadVsRemoveAllLock.readLock();
    }

    Lock removeAllLock() {
        return this.loadVsRemoveAllLock.writeLock();
    }

    public Element get(Object key) {
        if (key == null) {
            throw new NullPointerException("null keys are not permitted");
        }
        Element element = super.get(key);
        return element != null ? element : this.loadValueAndReleaseLock(key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Element loadValueAndReleaseLock(Object key) {
        Element result;
        this.loadLock().lock();
        try {
            result = this.delegate.synchronizedLoad(key, this::getFromLoader, loaded -> {
                if (loaded.getObjectValue() != null) {
                    this.getCache().put(loaded);
                }
            });
        }
        finally {
            Sync lock = this.getLockForKey(key);
            if (lock.isHeldByCurrentThread(LockType.WRITE)) {
                lock.unlock(LockType.WRITE);
            }
            this.loadLock().unlock();
        }
        return result;
    }

    private V getFromLoader(Object key) {
        Object value;
        try {
            value = this.loader.load(key);
        }
        catch (RuntimeException re) {
            this.put(new Element(key, null));
            throw LoadingCache.propagate(key, re);
        }
        catch (Error err) {
            this.put(new Element(key, null));
            throw LoadingCache.propagate(key, err);
        }
        if (value == null) {
            throw new CacheException("CacheLoader returned null for key " + key);
        }
        return (V)value;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean remove(Serializable key, boolean doNotNotifyCacheReplicators) {
        Sync sync = this.getLockForKey(key);
        sync.lock(LockType.WRITE);
        try {
            boolean bl = super.remove(key, doNotNotifyCacheReplicators);
            return bl;
        }
        finally {
            sync.unlock(LockType.WRITE);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean remove(Serializable key) {
        Sync sync = this.getLockForKey(key);
        sync.lock(LockType.WRITE);
        try {
            boolean bl = super.remove(key);
            return bl;
        }
        finally {
            sync.unlock(LockType.WRITE);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean remove(Object key) {
        Sync sync = this.getLockForKey(key);
        sync.lock(LockType.WRITE);
        try {
            boolean bl = super.remove(key);
            return bl;
        }
        finally {
            sync.unlock(LockType.WRITE);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean remove(Object key, boolean doNotNotifyCacheReplicators) {
        Sync sync = this.getLockForKey(key);
        sync.lock(LockType.WRITE);
        try {
            boolean bl = super.remove(key, doNotNotifyCacheReplicators);
            return bl;
        }
        finally {
            sync.unlock(LockType.WRITE);
        }
    }

    public void removeAll(Collection<?> keys) {
        this.removeGroupedBySync(keys, new RemoveCallback(){

            @Override
            public void removeUnderLock(Collection<?> keysForSync) {
                LoadingCache.this.underlyingCache.removeAll(keysForSync);
            }
        });
    }

    public void removeAll(Collection<?> keys, final boolean doNotNotifyCacheReplicators) {
        this.removeGroupedBySync(keys, new RemoveCallback(){

            @Override
            public void removeUnderLock(Collection<?> keysForSync) {
                LoadingCache.this.underlyingCache.removeAll(keysForSync, doNotNotifyCacheReplicators);
            }
        });
    }

    private void removeGroupedBySync(Collection<?> allKeys, RemoveCallback callback) {
        ImmutableListMultimap map = Multimaps.index(allKeys, (Function)new Function<Object, Sync>(){

            public Sync apply(Object key) {
                return LoadingCache.this.getLockForKey(key);
            }
        });
        LoadingCache.removeGroupedBySync(map, callback);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static <K> void removeGroupedBySync(Multimap<Sync, K> keysBySync, RemoveCallback callback) {
        for (Map.Entry entry : keysBySync.asMap().entrySet()) {
            Sync sync = (Sync)entry.getKey();
            Collection keysUsingThisSync = (Collection)entry.getValue();
            sync.lock(LockType.WRITE);
            try {
                callback.removeUnderLock(keysUsingThisSync);
            }
            finally {
                sync.unlock(LockType.WRITE);
            }
        }
    }

    public void removeAll() {
        this.removeAllLock().lock();
        try {
            super.removeAll();
        }
        finally {
            this.removeAllLock().unlock();
        }
    }

    public void removeAll(boolean doNotNotifyCacheReplicators) {
        this.removeAllLock().lock();
        try {
            super.removeAll(doNotNotifyCacheReplicators);
        }
        finally {
            this.removeAllLock().unlock();
        }
    }

    private static RuntimeException propagate(Object key, Throwable e) {
        Throwables.propagateIfInstanceOf((Throwable)e, CacheException.class);
        throw new CacheException("Could not fetch object for cache entry with key \"" + key + "\".", e);
    }

    static interface RemoveCallback {
        public void removeUnderLock(Collection<?> var1);
    }
}

