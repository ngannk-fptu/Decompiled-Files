/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheOperationOutcomes;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.concurrent.CacheLockProvider;
import net.sf.ehcache.concurrent.ReadWriteLockSync;
import net.sf.ehcache.concurrent.Sync;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheConfigurationListener;
import net.sf.ehcache.config.PinningConfiguration;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import net.sf.ehcache.event.RegisteredEventListeners;
import net.sf.ehcache.pool.Pool;
import net.sf.ehcache.pool.PoolAccessor;
import net.sf.ehcache.pool.PoolParticipant;
import net.sf.ehcache.pool.Size;
import net.sf.ehcache.pool.SizeOfEngine;
import net.sf.ehcache.pool.SizeOfEngineLoader;
import net.sf.ehcache.pool.impl.UnboundedPool;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.search.impl.SearchManager;
import net.sf.ehcache.statistics.StatisticBuilder;
import net.sf.ehcache.store.AbstractPolicy;
import net.sf.ehcache.store.AbstractStore;
import net.sf.ehcache.store.BruteForceSearchManager;
import net.sf.ehcache.store.BruteForceSource;
import net.sf.ehcache.store.CopyStrategyHandler;
import net.sf.ehcache.store.CopyingBruteForceSource;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.FifoPolicy;
import net.sf.ehcache.store.LfuPolicy;
import net.sf.ehcache.store.LruPolicy;
import net.sf.ehcache.store.MemoryStoreBruteForceSource;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import net.sf.ehcache.store.Policy;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.store.StoreOperationOutcomes;
import net.sf.ehcache.store.TransactionalBruteForceSource;
import net.sf.ehcache.store.TxCopyStrategyHandler;
import net.sf.ehcache.store.chm.SelectableConcurrentHashMap;
import net.sf.ehcache.store.disk.StoreUpdateException;
import net.sf.ehcache.writer.CacheWriterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.statistics.OperationStatistic;
import org.terracotta.statistics.Statistic;
import org.terracotta.statistics.StatisticsManager;
import org.terracotta.statistics.derived.EventRateSimpleMovingAverage;
import org.terracotta.statistics.derived.OperationResultFilter;
import org.terracotta.statistics.observer.OperationObserver;

public class MemoryStore
extends AbstractStore
implements CacheConfigurationListener,
Store {
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final int CONCURRENCY_LEVEL = 100;
    private static final int MAX_EVICTION_RATIO = 5;
    private static final Logger LOG = LoggerFactory.getLogger((String)MemoryStore.class.getName());
    private static final CopyStrategyHandler NO_COPY_STRATEGY_HANDLER = new CopyStrategyHandler(false, false, null, null);
    protected final OperationObserver<CacheOperationOutcomes.EvictionOutcome> evictionObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(CacheOperationOutcomes.EvictionOutcome.class).named("eviction")).of(this)).build();
    private final Ehcache cache;
    private final SelectableConcurrentHashMap map;
    private final PoolAccessor poolAccessor;
    private final OperationObserver<StoreOperationOutcomes.GetOutcome> getObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(StoreOperationOutcomes.GetOutcome.class).named("get")).of(this)).tag(new String[]{"local-heap"})).build();
    private final OperationObserver<StoreOperationOutcomes.PutOutcome> putObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(StoreOperationOutcomes.PutOutcome.class).named("put")).of(this)).tag(new String[]{"local-heap"})).build();
    private final OperationObserver<StoreOperationOutcomes.RemoveOutcome> removeObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(StoreOperationOutcomes.RemoveOutcome.class).named("remove")).of(this)).tag(new String[]{"local-heap"})).build();
    private final boolean storePinned;
    private final CopyStrategyHandler copyStrategyHandler;
    private volatile int maximumSize;
    private volatile Status status = Status.STATUS_UNINITIALISED;
    private volatile Policy policy;
    private volatile CacheLockProvider lockProvider;

    protected MemoryStore(Ehcache cache, Pool pool, BackingFactory factory, SearchManager searchManager) {
        super(searchManager, cache.getName());
        this.cache = cache;
        this.maximumSize = (int)cache.getCacheConfiguration().getMaxEntriesLocalHeap();
        this.policy = MemoryStore.determineEvictionPolicy(cache);
        this.poolAccessor = pool instanceof UnboundedPool ? pool.createPoolAccessor(null, null) : pool.createPoolAccessor(new Participant(), SizeOfPolicyConfiguration.resolveMaxDepth(cache), SizeOfPolicyConfiguration.resolveBehavior(cache).equals((Object)SizeOfPolicyConfiguration.MaxDepthExceededBehavior.ABORT));
        this.storePinned = this.determineStorePinned(cache.getCacheConfiguration());
        int maximumCapacity = this.isClockEviction() && !this.storePinned ? this.maximumSize : 0;
        RegisteredEventListeners eventListener = cache.getCacheEventNotificationService();
        if (Boolean.getBoolean(MemoryStore.class.getName() + ".presize")) {
            float loadFactor = this.maximumSize == 1 ? 1.0f : 0.75f;
            int initialCapacity = MemoryStore.getInitialCapacityForLoadFactor(this.maximumSize, loadFactor);
            this.map = factory.newBackingMap(this.poolAccessor, initialCapacity, loadFactor, 100, maximumCapacity, eventListener);
        } else {
            this.map = factory.newBackingMap(this.poolAccessor, 100, maximumCapacity, eventListener);
        }
        this.status = Status.STATUS_ALIVE;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Initialized " + this.getClass().getName() + " for " + cache.getName());
        }
        this.copyStrategyHandler = MemoryStore.getCopyStrategyHandler(cache);
    }

    static CopyStrategyHandler getCopyStrategyHandler(Ehcache cache) {
        if (cache.getCacheConfiguration().isXaTransactional() || cache.getCacheConfiguration().isXaStrictTransactional() || cache.getCacheConfiguration().isLocalTransactional()) {
            return new TxCopyStrategyHandler(cache.getCacheConfiguration().isCopyOnRead(), cache.getCacheConfiguration().isCopyOnWrite(), cache.getCacheConfiguration().getCopyStrategy(), cache.getCacheConfiguration().getClassLoader());
        }
        if (cache.getCacheConfiguration().isCopyOnRead() || cache.getCacheConfiguration().isCopyOnWrite()) {
            return new CopyStrategyHandler(cache.getCacheConfiguration().isCopyOnRead(), cache.getCacheConfiguration().isCopyOnWrite(), cache.getCacheConfiguration().getCopyStrategy(), cache.getCacheConfiguration().getClassLoader());
        }
        return NO_COPY_STRATEGY_HANDLER;
    }

    private boolean determineStorePinned(CacheConfiguration cacheConfiguration) {
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

    protected static int getInitialCapacityForLoadFactor(int maximumSizeGoal, float loadFactor) {
        double actualMaximum = Math.ceil((float)maximumSizeGoal / loadFactor);
        return Math.max(0, actualMaximum >= 2.147483647E9 ? Integer.MAX_VALUE : (int)actualMaximum);
    }

    public static Store create(Ehcache cache, Pool pool) {
        CacheConfiguration cacheConfiguration = cache.getCacheConfiguration();
        BruteForceSearchManager searchManager = new BruteForceSearchManager(cache);
        MemoryStore memoryStore = new MemoryStore(cache, pool, new BasicBackingFactory(), searchManager);
        cacheConfiguration.addConfigurationListener(memoryStore);
        searchManager.setBruteForceSource(MemoryStore.createBruteForceSource(memoryStore, cache.getCacheConfiguration()));
        return memoryStore;
    }

    protected static BruteForceSource createBruteForceSource(MemoryStore memoryStore, CacheConfiguration cacheConfiguration) {
        BruteForceSource source = new MemoryStoreBruteForceSource(memoryStore, cacheConfiguration.getSearchable());
        CopyStrategyHandler copyStrategyHandler = new CopyStrategyHandler(cacheConfiguration.isCopyOnRead(), cacheConfiguration.isCopyOnWrite(), cacheConfiguration.getCopyStrategy(), cacheConfiguration.getClassLoader());
        if (cacheConfiguration.getTransactionalMode().isTransactional()) {
            source = new TransactionalBruteForceSource(source, copyStrategyHandler);
        } else if (cacheConfiguration.isCopyOnRead() || cacheConfiguration.isCopyOnWrite()) {
            source = new CopyingBruteForceSource(source, copyStrategyHandler);
        }
        return source;
    }

    @Override
    public boolean put(Element element) throws CacheException {
        if (element == null) {
            return false;
        }
        if (this.searchManager != null) {
            this.searchManager.put(this.cache.getName(), -1, element, null, this.attributeExtractors, this.cache.getCacheConfiguration().getDynamicExtractor());
        }
        this.putObserver.begin();
        long delta = this.poolAccessor.add(element.getObjectKey(), element.getObjectValue(), this.map.storedObject(element), this.storePinned);
        if (delta > -1L) {
            Element old = this.map.put(element.getObjectKey(), element, delta);
            this.checkCapacity(element);
            if (old == null) {
                this.putObserver.end(StoreOperationOutcomes.PutOutcome.ADDED);
                return true;
            }
            this.putObserver.end(StoreOperationOutcomes.PutOutcome.UPDATED);
            return false;
        }
        this.notifyDirectEviction(element);
        this.putObserver.end(StoreOperationOutcomes.PutOutcome.ADDED);
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean putWithWriter(Element element, CacheWriterManager writerManager) throws CacheException {
        long delta;
        if (this.searchManager != null) {
            this.searchManager.put(this.cache.getName(), -1, element, null, this.attributeExtractors, this.cache.getCacheConfiguration().getDynamicExtractor());
        }
        if ((delta = this.poolAccessor.add(element.getObjectKey(), element.getObjectValue(), this.map.storedObject(element), this.storePinned)) > -1L) {
            Element old;
            block7: {
                ReentrantReadWriteLock lock = this.map.lockFor(element.getObjectKey());
                lock.writeLock().lock();
                try {
                    old = this.map.put(element.getObjectKey(), element, delta);
                    if (writerManager == null) break block7;
                    try {
                        writerManager.put(element);
                    }
                    catch (RuntimeException e) {
                        throw new StoreUpdateException(e, old != null);
                    }
                }
                finally {
                    lock.writeLock().unlock();
                }
            }
            this.checkCapacity(element);
            return old == null;
        }
        this.notifyDirectEviction(element);
        return true;
    }

    @Override
    public final Element get(Object key) {
        this.getObserver.begin();
        if (key == null) {
            this.getObserver.end(StoreOperationOutcomes.GetOutcome.MISS);
            return null;
        }
        Element e = this.map.get(key);
        if (e == null) {
            this.getObserver.end(StoreOperationOutcomes.GetOutcome.MISS);
            return null;
        }
        this.getObserver.end(StoreOperationOutcomes.GetOutcome.HIT);
        return e;
    }

    @Override
    public final Element getQuiet(Object key) {
        return this.map.get(key);
    }

    @Override
    public Element remove(Object key) {
        if (key == null) {
            return null;
        }
        this.removeObserver.begin();
        try {
            Element element = this.map.remove(key);
            return element;
        }
        finally {
            this.removeObserver.end(StoreOperationOutcomes.RemoveOutcome.SUCCESS);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final Element removeWithWriter(Object key, CacheWriterManager writerManager) throws CacheException {
        Element element;
        if (key == null) {
            return null;
        }
        ReentrantReadWriteLock.WriteLock writeLock = this.map.lockFor(key).writeLock();
        writeLock.lock();
        try {
            element = this.map.remove(key);
            if (writerManager != null) {
                writerManager.remove(new CacheEntry(key, element));
            }
        }
        finally {
            writeLock.unlock();
        }
        if (element == null && LOG.isDebugEnabled()) {
            LOG.debug(this.cache.getName() + "Cache: Cannot remove entry as key " + key + " was not found");
        }
        return element;
    }

    @Override
    public final boolean bufferFull() {
        return false;
    }

    @Override
    public void expireElements() {
        for (Object key : this.keySet()) {
            Element element = this.expireElement(key);
            if (element == null) continue;
            this.cache.getCacheEventNotificationService().notifyElementExpiry(this.copyStrategyHandler.copyElementForReadIfNeeded(element), false);
        }
    }

    protected Element expireElement(Object key) {
        Element value = this.get(key);
        return value != null && value.isExpired() && this.map.remove(key, value) ? value : null;
    }

    static Policy determineEvictionPolicy(Ehcache cache) {
        MemoryStoreEvictionPolicy policySelection = cache.getCacheConfiguration().getMemoryStoreEvictionPolicy();
        if (policySelection.equals(MemoryStoreEvictionPolicy.LRU)) {
            return new LruPolicy();
        }
        if (policySelection.equals(MemoryStoreEvictionPolicy.FIFO)) {
            return new FifoPolicy();
        }
        if (policySelection.equals(MemoryStoreEvictionPolicy.LFU)) {
            return new LfuPolicy();
        }
        if (policySelection.equals(MemoryStoreEvictionPolicy.CLOCK)) {
            return null;
        }
        throw new IllegalArgumentException(policySelection + " isn't a valid eviction policy");
    }

    @Override
    public final void removeAll() throws CacheException {
        for (Object key : this.map.keySet()) {
            this.remove(key);
        }
    }

    @Override
    public synchronized void dispose() {
        if (this.status.equals(Status.STATUS_SHUTDOWN)) {
            return;
        }
        this.status = Status.STATUS_SHUTDOWN;
        this.flush();
        this.poolAccessor.unlink();
    }

    @Override
    public void flush() {
        if (this.cache.getCacheConfiguration().isClearOnFlush()) {
            this.removeAll();
        }
    }

    @Override
    public final List<?> getKeys() {
        return new ArrayList<Object>(this.map.keySet());
    }

    protected Set<?> keySet() {
        return this.map.keySet();
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

    private void notifyExpiry(Element element) {
        this.cache.getCacheEventNotificationService().notifyElementExpiry(this.copyStrategyHandler.copyElementForReadIfNeeded(element), false);
    }

    protected void notifyDirectEviction(Element element) {
        this.evictionObserver.begin();
        this.evictionObserver.end(CacheOperationOutcomes.EvictionOutcome.SUCCESS);
        this.cache.getCacheEventNotificationService().notifyElementEvicted(this.copyStrategyHandler.copyElementForReadIfNeeded(element), false);
    }

    public final boolean isFull() {
        return this.maximumSize > 0 && this.map.quickSize() >= this.maximumSize;
    }

    public final boolean canPutWithoutEvicting(Element element) {
        if (element == null) {
            return true;
        }
        return !this.isFull() && this.poolAccessor.canAddWithoutEvicting(element.getObjectKey(), element.getObjectValue(), this.map.storedObject(element));
    }

    private void checkCapacity(Element elementJustAdded) {
        if (this.maximumSize > 0 && !this.isClockEviction()) {
            int evict = Math.min(this.map.quickSize() - this.maximumSize, 5);
            for (int i = 0; i < evict; ++i) {
                this.removeElementChosenByEvictionPolicy(elementJustAdded);
            }
        }
    }

    private boolean removeElementChosenByEvictionPolicy(Element elementJustAdded) {
        if (this.policy == null) {
            return this.map.evict();
        }
        Element element = this.findEvictionCandidate(elementJustAdded);
        if (element == null) {
            LOG.debug("Eviction selection miss. Selected element is null");
            return false;
        }
        if (element.isExpired()) {
            return this.expire(element);
        }
        if (this.storePinned) {
            return false;
        }
        return this.evict(element);
    }

    private Element findEvictionCandidate(Element elementJustAdded) {
        Object objectKey = elementJustAdded != null ? elementJustAdded.getObjectKey() : null;
        Element[] elements = this.sampleElements(objectKey);
        return this.policy.selectedBasedOnPolicy(elements, elementJustAdded);
    }

    private Element[] sampleElements(Object keyHint) {
        int size = AbstractPolicy.calculateSampleSize(this.map.quickSize());
        return this.map.getRandomValues(size, keyHint);
    }

    @Override
    public Object getInternalContext() {
        if (this.lockProvider != null) {
            return this.lockProvider;
        }
        this.lockProvider = new LockProvider();
        return this.lockProvider;
    }

    @Override
    public final Status getStatus() {
        return this.status;
    }

    @Override
    public void timeToIdleChanged(long oldTti, long newTti) {
    }

    @Override
    public void timeToLiveChanged(long oldTtl, long newTtl) {
    }

    @Override
    public void diskCapacityChanged(int oldCapacity, int newCapacity) {
    }

    @Override
    public void loggingChanged(boolean oldValue, boolean newValue) {
    }

    @Override
    public void memoryCapacityChanged(int oldCapacity, int newCapacity) {
        this.maximumSize = newCapacity;
        if (this.isClockEviction() && !this.storePinned) {
            this.map.setMaxSize(this.maximumSize);
        }
    }

    private boolean isClockEviction() {
        return this.policy == null;
    }

    @Override
    public void registered(CacheConfiguration config) {
    }

    @Override
    public void deregistered(CacheConfiguration config) {
    }

    @Override
    public void maxBytesLocalHeapChanged(long oldValue, long newValue) {
        this.poolAccessor.setMaxSize(newValue);
    }

    @Override
    public void maxBytesLocalDiskChanged(long oldValue, long newValue) {
    }

    @Override
    public void maxEntriesInCacheChanged(long oldValue, long newValue) {
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
        return this.policy;
    }

    @Override
    @Statistic(name="size", tags={"local-heap"})
    public int getInMemorySize() {
        return this.getSize();
    }

    @Override
    @Statistic(name="size-in-bytes", tags={"local-heap"})
    public long getInMemorySizeInBytes() {
        if (this.poolAccessor.getSize() < 0L) {
            SizeOfEngine defaultSizeOfEngine = SizeOfEngineLoader.newSizeOfEngine(SizeOfPolicyConfiguration.resolveMaxDepth(this.cache), SizeOfPolicyConfiguration.resolveBehavior(this.cache).equals((Object)SizeOfPolicyConfiguration.MaxDepthExceededBehavior.ABORT), true);
            long sizeInBytes = 0L;
            for (Element o : this.map.values()) {
                Element element = o;
                if (element == null) continue;
                Size size = defaultSizeOfEngine.sizeOf(element.getObjectKey(), element, this.map.storedObject(element));
                sizeInBytes += size.getCalculated();
            }
            return sizeInBytes;
        }
        return this.poolAccessor.getSize();
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
    public boolean hasAbortedSizeOf() {
        return this.poolAccessor.hasAbortedSizeOf();
    }

    @Override
    public void setInMemoryEvictionPolicy(Policy policy) {
        this.policy = policy;
    }

    @Override
    public void setAttributeExtractors(Map<String, AttributeExtractor> extractors) {
        super.setAttributeExtractors(extractors);
        HashSet attrs = new HashSet(this.attributeExtractors.size());
        for (String name : extractors.keySet()) {
            attrs.add(new Attribute(name));
        }
        ((BruteForceSearchManager)this.searchManager).addSearchAttributes(attrs);
    }

    @Override
    public Element putIfAbsent(Element element) throws NullPointerException {
        long delta;
        if (element == null) {
            return null;
        }
        if (this.searchManager != null) {
            this.searchManager.put(this.cache.getName(), -1, element, null, this.attributeExtractors, this.cache.getCacheConfiguration().getDynamicExtractor());
        }
        if ((delta = this.poolAccessor.add(element.getObjectKey(), element.getObjectValue(), this.map.storedObject(element), this.storePinned)) > -1L) {
            Element old = this.map.putIfAbsent(element.getObjectKey(), element, delta);
            if (old == null) {
                this.checkCapacity(element);
            } else {
                this.poolAccessor.delete(delta);
            }
            return old;
        }
        this.notifyDirectEviction(element);
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean evict(Element element) {
        ReentrantReadWriteLock.WriteLock lock = this.map.lockFor(element.getObjectKey()).writeLock();
        if (lock.tryLock()) {
            Element remove;
            this.evictionObserver.begin();
            try {
                remove = this.remove(element.getObjectKey());
            }
            finally {
                lock.unlock();
            }
            if (remove != null) {
                this.evictionObserver.end(CacheOperationOutcomes.EvictionOutcome.SUCCESS);
                this.cache.getCacheEventNotificationService().notifyElementEvicted(this.copyStrategyHandler.copyElementForReadIfNeeded(remove), false);
            }
            return remove != null;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean expire(Element element) {
        ReentrantReadWriteLock.WriteLock lock = this.map.lockFor(element.getObjectKey()).writeLock();
        if (lock.tryLock()) {
            Element remove;
            try {
                remove = this.remove(element.getObjectKey());
            }
            finally {
                lock.unlock();
            }
            if (remove != null) {
                if (remove.isExpired()) {
                    this.notifyExpiry(remove);
                } else {
                    this.notifyDirectEviction(remove);
                }
            }
            return remove != null;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element removeElement(Element element, ElementValueComparator comparator) throws NullPointerException {
        if (element == null || element.getObjectKey() == null) {
            return null;
        }
        Object key = element.getObjectKey();
        Lock lock = this.getWriteLock(key);
        lock.lock();
        try {
            Element toRemove = this.map.get(key);
            if (comparator.equals(element, toRemove)) {
                this.map.remove(key);
                Element element2 = toRemove;
                return element2;
            }
            Element element3 = null;
            return element3;
        }
        finally {
            lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean replace(Element old, Element element, ElementValueComparator comparator) throws NullPointerException, IllegalArgumentException {
        if (element == null || element.getObjectKey() == null) {
            return false;
        }
        if (this.searchManager != null) {
            this.searchManager.put(this.cache.getName(), -1, element, null, this.attributeExtractors, this.cache.getCacheConfiguration().getDynamicExtractor());
        }
        Object key = element.getObjectKey();
        long delta = this.poolAccessor.add(element.getObjectKey(), element.getObjectValue(), this.map.storedObject(element), this.storePinned);
        if (delta > -1L) {
            Lock lock = this.getWriteLock(key);
            lock.lock();
            try {
                Element toRemove = this.map.get(key);
                if (comparator.equals(old, toRemove)) {
                    this.map.put(key, element, delta);
                    boolean bl = true;
                    return bl;
                }
                this.poolAccessor.delete(delta);
                boolean bl = false;
                return bl;
            }
            finally {
                lock.unlock();
            }
        }
        this.notifyDirectEviction(element);
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element replace(Element element) throws NullPointerException {
        if (element == null || element.getObjectKey() == null) {
            return null;
        }
        if (this.searchManager != null) {
            this.searchManager.put(this.cache.getName(), -1, element, null, this.attributeExtractors, this.cache.getCacheConfiguration().getDynamicExtractor());
        }
        Object key = element.getObjectKey();
        long delta = this.poolAccessor.add(element.getObjectKey(), element.getObjectValue(), this.map.storedObject(element), this.storePinned);
        if (delta > -1L) {
            Lock lock = this.getWriteLock(key);
            lock.lock();
            try {
                Element toRemove = this.map.get(key);
                if (toRemove != null) {
                    this.map.put(key, element, delta);
                    Element element2 = toRemove;
                    return element2;
                }
                this.poolAccessor.delete(delta);
                Element element3 = null;
                return element3;
            }
            finally {
                lock.unlock();
            }
        }
        this.notifyDirectEviction(element);
        return null;
    }

    @Override
    public Object getMBean() {
        return null;
    }

    private Lock getWriteLock(Object key) {
        return this.map.lockFor(key).writeLock();
    }

    public Collection<Element> elementSet() {
        return this.map.values();
    }

    private static boolean getAdvancedBooleanConfigProperty(String property, String cacheName, boolean defaultValue) {
        String globalPropertyKey = "net.sf.ehcache.store.config." + property;
        String cachePropertyKey = "net.sf.ehcache.store." + cacheName + ".config." + property;
        return Boolean.parseBoolean(System.getProperty(cachePropertyKey, System.getProperty(globalPropertyKey, Boolean.toString(defaultValue))));
    }

    @Override
    public void recalculateSize(Object key) {
        if (key == null) {
            return;
        }
        this.map.recalculateSize(key);
    }

    static class BasicBackingFactory
    implements BackingFactory {
        BasicBackingFactory() {
        }

        @Override
        public SelectableConcurrentHashMap newBackingMap(PoolAccessor poolAccessor, int concurrency, int maximumCapacity, RegisteredEventListeners eventListener) {
            return new SelectableConcurrentHashMap(poolAccessor, concurrency, maximumCapacity, eventListener);
        }

        @Override
        public SelectableConcurrentHashMap newBackingMap(PoolAccessor poolAccessor, int initialCapacity, float loadFactor, int concurrency, int maximumCapacity, RegisteredEventListeners eventListener) {
            return new SelectableConcurrentHashMap(poolAccessor, initialCapacity, loadFactor, concurrency, maximumCapacity, eventListener);
        }
    }

    protected static interface BackingFactory {
        @Deprecated
        public SelectableConcurrentHashMap newBackingMap(PoolAccessor var1, int var2, float var3, int var4, int var5, RegisteredEventListeners var6);

        public SelectableConcurrentHashMap newBackingMap(PoolAccessor var1, int var2, int var3, RegisteredEventListeners var4);
    }

    private final class Participant
    implements PoolParticipant {
        private final EventRateSimpleMovingAverage hitRate = new EventRateSimpleMovingAverage(1L, TimeUnit.SECONDS);
        private final EventRateSimpleMovingAverage missRate = new EventRateSimpleMovingAverage(1L, TimeUnit.SECONDS);

        private Participant() {
            OperationStatistic<StoreOperationOutcomes.GetOutcome> getStatistic = StatisticsManager.getOperationStatisticFor(MemoryStore.this.getObserver);
            getStatistic.addDerivedStatistic((StoreOperationOutcomes.GetOutcome)((Object)new OperationResultFilter<StoreOperationOutcomes.GetOutcome>(EnumSet.of(StoreOperationOutcomes.GetOutcome.HIT), this.hitRate)));
            getStatistic.addDerivedStatistic((StoreOperationOutcomes.GetOutcome)((Object)new OperationResultFilter<StoreOperationOutcomes.GetOutcome>(EnumSet.of(StoreOperationOutcomes.GetOutcome.MISS), this.missRate)));
        }

        @Override
        public boolean evict(int count, long size) {
            if (MemoryStore.this.storePinned) {
                return false;
            }
            for (int i = 0; i < count; ++i) {
                boolean removed = MemoryStore.this.removeElementChosenByEvictionPolicy(null);
                if (removed) continue;
                return false;
            }
            return true;
        }

        @Override
        public float getApproximateHitRate() {
            return this.hitRate.rate(TimeUnit.SECONDS).floatValue();
        }

        @Override
        public float getApproximateMissRate() {
            return this.missRate.rate(TimeUnit.SECONDS).floatValue();
        }

        @Override
        public long getApproximateCountSize() {
            return MemoryStore.this.map.quickSize();
        }
    }

    private class LockProvider
    implements CacheLockProvider {
        private LockProvider() {
        }

        @Override
        public Sync getSyncForKey(Object key) {
            return new ReadWriteLockSync(MemoryStore.this.map.lockFor(key));
        }
    }
}

