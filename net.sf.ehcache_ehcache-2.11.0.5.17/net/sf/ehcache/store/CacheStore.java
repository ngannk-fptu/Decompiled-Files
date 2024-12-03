/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.store;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.concurrent.StripedReadWriteLock;
import net.sf.ehcache.concurrent.StripedReadWriteLockSync;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.store.AuthoritativeTier;
import net.sf.ehcache.store.CachingTier;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.Policy;
import net.sf.ehcache.store.PressuredStore;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.store.StoreListener;
import net.sf.ehcache.store.StoreQuery;
import net.sf.ehcache.store.StripedReadWriteLockProvider;
import net.sf.ehcache.store.disk.DiskStore;
import net.sf.ehcache.terracotta.TerracottaNotRunningException;
import net.sf.ehcache.writer.CacheWriterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.context.annotations.ContextChild;

public class CacheStore
implements Store {
    private static final Logger LOG = LoggerFactory.getLogger(CacheStore.class);
    private static final int DEFAULT_LOCK_STRIPE_COUNT = 128;
    @ContextChild
    private final CachingTier<Object, Element> cachingTier;
    @ContextChild
    private final AuthoritativeTier authoritativeTier;
    @Deprecated
    private final StripedReadWriteLock masterLocks;
    @Deprecated
    private final CacheConfiguration cacheConfiguration;
    private volatile Status status;
    private final ReadWriteLock daLock = new ReentrantReadWriteLock();

    public CacheStore(CachingTier<Object, Element> cache, AuthoritativeTier authority) {
        this(cache, authority, null);
    }

    @Deprecated
    public CacheStore(CachingTier<Object, Element> cache, final AuthoritativeTier authority, final CacheConfiguration cacheConfiguration) {
        if (cache == null || authority == null) {
            throw new NullPointerException();
        }
        this.cachingTier = cache;
        this.cacheConfiguration = cacheConfiguration;
        this.cachingTier.addListener(new CachingTier.Listener<Object, Element>(){

            @Override
            public void evicted(Object key, Element value) {
                authority.flush(value);
            }
        });
        this.authoritativeTier = authority;
        this.masterLocks = authority instanceof StripedReadWriteLockProvider ? ((StripedReadWriteLockProvider)((Object)authority)).createStripedReadWriteLock() : new StripedReadWriteLockSync(128);
        this.status = Status.STATUS_ALIVE;
        if (authority instanceof PressuredStore) {
            ((PressuredStore)((Object)authority)).registerEmergencyValve(new Callable<Void>(){

                @Override
                public Void call() throws Exception {
                    LOG.warn("Having to clear CachingTier to free space in the Authority for Cache '" + (cacheConfiguration != null ? cacheConfiguration.getName() : "<null>") + "'");
                    CacheStore.this.cachingTier.clearAndNotify();
                    return null;
                }
            });
        }
    }

    @Override
    public void addStoreListener(StoreListener listener) {
        this.authoritativeTier.addStoreListener(listener);
    }

    @Override
    public void removeStoreListener(StoreListener listener) {
        this.authoritativeTier.removeStoreListener(listener);
    }

    @Override
    public boolean put(final Element element) throws CacheException {
        if (this.cachingTier.remove(element.getObjectKey()) != null || this.cachingTier.loadOnPut()) {
            try {
                final boolean[] hack = new boolean[1];
                if (this.cachingTier.get(element.getObjectKey(), new Callable<Element>(){

                    @Override
                    public Element call() throws Exception {
                        Lock lock = CacheStore.this.daLock.readLock();
                        lock.lock();
                        try {
                            hack[0] = CacheStore.this.authoritativeTier.putFaulted(element);
                            Element element2 = element;
                            return element2;
                        }
                        finally {
                            lock.unlock();
                        }
                    }
                }, false) == element) {
                    return hack[0];
                }
            }
            catch (Throwable e) {
                this.cachingTier.remove(element.getObjectKey());
                if (e instanceof RuntimeException) {
                    throw (RuntimeException)e;
                }
                throw new CacheException(e);
            }
        }
        try {
            boolean e = this.authoritativeTier.put(element);
            return e;
        }
        catch (RuntimeException e) {
            this.authoritativeTier.flush(element);
            throw e;
        }
        finally {
            this.cachingTier.remove(element.getObjectKey());
        }
    }

    @Override
    public void putAll(Collection<Element> elements) throws CacheException {
        for (Element element : elements) {
            this.put(element);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean putWithWriter(Element element, CacheWriterManager writerManager) throws CacheException {
        try {
            boolean bl = this.authoritativeTier.putWithWriter(element, writerManager);
            return bl;
        }
        finally {
            this.cachingTier.remove(element.getObjectKey());
        }
    }

    @Override
    public Element get(final Object key) {
        if (key == null) {
            return null;
        }
        return this.cachingTier.get(key, new Callable<Element>(){

            @Override
            public Element call() throws Exception {
                Lock lock = CacheStore.this.daLock.readLock();
                lock.lock();
                try {
                    Element element = CacheStore.this.authoritativeTier.fault(key, true);
                    return element;
                }
                finally {
                    lock.unlock();
                }
            }
        }, true);
    }

    @Override
    public Element getQuiet(final Object key) {
        if (key == null) {
            return null;
        }
        return this.cachingTier.get(key, new Callable<Element>(){

            @Override
            public Element call() throws Exception {
                Lock lock = CacheStore.this.daLock.readLock();
                lock.lock();
                try {
                    Element element = CacheStore.this.authoritativeTier.fault(key, false);
                    return element;
                }
                finally {
                    lock.unlock();
                }
            }
        }, false);
    }

    @Override
    public List getKeys() {
        return this.authoritativeTier.getKeys();
    }

    @Override
    public Element remove(Object key) {
        if (key == null) {
            return null;
        }
        try {
            Element element = this.authoritativeTier.remove(key);
            return element;
        }
        finally {
            this.cachingTier.remove(key);
        }
    }

    @Override
    public void removeAll(Collection<?> keys) {
        for (Object key : keys) {
            this.remove(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element removeWithWriter(Object key, CacheWriterManager writerManager) throws CacheException {
        try {
            Element element = this.authoritativeTier.removeWithWriter(key, writerManager);
            return element;
        }
        finally {
            this.cachingTier.remove(key);
        }
    }

    @Override
    public void removeAll() throws CacheException {
        Lock lock = this.daLock.writeLock();
        lock.lock();
        try {
            this.authoritativeTier.removeAll();
        }
        finally {
            this.cachingTier.clear();
            lock.unlock();
        }
    }

    @Override
    public Element putIfAbsent(Element element) throws NullPointerException {
        Element previous = null;
        try {
            previous = this.authoritativeTier.putIfAbsent(element);
        }
        finally {
            if (previous == null) {
                this.cachingTier.remove(element.getObjectKey());
            }
        }
        return previous;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element removeElement(Element element, ElementValueComparator comparator) throws NullPointerException {
        Element removedElement;
        try {
            removedElement = this.authoritativeTier.removeElement(element, comparator);
        }
        finally {
            this.cachingTier.remove(element.getObjectKey());
        }
        return removedElement;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean replace(Element old, Element element, ElementValueComparator comparator) throws NullPointerException, IllegalArgumentException {
        boolean replaced = true;
        try {
            replaced = this.authoritativeTier.replace(old, element, comparator);
        }
        finally {
            if (replaced) {
                this.cachingTier.remove(element.getObjectKey());
            }
        }
        return replaced;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element replace(Element element) throws NullPointerException {
        Element previous = null;
        try {
            previous = this.authoritativeTier.replace(element);
        }
        catch (Throwable e) {
            this.cachingTier.remove(previous.getObjectKey());
            this.throwUp(e);
        }
        finally {
            if (previous != null) {
                this.cachingTier.remove(previous.getObjectKey());
            }
        }
        return previous;
    }

    private void throwUp(Throwable e) {
        if (e instanceof RuntimeException) {
            throw (RuntimeException)e;
        }
        if (e instanceof Error) {
            throw (Error)e;
        }
        throw new CacheException(e);
    }

    @Override
    public synchronized void dispose() {
        if (this.status == Status.STATUS_SHUTDOWN) {
            return;
        }
        if (this.cacheConfiguration != null && this.cacheConfiguration.isClearOnFlush()) {
            this.cachingTier.clear();
        }
        this.authoritativeTier.dispose();
        this.status = Status.STATUS_SHUTDOWN;
    }

    @Override
    public int getSize() {
        return this.authoritativeTier.getSize();
    }

    @Override
    public int getInMemorySize() {
        return this.cachingTier.getInMemorySize() + this.authoritativeTier.getInMemorySize();
    }

    @Override
    public int getOffHeapSize() {
        return this.cachingTier.getOffHeapSize() + this.authoritativeTier.getOffHeapSize();
    }

    @Override
    public int getOnDiskSize() {
        return this.authoritativeTier.getOnDiskSize();
    }

    @Override
    public int getTerracottaClusteredSize() {
        throw new UnsupportedOperationException("No such thing for non clustered stores!");
    }

    @Override
    public long getInMemorySizeInBytes() {
        return this.cachingTier.getInMemorySizeInBytes() + this.authoritativeTier.getInMemorySizeInBytes();
    }

    @Override
    public long getOffHeapSizeInBytes() {
        return this.cachingTier.getOffHeapSizeInBytes() + this.authoritativeTier.getOffHeapSizeInBytes();
    }

    @Override
    public long getOnDiskSizeInBytes() {
        return this.cachingTier.getOnDiskSizeInBytes() + this.authoritativeTier.getOnDiskSizeInBytes();
    }

    @Override
    public boolean hasAbortedSizeOf() {
        return this.authoritativeTier.hasAbortedSizeOf();
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.authoritativeTier.containsKey(key);
    }

    @Override
    @Deprecated
    public boolean containsKeyOnDisk(Object key) {
        return this.authoritativeTier.containsKeyOnDisk(key);
    }

    @Override
    @Deprecated
    public boolean containsKeyOffHeap(Object key) {
        return this.authoritativeTier.containsKeyOffHeap(key);
    }

    @Override
    @Deprecated
    public boolean containsKeyInMemory(Object key) {
        return this.cachingTier.contains(key);
    }

    @Override
    public void expireElements() {
        this.authoritativeTier.expireElements();
    }

    @Override
    public void flush() throws IOException {
        if (this.authoritativeTier instanceof DiskStore && this.cacheConfiguration != null && this.cacheConfiguration.isClearOnFlush()) {
            Lock lock = this.daLock.writeLock();
            lock.lock();
            try {
                this.cachingTier.clear();
                ((DiskStore)this.authoritativeTier).clearFaultedBit();
            }
            finally {
                lock.unlock();
            }
        } else {
            this.authoritativeTier.flush();
        }
    }

    @Override
    public boolean bufferFull() {
        return this.authoritativeTier.bufferFull();
    }

    @Override
    public Policy getInMemoryEvictionPolicy() {
        return this.cachingTier.getEvictionPolicy();
    }

    @Override
    public void setInMemoryEvictionPolicy(Policy policy) {
        this.cachingTier.setEvictionPolicy(policy);
    }

    @Override
    @Deprecated
    public Object getInternalContext() {
        return this.masterLocks;
    }

    @Override
    public boolean isCacheCoherent() {
        throw new UnsupportedOperationException("No such thing for non clustered stores!");
    }

    @Override
    public boolean isClusterCoherent() throws TerracottaNotRunningException {
        throw new UnsupportedOperationException("No such thing for non clustered stores!");
    }

    @Override
    public boolean isNodeCoherent() throws TerracottaNotRunningException {
        throw new UnsupportedOperationException("No such thing for non clustered stores!");
    }

    @Override
    public void setNodeCoherent(boolean coherent) throws UnsupportedOperationException, TerracottaNotRunningException {
        throw new UnsupportedOperationException("No such thing for non clustered stores!");
    }

    @Override
    public void waitUntilClusterCoherent() throws UnsupportedOperationException, TerracottaNotRunningException, InterruptedException {
        throw new UnsupportedOperationException("No such thing for non clustered stores!");
    }

    @Override
    public Object getMBean() {
        return this.authoritativeTier.getMBean();
    }

    @Override
    public void setAttributeExtractors(Map<String, AttributeExtractor> extractors) {
        this.authoritativeTier.setAttributeExtractors(extractors);
    }

    @Override
    public Results executeQuery(StoreQuery query) throws SearchException {
        return this.authoritativeTier.executeQuery(query);
    }

    @Override
    public <T> Attribute<T> getSearchAttribute(String attributeName) {
        return this.authoritativeTier.getSearchAttribute(attributeName);
    }

    @Override
    public Set<Attribute> getSearchAttributes() {
        return this.authoritativeTier.getSearchAttributes();
    }

    @Override
    public Map<Object, Element> getAllQuiet(Collection<?> keys) {
        HashMap<Object, Element> result = new HashMap<Object, Element>();
        for (Object key : keys) {
            result.put(key, this.getQuiet(key));
        }
        return result;
    }

    @Override
    public Map<Object, Element> getAll(Collection<?> keys) {
        HashMap<Object, Element> result = new HashMap<Object, Element>();
        for (Object key : keys) {
            result.put(key, this.get(key));
        }
        return result;
    }

    @Override
    public void recalculateSize(Object key) {
        this.cachingTier.recalculateSize(key);
    }
}

