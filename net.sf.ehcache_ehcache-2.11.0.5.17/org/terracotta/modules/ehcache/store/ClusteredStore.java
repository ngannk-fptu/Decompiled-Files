/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.terracotta.toolkit.ToolkitFeatureTypeInternal
 *  org.terracotta.toolkit.atomic.ToolkitTransaction
 *  org.terracotta.toolkit.atomic.ToolkitTransactionController
 *  org.terracotta.toolkit.atomic.ToolkitTransactionType
 *  org.terracotta.toolkit.cache.ToolkitCache
 *  org.terracotta.toolkit.cache.ToolkitCacheListener
 *  org.terracotta.toolkit.collections.ToolkitMap
 *  org.terracotta.toolkit.concurrent.locks.ToolkitLock
 *  org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock
 *  org.terracotta.toolkit.internal.ToolkitInternal
 *  org.terracotta.toolkit.internal.cache.ToolkitCacheInternal
 *  org.terracotta.toolkit.internal.cache.ToolkitValueComparator
 *  org.terracotta.toolkit.internal.concurrent.locks.ToolkitLockTypeInternal
 */
package org.terracotta.modules.ehcache.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.event.EventListenerList;
import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheOperationOutcomes;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.ElementData;
import net.sf.ehcache.Status;
import net.sf.ehcache.cluster.CacheCluster;
import net.sf.ehcache.cluster.ClusterNode;
import net.sf.ehcache.cluster.ClusterTopologyListener;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.ConfigError;
import net.sf.ehcache.config.InvalidConfigurationException;
import net.sf.ehcache.config.PinningConfiguration;
import net.sf.ehcache.config.TerracottaConfiguration;
import net.sf.ehcache.event.RegisteredEventListeners;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.statistics.StatisticBuilder;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import net.sf.ehcache.store.Policy;
import net.sf.ehcache.store.StoreListener;
import net.sf.ehcache.store.StoreQuery;
import net.sf.ehcache.store.TerracottaStore;
import net.sf.ehcache.terracotta.TerracottaNotRunningException;
import net.sf.ehcache.util.SetAsList;
import net.sf.ehcache.writer.CacheWriterManager;
import net.sf.ehcache.writer.writebehind.WriteBehind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.modules.ehcache.ClusteredCacheInternalContext;
import org.terracotta.modules.ehcache.ToolkitInstanceFactory;
import org.terracotta.modules.ehcache.concurrency.TCCacheLockProvider;
import org.terracotta.modules.ehcache.store.CacheConfigChangeBridge;
import org.terracotta.modules.ehcache.store.RealObjectKeySet;
import org.terracotta.modules.ehcache.store.ValueModeHandler;
import org.terracotta.modules.ehcache.store.ValueModeHandlerFactory;
import org.terracotta.statistics.Statistic;
import org.terracotta.statistics.observer.OperationObserver;
import org.terracotta.toolkit.ToolkitFeatureTypeInternal;
import org.terracotta.toolkit.atomic.ToolkitTransaction;
import org.terracotta.toolkit.atomic.ToolkitTransactionController;
import org.terracotta.toolkit.atomic.ToolkitTransactionType;
import org.terracotta.toolkit.cache.ToolkitCache;
import org.terracotta.toolkit.cache.ToolkitCacheListener;
import org.terracotta.toolkit.collections.ToolkitMap;
import org.terracotta.toolkit.concurrent.locks.ToolkitLock;
import org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock;
import org.terracotta.toolkit.internal.ToolkitInternal;
import org.terracotta.toolkit.internal.cache.ToolkitCacheInternal;
import org.terracotta.toolkit.internal.cache.ToolkitValueComparator;
import org.terracotta.toolkit.internal.concurrent.locks.ToolkitLockTypeInternal;

public class ClusteredStore
implements TerracottaStore,
StoreListener {
    private static final Logger LOG = LoggerFactory.getLogger((String)ClusteredStore.class.getName());
    private static final String CHECK_CONTAINS_KEY_ON_PUT_PROPERTY_NAME = "ehcache.clusteredStore.checkContainsKeyOnPut";
    private static final String TRANSACTIONAL_MODE = "trasactionalMode";
    private static final String LEADER_ELECTION_LOCK_NAME = "SERVER-EVENT-SUBSCRIPTION-LOCK";
    protected static final String LEADER_NODE_ID = "LEADER-NODE-ID";
    protected final ToolkitCacheInternal<String, Serializable> backend;
    protected final ValueModeHandler valueModeHandler;
    protected final ToolkitInstanceFactory toolkitInstanceFactory;
    protected final Ehcache cache;
    protected final String fullyQualifiedCacheName;
    private final boolean checkContainsKeyOnPut;
    private final int localKeyCacheMaxsize;
    private final CacheConfiguration.TransactionalMode transactionalMode;
    private final Map<Object, String> keyLookupCache;
    private final CacheConfigChangeBridge cacheConfigChangeBridge;
    private final RegisteredEventListeners registeredEventListeners;
    private final ClusteredCacheInternalContext internalContext;
    private final CacheEventListener evictionListener;
    private EventListenerList listenerList;
    private boolean cacheEventListenerRegistered = false;
    private final ToolkitLock eventualConcurrentLock;
    private final ToolkitLock leaderElectionLock;
    private final boolean isEventual;
    private final OperationObserver<CacheOperationOutcomes.EvictionOutcome> evictionObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(CacheOperationOutcomes.EvictionOutcome.class).named("eviction")).of(this)).build();
    private final CacheCluster topology;
    private final ToolkitMap<String, Serializable> configMap;
    private final EventListenersRefresher eventListenersRefresher;
    private final ToolkitTransactionController transactionController;
    private final ToolkitTransactionType transactionType;

    public ClusteredStore(ToolkitInstanceFactory toolkitInstanceFactory, Ehcache cache, CacheCluster topology) {
        ClusteredStore.validateConfig(cache);
        this.toolkitInstanceFactory = toolkitInstanceFactory;
        this.cache = cache;
        this.fullyQualifiedCacheName = toolkitInstanceFactory.getFullyQualifiedCacheName(cache);
        this.topology = topology;
        CacheConfiguration ehcacheConfig = cache.getCacheConfiguration();
        TerracottaConfiguration terracottaConfiguration = ehcacheConfig.getTerracottaConfiguration();
        this.backend = toolkitInstanceFactory.getOrCreateToolkitCache(cache);
        this.configMap = toolkitInstanceFactory.getOrCreateClusteredStoreConfigMap(cache.getCacheManager().getName(), cache.getName());
        CacheConfiguration.TransactionalMode transactionalModeTemp = (CacheConfiguration.TransactionalMode)((Object)this.configMap.get((Object)TRANSACTIONAL_MODE));
        if (transactionalModeTemp == null) {
            this.configMap.putIfAbsent((Object)TRANSACTIONAL_MODE, (Object)ehcacheConfig.getTransactionalMode());
            transactionalModeTemp = (CacheConfiguration.TransactionalMode)((Object)this.configMap.get((Object)TRANSACTIONAL_MODE));
        }
        this.transactionalMode = transactionalModeTemp;
        this.valueModeHandler = ValueModeHandlerFactory.createValueModeHandler(this, ehcacheConfig);
        if (terracottaConfiguration.getLocalKeyCache()) {
            this.localKeyCacheMaxsize = terracottaConfiguration.getLocalKeyCacheSize();
            this.keyLookupCache = new ConcurrentHashMap<Object, String>();
        } else {
            this.localKeyCacheMaxsize = -1;
            this.keyLookupCache = null;
        }
        this.setUpWanConfig();
        ToolkitInternal toolkitInternal = (ToolkitInternal)toolkitInstanceFactory.getToolkit();
        this.checkContainsKeyOnPut = toolkitInternal.getProperties().getBoolean(CHECK_CONTAINS_KEY_ON_PUT_PROPERTY_NAME);
        LOG.info(ClusteredStore.getConcurrencyValueLogMsg(cache.getName(), this.backend.getConfiguration().getInt("concurrency")));
        this.cacheConfigChangeBridge = ClusteredStore.createConfigChangeBridge(toolkitInstanceFactory, cache, this.backend);
        this.cacheConfigChangeBridge.connectConfigs();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Initialized " + this.getClass().getName() + " for " + cache.getName());
        }
        this.registeredEventListeners = cache.getCacheEventNotificationService();
        this.leaderElectionLock = toolkitInstanceFactory.getLockForCache(cache, LEADER_ELECTION_LOCK_NAME);
        this.evictionListener = new CacheEventListener();
        this.eventListenersRefresher = new EventListenersRefresher();
        topology.addTopologyListener(this.eventListenersRefresher);
        this.notifyCacheEventListenersChanged();
        TCCacheLockProvider cacheLockProvider = new TCCacheLockProvider((ToolkitCache)this.backend, this.valueModeHandler);
        this.internalContext = new ClusteredCacheInternalContext(toolkitInstanceFactory.getToolkit(), cacheLockProvider);
        this.eventualConcurrentLock = toolkitInternal.getLock("EVENTUAL-CONCURRENT-LOCK-FOR-CLUSTERED-STORE", ToolkitLockTypeInternal.CONCURRENT);
        this.isEventual = terracottaConfiguration.getConsistency() == TerracottaConfiguration.Consistency.EVENTUAL;
        this.transactionController = (ToolkitTransactionController)toolkitInternal.getFeature(ToolkitFeatureTypeInternal.TRANSACTION);
        this.transactionType = terracottaConfiguration.isSynchronousWrites() ? ToolkitTransactionType.SYNC : ToolkitTransactionType.NORMAL;
    }

    void setUpWanConfig() {
        if (!this.cache.getCacheManager().getConfiguration().getTerracottaConfiguration().isWanEnabledTSA()) {
            this.toolkitInstanceFactory.markCacheWanDisabled(this.cache.getCacheManager().getName(), this.cache.getName());
        }
    }

    public String getFullyQualifiedCacheName() {
        return this.fullyQualifiedCacheName;
    }

    private static CacheConfigChangeBridge createConfigChangeBridge(ToolkitInstanceFactory toolkitInstanceFactory, Ehcache ehcache, ToolkitCacheInternal<String, Serializable> cache) {
        return new CacheConfigChangeBridge(toolkitInstanceFactory.getFullyQualifiedCacheName(ehcache), cache, toolkitInstanceFactory.getOrCreateConfigChangeNotifier(ehcache), ehcache.getCacheConfiguration());
    }

    private static void validateConfig(Ehcache ehcache) {
        boolean cachePinned;
        CacheConfiguration cacheConfiguration = ehcache.getCacheConfiguration();
        TerracottaConfiguration terracottaConfiguration = cacheConfiguration.getTerracottaConfiguration();
        ArrayList<ConfigError> errors = new ArrayList<ConfigError>();
        if (terracottaConfiguration == null || !terracottaConfiguration.isClustered()) {
            throw new InvalidConfigurationException("Cannot create clustered store for non-terracotta clustered caches");
        }
        MemoryStoreEvictionPolicy policy = cacheConfiguration.getMemoryStoreEvictionPolicy();
        if (policy == MemoryStoreEvictionPolicy.FIFO || policy == MemoryStoreEvictionPolicy.LFU) {
            errors.add(new ConfigError("Policy '" + policy + "' is not a supported memory store eviction policy."));
        }
        if (cacheConfiguration.isOverflowToDisk() && LOG.isWarnEnabled()) {
            LOG.warn("Persistence on disk on the local node is not supported with a Terracotta clustered ehcache store. Configure the Terracotta server array to be persistent instead.");
        }
        boolean bl = cachePinned = cacheConfiguration.getPinningConfiguration() != null && cacheConfiguration.getPinningConfiguration().getStore() == PinningConfiguration.Store.INCACHE;
        if (cachePinned && cacheConfiguration.getMaxEntriesInCache() != 0L) {
            errors.add(new ConfigError("Cache pinning is not supported with maxEntriesInCache"));
        }
        if (errors.size() > 0) {
            throw new InvalidConfigurationException(errors);
        }
    }

    @Override
    public void recalculateSize(Object key) {
        throw new UnsupportedOperationException("Recalculate size is not supported for Terracotta clustered caches.");
    }

    @Override
    public synchronized void addStoreListener(StoreListener listener) {
        this.removeStoreListener(listener);
        this.getEventListenerList().add(StoreListener.class, listener);
    }

    @Override
    public synchronized void removeStoreListener(StoreListener listener) {
        this.getEventListenerList().remove(StoreListener.class, listener);
    }

    private synchronized EventListenerList getEventListenerList() {
        if (this.listenerList == null) {
            this.listenerList = new EventListenerList();
        }
        return this.listenerList;
    }

    @Override
    public void clusterCoherent(boolean clusterCoherent) {
        Object[] listeners = this.getEventListenerList().getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] != StoreListener.class) continue;
            ((StoreListener)listeners[i + 1]).clusterCoherent(clusterCoherent);
        }
    }

    @Override
    public boolean put(Element element) throws CacheException {
        return this.putInternal(element);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean putWithWriter(Element element, CacheWriterManager writerManager) throws CacheException {
        if (element == null) {
            return true;
        }
        String pKey = this.generatePortableKeyFor(element.getObjectKey());
        ToolkitTransaction transaction = this.transactionController.beginTransaction(this.transactionType);
        try {
            boolean bl;
            ToolkitLock lock = this.getLockForKey(pKey);
            lock.lock();
            try {
                writerManager.put(element);
                bl = this.putInternal(element);
            }
            catch (Throwable throwable) {
                lock.unlock();
                throw throwable;
            }
            lock.unlock();
            return bl;
        }
        finally {
            transaction.commit();
        }
    }

    private boolean putInternal(Element element) throws CacheException {
        if (element == null) {
            return true;
        }
        String pKey = this.generatePortableKeyFor(element.getObjectKey());
        if (element.usesCacheDefaultLifespan()) {
            return this.doPut(pKey, element);
        }
        return this.doPutWithCustomLifespan(pKey, element);
    }

    @Override
    public void putAll(Collection<Element> elements) throws CacheException {
        HashMap<String, ElementData> entries = new HashMap<String, ElementData>();
        for (Element element : elements) {
            String pKey = this.generatePortableKeyFor(element.getObjectKey());
            if (!element.usesCacheDefaultLifespan()) {
                this.doPutWithCustomLifespan(pKey, element);
                continue;
            }
            ElementData elementData = this.valueModeHandler.createElementData(element);
            entries.put(pKey, elementData);
        }
        this.backend.putAll(entries);
    }

    @Override
    public Element get(Object key) {
        String pKey = this.generatePortableKeyFor(key);
        Serializable value = (Serializable)this.backend.get((Object)pKey);
        if (value == null) {
            return null;
        }
        return this.valueModeHandler.createElement(key, value);
    }

    @Override
    public Element getQuiet(Object key) {
        String pKey = this.generatePortableKeyFor(key);
        Serializable value = (Serializable)this.backend.getQuiet((Object)pKey);
        if (value == null) {
            return null;
        }
        return this.valueModeHandler.createElement(key, value);
    }

    @Override
    public List getKeys() {
        return Collections.unmodifiableList(new SetAsList(new RealObjectKeySet(this.valueModeHandler, this.backend.keySet())));
    }

    @Override
    public Element remove(Object key) {
        if (key == null) {
            return null;
        }
        String pKey = this.generatePortableKeyFor(key);
        Serializable value = (Serializable)this.backend.remove((Object)pKey);
        Element element = this.valueModeHandler.createElement(key, value);
        if (this.keyLookupCache != null) {
            this.keyLookupCache.remove(key);
        }
        if (element != null) {
            return element;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(this.cache.getName() + " Cache: Cannot remove entry as key " + key + " was not found");
        }
        return null;
    }

    @Override
    public void removeAll(Collection<?> keys) {
        HashSet<String> entries = new HashSet<String>();
        for (Object key : keys) {
            String pKey = this.generatePortableKeyFor(key);
            entries.add(pKey);
        }
        this.backend.removeAll(entries);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element removeWithWriter(Object key, CacheWriterManager writerManager) throws CacheException {
        if (key == null) {
            return null;
        }
        String pKey = this.generatePortableKeyFor(key);
        ToolkitLock lock = this.getLockForKey(pKey);
        ToolkitTransaction transaction = this.transactionController.beginTransaction(this.transactionType);
        try {
            Element element;
            lock.lock();
            try {
                writerManager.remove(new CacheEntry(key, this.get(key)));
                element = this.remove(key);
            }
            catch (Throwable throwable) {
                lock.unlock();
                throw throwable;
            }
            lock.unlock();
            return element;
        }
        finally {
            transaction.commit();
        }
    }

    @Override
    public void removeAll() throws CacheException {
        this.backend.clear();
        if (this.keyLookupCache != null) {
            this.keyLookupCache.clear();
        }
    }

    @Override
    public Element putIfAbsent(Element element) throws NullPointerException {
        ElementData value;
        String pKey = this.generatePortableKeyFor(element.getObjectKey());
        Serializable data = (Serializable)this.backend.putIfAbsent((Object)pKey, (Object)(value = this.valueModeHandler.createElementData(element)));
        return data == null ? null : this.valueModeHandler.createElement(element.getKey(), data);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element removeElement(Element element, ElementValueComparator comparator) throws NullPointerException {
        if (this.isEventual) {
            return this.removeElementEventual(element, comparator);
        }
        String pKey = this.generatePortableKeyFor(element.getKey());
        ToolkitReadWriteLock lock = this.backend.createLockForKey((Object)pKey);
        lock.writeLock().lock();
        try {
            Element oldElement = this.getQuiet(element.getKey());
            if (comparator.equals(oldElement, element)) {
                Element element2 = this.remove(element.getKey());
                return element2;
            }
        }
        finally {
            lock.writeLock().unlock();
        }
        return null;
    }

    private Element removeElementEventual(Element element, ElementValueComparator comparator) {
        ElementData value;
        String pKey = this.generatePortableKeyFor(element.getKey());
        if (this.backend.remove((Object)pKey, (Object)(value = this.valueModeHandler.createElementData(element)), (ToolkitValueComparator)new ElementValueComparatorToolkitWrapper(element.getObjectKey(), comparator))) {
            return element;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean replace(Element old, Element element, ElementValueComparator comparator) throws NullPointerException, IllegalArgumentException {
        if (this.isEventual) {
            return this.replaceEventual(old, element, comparator);
        }
        String pKey = this.generatePortableKeyFor(element.getKey());
        ToolkitReadWriteLock lock = this.backend.createLockForKey((Object)pKey);
        lock.writeLock().lock();
        try {
            Element oldElement = this.getQuiet(element.getKey());
            if (comparator.equals(oldElement, old)) {
                boolean bl = this.putInternal(element);
                return bl;
            }
        }
        finally {
            lock.writeLock().unlock();
        }
        return false;
    }

    private boolean replaceEventual(Element old, Element element, ElementValueComparator comparator) {
        String pKey = this.generatePortableKeyFor(element.getKey());
        ElementData oldValue = this.valueModeHandler.createElementData(old);
        ElementData value = this.valueModeHandler.createElementData(element);
        return this.backend.replace((Object)pKey, (Object)oldValue, (Object)value, (ToolkitValueComparator)new ElementValueComparatorToolkitWrapper(old.getObjectKey(), comparator));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element replace(Element element) throws NullPointerException {
        if (this.isEventual) {
            return this.replaceEventual(element);
        }
        String pKey = this.generatePortableKeyFor(element.getKey());
        ToolkitReadWriteLock lock = this.backend.createLockForKey((Object)pKey);
        lock.writeLock().lock();
        try {
            Element oldElement = this.getQuiet(element.getKey());
            if (oldElement != null) {
                this.putInternal(element);
            }
            Element element2 = oldElement;
            return element2;
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    private Element replaceEventual(Element element) {
        String pKey = this.generatePortableKeyFor(element.getKey());
        ElementData value = this.valueModeHandler.createElementData(element);
        return this.valueModeHandler.createElement(element.getObjectKey(), (Serializable)this.backend.replace((Object)pKey, (Object)value));
    }

    @Override
    public void dispose() {
        try {
            this.dropLeaderStatus();
            this.topology.removeTopologyListener(this.eventListenersRefresher);
            this.backend.removeListener((ToolkitCacheListener)this.evictionListener);
            this.backend.disposeLocally();
        }
        catch (RuntimeException e) {
            if (e.getClass().getSimpleName().equals("TCNotRunningException")) {
                LOG.info("Terracotta client already shutdown", (Throwable)e);
            }
            throw e;
        }
        this.cacheConfigChangeBridge.disconnectConfigs();
        this.toolkitInstanceFactory.removeNonStopConfigforCache(this.cache);
    }

    @Override
    public int getSize() {
        return this.getTerracottaClusteredSize();
    }

    @Override
    @Statistic(name="size", tags={"local-heap"})
    public int getInMemorySize() {
        return this.backend.localOnHeapSize();
    }

    @Override
    @Statistic(name="size", tags={"local-offheap"})
    public int getOffHeapSize() {
        return this.backend.localOffHeapSize();
    }

    @Override
    public int getOnDiskSize() {
        return 0;
    }

    @Override
    public void quickClear() {
        this.backend.quickClear();
    }

    @Override
    @Statistic(name="size", tags={"remote"})
    public int quickSize() {
        return this.backend.quickSize();
    }

    @Override
    public int getTerracottaClusteredSize() {
        return this.backend.size();
    }

    @Override
    @Statistic(name="size-in-bytes", tags={"local-heap"})
    public long getInMemorySizeInBytes() {
        return this.backend.localOnHeapSizeInBytes();
    }

    @Override
    @Statistic(name="size-in-bytes", tags={"local-offheap"})
    public long getOffHeapSizeInBytes() {
        return this.backend.localOffHeapSizeInBytes();
    }

    @Override
    public long getOnDiskSizeInBytes() {
        return 0L;
    }

    @Override
    public boolean hasAbortedSizeOf() {
        return false;
    }

    @Override
    public Status getStatus() {
        return Status.STATUS_ALIVE;
    }

    @Override
    public boolean containsKey(Object key) {
        String pKey = this.generatePortableKeyFor(key);
        return this.backend.containsKey((Object)pKey);
    }

    @Override
    public boolean containsKeyOnDisk(Object key) {
        return false;
    }

    @Override
    public boolean containsKeyOffHeap(Object key) {
        String pKey = this.generatePortableKeyFor(key);
        return this.backend.containsKeyLocalOffHeap((Object)pKey);
    }

    @Override
    public boolean containsKeyInMemory(Object key) {
        String pKey = this.generatePortableKeyFor(key);
        return this.backend.containsKeyLocalOnHeap((Object)pKey);
    }

    @Override
    public void expireElements() {
    }

    @Override
    public void flush() {
        if (this.cache.getCacheConfiguration().isClearOnFlush()) {
            this.backend.clear();
            if (this.keyLookupCache != null) {
                this.keyLookupCache.clear();
            }
        }
    }

    @Override
    public boolean bufferFull() {
        return false;
    }

    @Override
    public Policy getInMemoryEvictionPolicy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setInMemoryEvictionPolicy(Policy policy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getInternalContext() {
        return this.internalContext;
    }

    @Override
    public boolean isCacheCoherent() {
        return this.isClusterCoherent();
    }

    @Override
    public boolean isClusterCoherent() throws TerracottaNotRunningException {
        return !this.backend.isBulkLoadEnabled();
    }

    @Override
    public boolean isNodeCoherent() throws TerracottaNotRunningException {
        return !this.backend.isNodeBulkLoadEnabled();
    }

    @Override
    public void setNodeCoherent(boolean coherent) throws UnsupportedOperationException, TerracottaNotRunningException {
        this.backend.setNodeBulkLoadEnabled(!coherent);
    }

    @Override
    public void waitUntilClusterCoherent() throws UnsupportedOperationException, TerracottaNotRunningException, InterruptedException {
        this.backend.waitUntilBulkLoadComplete();
    }

    @Override
    public Object getMBean() {
        return null;
    }

    @Override
    public void setAttributeExtractors(Map<String, AttributeExtractor> extractors) {
        if (!extractors.isEmpty()) {
            throw new CacheException("Search attributes only supported in enterprise edition");
        }
    }

    @Override
    public Results executeQuery(StoreQuery query) throws SearchException {
        throw new UnsupportedOperationException("Search execution unsupported in non-enterprise edition");
    }

    @Override
    public Set<Attribute> getSearchAttributes() {
        return Collections.emptySet();
    }

    @Override
    public <T> Attribute<T> getSearchAttribute(String attributeName) {
        return null;
    }

    @Override
    public Map<Object, Element> getAllQuiet(Collection<?> keys) {
        return this.doGetAll(keys, true);
    }

    @Override
    public Map<Object, Element> getAll(Collection<?> keys) {
        return this.doGetAll(keys, false);
    }

    private Map<Object, Element> doGetAll(Collection<?> keys, boolean quiet) {
        ArrayList<String> pKeys = new ArrayList<String>(keys.size());
        for (Object key : keys) {
            pKeys.add(this.generatePortableKeyFor(key));
        }
        Map values = quiet ? this.backend.getAllQuiet(pKeys) : this.backend.getAll(pKeys);
        HashMap<Object, Element> elements = new HashMap<Object, Element>();
        Set entrySet = values.entrySet();
        for (Map.Entry entry : entrySet) {
            Object key = this.valueModeHandler.getRealKeyObject((String)entry.getKey());
            elements.put(key, this.valueModeHandler.createElement(key, (Serializable)entry.getValue()));
        }
        return elements;
    }

    public String generatePortableKeyFor(Object obj) {
        String key;
        String value;
        boolean useCache = this.shouldUseCache(obj);
        if (useCache && (value = this.keyLookupCache.get(obj)) != null) {
            return value;
        }
        try {
            key = this.valueModeHandler.createPortableKey(obj);
        }
        catch (Exception e) {
            throw new CacheException(e);
        }
        if (useCache && this.keyLookupCache.size() < this.localKeyCacheMaxsize) {
            this.keyLookupCache.put(obj, key);
        }
        return key;
    }

    private boolean shouldUseCache(Object obj) {
        return this.keyLookupCache != null && !(obj instanceof String);
    }

    private boolean doPut(String portableKey, Element element) {
        ElementData value = this.valueModeHandler.createElementData(element);
        if (this.checkContainsKeyOnPut) {
            return this.backend.put((Object)portableKey, (Object)value) == null;
        }
        this.backend.putNoReturn((Object)portableKey, (Object)value);
        return true;
    }

    private boolean doPutWithCustomLifespan(String portableKey, Element element) {
        int customTTL;
        ElementData value = this.valueModeHandler.createElementData(element);
        int creationTimeInSecs = (int)(element.getCreationTime() / 1000L);
        int customTTI = element.isEternal() ? Integer.MAX_VALUE : element.getTimeToIdle();
        int n = customTTL = element.isEternal() ? Integer.MAX_VALUE : element.getTimeToLive();
        if (this.checkContainsKeyOnPut) {
            return this.backend.put((Object)portableKey, (Object)value, creationTimeInSecs, customTTI, customTTL) == null;
        }
        this.backend.putNoReturn((Object)portableKey, (Object)value, (long)creationTimeInSecs, customTTI, customTTL);
        return true;
    }

    @Override
    public Element unsafeGet(Object key) {
        String pKey = this.generatePortableKeyFor(key);
        Serializable value = (Serializable)this.backend.unsafeLocalGet((Object)pKey);
        if (value == null) {
            return null;
        }
        return this.valueModeHandler.createElement(key, value);
    }

    @Override
    public Set getLocalKeys() {
        return Collections.unmodifiableSet(new RealObjectKeySet(this.valueModeHandler, this.backend.localKeySet()));
    }

    @Override
    public CacheConfiguration.TransactionalMode getTransactionalMode() {
        return this.transactionalMode;
    }

    public boolean isSearchable() {
        return false;
    }

    public String getLeader() {
        return (String)this.configMap.get((Object)LEADER_NODE_ID);
    }

    private boolean isThisNodeLeader() {
        return this.topology.getCurrentNode().getId().equals(this.getLeader());
    }

    private void dropLeaderStatus() {
        this.leaderElectionLock.lock();
        try {
            if (this.isThisNodeLeader()) {
                this.configMap.remove((Object)LEADER_NODE_ID);
            }
        }
        finally {
            this.leaderElectionLock.unlock();
        }
    }

    private void electLeaderIfNecessary() {
        String leader;
        while ((leader = this.getLeader()) == null || this.isNotInCluster(leader)) {
            if (!this.leaderElectionLock.tryLock()) continue;
            try {
                String id = this.topology.getCurrentNode().getId();
                this.configMap.put((Object)LEADER_NODE_ID, (Object)id);
                if (!LOG.isDebugEnabled()) continue;
                LOG.debug("New server event acceptor elected: " + id);
            }
            finally {
                this.leaderElectionLock.unlock();
            }
        }
    }

    private boolean isNotInCluster(String nodeId) {
        for (ClusterNode node : this.topology.getNodes()) {
            if (!node.getId().equals(nodeId)) continue;
            return false;
        }
        return true;
    }

    private static String getConcurrencyValueLogMsg(String name, int concurrency) {
        return "Cache [" + name + "] using concurrency: " + concurrency;
    }

    @Override
    public WriteBehind createWriteBehind() {
        throw new UnsupportedOperationException();
    }

    private ToolkitLock getLockForKey(String pKey) {
        if (this.isEventual) {
            return this.eventualConcurrentLock;
        }
        return this.backend.createLockForKey((Object)pKey).writeLock();
    }

    @Override
    public synchronized void notifyCacheEventListenersChanged() {
        if (this.cache.getCacheEventNotificationService().hasCacheEventListeners() && !this.cacheEventListenerRegistered) {
            this.backend.addListener((ToolkitCacheListener)this.evictionListener);
            this.cacheEventListenerRegistered = true;
        } else if (!this.cache.getCacheEventNotificationService().hasCacheEventListeners() && this.cacheEventListenerRegistered) {
            this.dropLeaderStatus();
            this.backend.removeListener((ToolkitCacheListener)this.evictionListener);
            this.cacheEventListenerRegistered = false;
        }
    }

    private static class ElementValueComparatorToolkitWrapper
    implements ToolkitValueComparator<Serializable> {
        private final Object key;
        private final ElementValueComparator wrappedComparator;

        private ElementValueComparatorToolkitWrapper(Object key, ElementValueComparator wrappedComparator) {
            this.key = key;
            this.wrappedComparator = wrappedComparator;
        }

        public boolean equals(Serializable serializable, Serializable serializable2) {
            ElementData val1 = (ElementData)serializable;
            ElementData val2 = (ElementData)serializable2;
            return this.wrappedComparator.equals(new Element(this.key, val1.getValue()), new Element(this.key, val2.getValue()));
        }
    }

    private class EventListenersRefresher
    implements ClusterTopologyListener {
        private EventListenersRefresher() {
        }

        @Override
        public void nodeJoined(ClusterNode node) {
        }

        @Override
        public void nodeLeft(ClusterNode node) {
        }

        @Override
        public void clusterOnline(ClusterNode node) {
            ClusteredStore.this.notifyCacheEventListenersChanged();
        }

        @Override
        public void clusterOffline(ClusterNode node) {
        }

        @Override
        public void clusterRejoined(ClusterNode oldNode, ClusterNode newNode) {
        }
    }

    private class CacheEventListener
    implements ToolkitCacheListener<String> {
        private CacheEventListener() {
        }

        public void onEviction(String key) {
            ClusteredStore.this.evictionObserver.begin();
            ClusteredStore.this.evictionObserver.end(CacheOperationOutcomes.EvictionOutcome.SUCCESS);
            ClusteredStore.this.electLeaderIfNecessary();
            if (ClusteredStore.this.isThisNodeLeader()) {
                Element element = new Element(ClusteredStore.this.valueModeHandler.getRealKeyObject(key), null);
                ClusteredStore.this.registeredEventListeners.notifyElementEvicted(element, false);
            }
        }

        public void onExpiration(String key) {
            ClusteredStore.this.electLeaderIfNecessary();
            if (ClusteredStore.this.isThisNodeLeader()) {
                Element element = new Element(ClusteredStore.this.valueModeHandler.getRealKeyObject(key), null);
                ClusteredStore.this.registeredEventListeners.notifyElementExpiry(element, false);
            }
        }
    }
}

