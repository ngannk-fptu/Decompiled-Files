/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.store;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheOperationOutcomes;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PinningConfiguration;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import net.sf.ehcache.pool.Size;
import net.sf.ehcache.pool.SizeOfEngine;
import net.sf.ehcache.pool.SizeOfEngineLoader;
import net.sf.ehcache.statistics.StatisticBuilder;
import net.sf.ehcache.store.AbstractStore;
import net.sf.ehcache.store.CopyStrategyHandler;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.LruPolicy;
import net.sf.ehcache.store.MemoryStore;
import net.sf.ehcache.store.Policy;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.store.StoreOperationOutcomes;
import net.sf.ehcache.writer.CacheWriterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.statistics.Statistic;
import org.terracotta.statistics.observer.OperationObserver;

public class LruMemoryStore
extends AbstractStore {
    private static final Logger LOG = LoggerFactory.getLogger((String)LruMemoryStore.class.getName());
    protected Ehcache cache;
    protected Map map;
    protected final Store diskStore;
    protected Status status;
    protected long maximumSize;
    private final boolean cachePinned;
    private final boolean elementPinningEnabled;
    private final CopyStrategyHandler copyStrategyHandler;
    private final OperationObserver<StoreOperationOutcomes.GetOutcome> getObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(StoreOperationOutcomes.GetOutcome.class).named("get")).of(this)).tag(new String[]{"local-heap"})).build();
    private final OperationObserver<StoreOperationOutcomes.PutOutcome> putObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(StoreOperationOutcomes.PutOutcome.class).named("put")).of(this)).tag(new String[]{"local-heap"})).build();
    private final OperationObserver<StoreOperationOutcomes.RemoveOutcome> removeObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(StoreOperationOutcomes.RemoveOutcome.class).named("remove")).of(this)).tag(new String[]{"local-heap"})).build();
    private final OperationObserver<CacheOperationOutcomes.EvictionOutcome> evictionObserver;

    public LruMemoryStore(Ehcache cache, Store diskStore) {
        this.status = Status.STATUS_UNINITIALISED;
        this.maximumSize = cache.getCacheConfiguration().getMaxEntriesLocalHeap();
        this.cachePinned = this.determineCachePinned(cache.getCacheConfiguration());
        this.elementPinningEnabled = !cache.getCacheConfiguration().isOverflowToOffHeap();
        this.cache = cache;
        this.diskStore = diskStore;
        this.evictionObserver = cache.getCacheConfiguration().isOverflowToDisk() ? null : ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(CacheOperationOutcomes.EvictionOutcome.class).named("eviction")).of(this)).build();
        this.map = new SpoolingLinkedHashMap();
        this.status = Status.STATUS_ALIVE;
        this.copyStrategyHandler = MemoryStore.getCopyStrategyHandler(cache);
    }

    private boolean determineCachePinned(CacheConfiguration cacheConfiguration) {
        PinningConfiguration pinningConfiguration = cacheConfiguration.getPinningConfiguration();
        if (pinningConfiguration == null) {
            return false;
        }
        switch (pinningConfiguration.getStore()) {
            case LOCALMEMORY: {
                return false;
            }
            case INCACHE: {
                return !cacheConfiguration.isOverflowToOffHeap() && !cacheConfiguration.isOverflowToDisk();
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public final boolean put(Element element) throws CacheException {
        return this.putInternal(element, null);
    }

    @Override
    public final boolean putWithWriter(Element element, CacheWriterManager writerManager) throws CacheException {
        return this.putInternal(element, writerManager);
    }

    private synchronized boolean putInternal(Element element, CacheWriterManager writerManager) throws CacheException {
        this.putObserver.begin();
        boolean newPut = true;
        if (element != null) {
            boolean bl = newPut = this.map.put(element.getObjectKey(), element) == null;
            if (writerManager != null) {
                writerManager.put(element);
            }
            this.doPut(element);
        }
        if (newPut) {
            this.putObserver.end(StoreOperationOutcomes.PutOutcome.ADDED);
        } else {
            this.putObserver.end(StoreOperationOutcomes.PutOutcome.UPDATED);
        }
        return newPut;
    }

    protected void doPut(Element element) throws CacheException {
    }

    @Override
    public final synchronized Element get(Object key) {
        this.getObserver.begin();
        Element e = (Element)this.map.get(key);
        if (e == null) {
            this.getObserver.end(StoreOperationOutcomes.GetOutcome.MISS);
            return null;
        }
        this.getObserver.end(StoreOperationOutcomes.GetOutcome.HIT);
        return e;
    }

    @Override
    public final synchronized Element getQuiet(Object key) {
        return (Element)this.map.get(key);
    }

    @Override
    public final Element remove(Object key) {
        return this.removeInternal(key, null);
    }

    @Override
    public final Element removeWithWriter(Object key, CacheWriterManager writerManager) throws CacheException {
        return this.removeInternal(key, writerManager);
    }

    private synchronized Element removeInternal(Object key, CacheWriterManager writerManager) throws CacheException {
        this.removeObserver.begin();
        Element element = (Element)this.map.remove(key);
        if (writerManager != null) {
            writerManager.remove(new CacheEntry(key, element));
        }
        this.removeObserver.end(StoreOperationOutcomes.RemoveOutcome.SUCCESS);
        if (element != null) {
            return element;
        }
        return null;
    }

    @Override
    public final synchronized void removeAll() throws CacheException {
        this.clear();
    }

    protected final void clear() {
        this.map.clear();
    }

    @Override
    public final synchronized void dispose() {
        if (this.status.equals(Status.STATUS_SHUTDOWN)) {
            return;
        }
        this.status = Status.STATUS_SHUTDOWN;
        this.flush();
        this.cache = null;
    }

    @Override
    public final void flush() {
        if (this.cache.getCacheConfiguration().isDiskPersistent()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(this.cache.getName() + " is persistent. Spooling " + this.map.size() + " elements to the disk store.");
            }
            this.spoolAllToDisk();
        }
        if (this.cache.getCacheConfiguration().isClearOnFlush()) {
            this.clear();
        }
    }

    protected final void spoolAllToDisk() {
        boolean clearOnFlush = this.cache.getCacheConfiguration().isClearOnFlush();
        for (Object key : this.getKeys()) {
            Element element = (Element)this.map.get(key);
            if (element == null) continue;
            if (!element.isSerializable()) {
                if (!LOG.isWarnEnabled()) continue;
                LOG.warn("Object with key " + element.getObjectKey() + " is not Serializable and is not being overflowed to disk.");
                continue;
            }
            this.spoolToDisk(element);
            if (!clearOnFlush) continue;
            this.remove(key);
        }
    }

    protected void spoolToDisk(Element element) {
        this.diskStore.put(element);
        if (LOG.isDebugEnabled()) {
            LOG.debug(this.cache.getName() + "Cache: spool to disk done for: " + element.getObjectKey());
        }
    }

    @Override
    public final Status getStatus() {
        return this.status;
    }

    @Override
    public final synchronized List getKeys() {
        return new ArrayList(this.map.keySet());
    }

    @Override
    public final int getSize() {
        return this.map.size();
    }

    @Override
    public final int getTerracottaClusteredSize() {
        return 0;
    }

    @Override
    public final boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    public final synchronized long getSizeInBytes() throws CacheException {
        SizeOfEngine defaultSizeOfEngine = SizeOfEngineLoader.newSizeOfEngine(SizeOfPolicyConfiguration.resolveMaxDepth(this.cache), SizeOfPolicyConfiguration.resolveBehavior(this.cache).equals((Object)SizeOfPolicyConfiguration.MaxDepthExceededBehavior.ABORT), true);
        long sizeInBytes = 0L;
        for (Map.Entry o : this.map.entrySet()) {
            Map.Entry entry = o;
            Element element = (Element)entry.getValue();
            if (element == null) continue;
            Size size = defaultSizeOfEngine.sizeOf(entry.getKey(), element, null);
            sizeInBytes += size.getCalculated();
        }
        return sizeInBytes;
    }

    protected final void evict(Element element) throws CacheException {
        if (this.cache.getCacheConfiguration().isOverflowToDisk()) {
            if (!element.isSerializable()) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Object with key " + element.getObjectKey() + " is not Serializable and cannot be overflowed to disk");
                }
                this.cache.getCacheEventNotificationService().notifyElementEvicted(this.copyStrategyHandler.copyElementForReadIfNeeded(element), false);
            } else {
                this.spoolToDisk(element);
            }
        } else {
            this.evictionObserver.begin();
            this.evictionObserver.end(CacheOperationOutcomes.EvictionOutcome.SUCCESS);
            this.cache.getCacheEventNotificationService().notifyElementEvicted(this.copyStrategyHandler.copyElementForReadIfNeeded(element), false);
        }
    }

    protected final void notifyExpiry(Element element) {
        this.cache.getCacheEventNotificationService().notifyElementExpiry(this.copyStrategyHandler.copyElementForReadIfNeeded(element), false);
    }

    protected final boolean isFull() {
        return this.maximumSize > 0L && (long)this.map.size() > this.maximumSize;
    }

    @Override
    public void expireElements() {
    }

    @Override
    public boolean bufferFull() {
        return false;
    }

    Map getBackingMap() {
        return this.map;
    }

    @Override
    public Object getMBean() {
        return null;
    }

    public Policy getEvictionPolicy() {
        return new LruPolicy();
    }

    public void setEvictionPolicy(Policy policy) {
        throw new UnsupportedOperationException("This store is LRU only. It does not support changing the eviction strategy.");
    }

    @Override
    public Object getInternalContext() {
        return null;
    }

    @Override
    public boolean containsKeyInMemory(Object key) {
        return this.containsKey(key);
    }

    @Override
    public boolean containsKeyOffHeap(Object key) {
        return false;
    }

    @Override
    public boolean containsKeyOnDisk(Object key) {
        return false;
    }

    @Override
    public Policy getInMemoryEvictionPolicy() {
        return this.getEvictionPolicy();
    }

    @Override
    @Statistic(name="size", tags={"local-heap"})
    public int getInMemorySize() {
        return this.getSize();
    }

    @Override
    @Statistic(name="size-in-bytes", tags={"local-heap"})
    public long getInMemorySizeInBytes() {
        return this.getSizeInBytes();
    }

    @Override
    public int getOffHeapSize() {
        return 0;
    }

    @Override
    public long getOffHeapSizeInBytes() {
        return 0L;
    }

    @Override
    public int getOnDiskSize() {
        return 0;
    }

    @Override
    public long getOnDiskSizeInBytes() {
        return 0L;
    }

    @Override
    public void setInMemoryEvictionPolicy(Policy policy) {
        this.setEvictionPolicy(policy);
    }

    @Override
    public Element putIfAbsent(Element element) throws NullPointerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Element removeElement(Element element, ElementValueComparator comparator) throws NullPointerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean replace(Element old, Element element, ElementValueComparator comparator) throws NullPointerException, IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Element replace(Element element) throws NullPointerException {
        throw new UnsupportedOperationException();
    }

    public final class SpoolingLinkedHashMap
    extends LinkedHashMap {
        private static final int INITIAL_CAPACITY = 100;
        private static final float GROWTH_FACTOR = 0.75f;

        public SpoolingLinkedHashMap() {
            super(100, 0.75f, true);
        }

        protected final boolean removeEldestEntry(Map.Entry eldest) {
            Element element = (Element)eldest.getValue();
            return element != null && this.removeLeastRecentlyUsedElement(element);
        }

        @Override
        public Object put(Object key, Object value) {
            Object put = super.put(key, value);
            Iterator it = this.entrySet().iterator();
            while (LruMemoryStore.this.isFull() && it.hasNext()) {
                Map.Entry entry = it.next();
                if (!this.removeEldestEntry(entry)) continue;
                it.remove();
            }
            return put;
        }

        private boolean removeLeastRecentlyUsedElement(Element element) throws CacheException {
            if (element.isExpired()) {
                LruMemoryStore.this.notifyExpiry(element);
                return true;
            }
            if (LruMemoryStore.this.isFull() && !LruMemoryStore.this.cachePinned) {
                LruMemoryStore.this.evict(element);
                return true;
            }
            return false;
        }
    }
}

