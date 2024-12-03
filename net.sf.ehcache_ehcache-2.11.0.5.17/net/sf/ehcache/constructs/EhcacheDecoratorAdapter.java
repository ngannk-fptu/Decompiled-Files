/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.constructs;

import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.bootstrap.BootstrapCacheLoader;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.event.RegisteredEventListeners;
import net.sf.ehcache.exceptionhandler.CacheExceptionHandler;
import net.sf.ehcache.extension.CacheExtension;
import net.sf.ehcache.loader.CacheLoader;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.attribute.DynamicAttributesExtractor;
import net.sf.ehcache.statistics.StatisticsGateway;
import net.sf.ehcache.terracotta.InternalEhcache;
import net.sf.ehcache.terracotta.TerracottaNotRunningException;
import net.sf.ehcache.transaction.manager.TransactionManagerLookup;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.CacheWriterManager;
import org.terracotta.statistics.StatisticsManager;

public class EhcacheDecoratorAdapter
implements InternalEhcache {
    protected final Ehcache underlyingCache;

    public EhcacheDecoratorAdapter(Ehcache underlyingCache) {
        if (underlyingCache == null) {
            throw new NullPointerException("Underlying cache cannot be null");
        }
        StatisticsManager.associate(this).withParent(underlyingCache);
        this.underlyingCache = underlyingCache;
    }

    @Override
    public Element get(Object key) throws IllegalStateException, CacheException {
        return this.underlyingCache.get(key);
    }

    @Override
    public Map<Object, Element> getAll(Collection<?> keys) throws IllegalStateException, CacheException {
        return this.underlyingCache.getAll(keys);
    }

    @Override
    public Element get(Serializable key) throws IllegalStateException, CacheException {
        return this.underlyingCache.get(key);
    }

    @Override
    public Element getQuiet(Object key) throws IllegalStateException, CacheException {
        return this.underlyingCache.getQuiet(key);
    }

    @Override
    public Element getQuiet(Serializable key) throws IllegalStateException, CacheException {
        return this.underlyingCache.getQuiet(key);
    }

    @Override
    public void put(Element element, boolean doNotNotifyCacheReplicators) throws IllegalArgumentException, IllegalStateException, CacheException {
        this.underlyingCache.put(element, doNotNotifyCacheReplicators);
    }

    @Override
    public void put(Element element) throws IllegalArgumentException, IllegalStateException, CacheException {
        this.underlyingCache.put(element);
    }

    @Override
    public void putAll(Collection<Element> elements) throws IllegalArgumentException, IllegalStateException, CacheException {
        this.underlyingCache.putAll(elements);
    }

    @Override
    public void putQuiet(Element element) throws IllegalArgumentException, IllegalStateException, CacheException {
        this.underlyingCache.putQuiet(element);
    }

    @Override
    public void putWithWriter(Element element) throws IllegalArgumentException, IllegalStateException, CacheException {
        this.underlyingCache.putWithWriter(element);
    }

    @Override
    public boolean remove(Object key, boolean doNotNotifyCacheReplicators) throws IllegalStateException {
        return this.underlyingCache.remove(key, doNotNotifyCacheReplicators);
    }

    @Override
    public boolean remove(Object key) throws IllegalStateException {
        return this.underlyingCache.remove(key);
    }

    @Override
    public void removeAll(Collection<?> keys) throws IllegalStateException {
        this.underlyingCache.removeAll(keys);
    }

    @Override
    public void removeAll(Collection<?> keys, boolean doNotNotifyCacheReplicators) throws IllegalStateException {
        this.underlyingCache.removeAll(keys, doNotNotifyCacheReplicators);
    }

    @Override
    public boolean remove(Serializable key, boolean doNotNotifyCacheReplicators) throws IllegalStateException {
        return this.underlyingCache.remove(key, doNotNotifyCacheReplicators);
    }

    @Override
    public boolean remove(Serializable key) throws IllegalStateException {
        return this.underlyingCache.remove(key);
    }

    @Override
    public void removeAll() throws IllegalStateException, CacheException {
        this.underlyingCache.removeAll();
    }

    @Override
    public void removeAll(boolean doNotNotifyCacheReplicators) throws IllegalStateException, CacheException {
        this.underlyingCache.removeAll(doNotNotifyCacheReplicators);
    }

    @Override
    public void bootstrap() {
        this.underlyingCache.bootstrap();
    }

    @Override
    @Deprecated
    public long calculateInMemorySize() throws IllegalStateException, CacheException {
        return this.underlyingCache.calculateInMemorySize();
    }

    @Override
    @Deprecated
    public long calculateOffHeapSize() throws IllegalStateException, CacheException {
        return this.underlyingCache.calculateOffHeapSize();
    }

    @Override
    @Deprecated
    public long calculateOnDiskSize() throws IllegalStateException, CacheException {
        return this.underlyingCache.calculateOnDiskSize();
    }

    @Override
    public boolean hasAbortedSizeOf() {
        return this.underlyingCache.hasAbortedSizeOf();
    }

    @Override
    public void disableDynamicFeatures() {
        this.underlyingCache.disableDynamicFeatures();
    }

    @Override
    public void dispose() throws IllegalStateException {
        this.underlyingCache.dispose();
    }

    @Override
    public void evictExpiredElements() {
        this.underlyingCache.evictExpiredElements();
    }

    @Override
    public void flush() throws IllegalStateException, CacheException {
        this.underlyingCache.flush();
    }

    @Override
    public Element getWithLoader(Object key, CacheLoader loader, Object loaderArgument) throws CacheException {
        return this.underlyingCache.getWithLoader(key, loader, loaderArgument);
    }

    @Override
    public Map getAllWithLoader(Collection keys, Object loaderArgument) throws CacheException {
        return this.underlyingCache.getAllWithLoader(keys, loaderArgument);
    }

    @Override
    public void registerCacheLoader(CacheLoader cacheLoader) {
        this.underlyingCache.registerCacheLoader(cacheLoader);
    }

    @Override
    public void unregisterCacheLoader(CacheLoader cacheLoader) {
        this.underlyingCache.unregisterCacheLoader(cacheLoader);
    }

    @Override
    public void load(Object key) throws CacheException {
        this.underlyingCache.load(key);
    }

    @Override
    public void loadAll(Collection keys, Object argument) throws CacheException {
        this.underlyingCache.loadAll(keys, argument);
    }

    @Override
    public BootstrapCacheLoader getBootstrapCacheLoader() {
        return this.underlyingCache.getBootstrapCacheLoader();
    }

    @Override
    public CacheConfiguration getCacheConfiguration() {
        return this.underlyingCache.getCacheConfiguration();
    }

    @Override
    public RegisteredEventListeners getCacheEventNotificationService() {
        return this.underlyingCache.getCacheEventNotificationService();
    }

    @Override
    public CacheExceptionHandler getCacheExceptionHandler() {
        return this.underlyingCache.getCacheExceptionHandler();
    }

    @Override
    public CacheManager getCacheManager() {
        return this.underlyingCache.getCacheManager();
    }

    @Override
    @Deprecated
    public long getOffHeapStoreSize() throws IllegalStateException {
        return this.underlyingCache.getOffHeapStoreSize();
    }

    @Override
    @Deprecated
    public int getDiskStoreSize() throws IllegalStateException {
        return this.underlyingCache.getDiskStoreSize();
    }

    @Override
    public String getGuid() {
        return this.underlyingCache.getGuid();
    }

    @Override
    public Object getInternalContext() {
        return this.underlyingCache.getInternalContext();
    }

    @Override
    public List getKeys() throws IllegalStateException, CacheException {
        return this.underlyingCache.getKeys();
    }

    @Override
    public List getKeysNoDuplicateCheck() throws IllegalStateException {
        return this.underlyingCache.getKeysNoDuplicateCheck();
    }

    @Override
    public List getKeysWithExpiryCheck() throws IllegalStateException, CacheException {
        return this.underlyingCache.getKeysWithExpiryCheck();
    }

    @Override
    @Deprecated
    public long getMemoryStoreSize() throws IllegalStateException {
        return this.underlyingCache.getMemoryStoreSize();
    }

    @Override
    public String getName() {
        return this.underlyingCache.getName();
    }

    @Override
    public List<CacheExtension> getRegisteredCacheExtensions() {
        return this.underlyingCache.getRegisteredCacheExtensions();
    }

    @Override
    public List<CacheLoader> getRegisteredCacheLoaders() {
        return this.underlyingCache.getRegisteredCacheLoaders();
    }

    @Override
    public CacheWriter getRegisteredCacheWriter() {
        return this.underlyingCache.getRegisteredCacheWriter();
    }

    @Override
    public int getSize() throws IllegalStateException, CacheException {
        return this.underlyingCache.getSize();
    }

    @Override
    public Status getStatus() {
        return this.underlyingCache.getStatus();
    }

    @Override
    public CacheWriterManager getWriterManager() {
        return this.underlyingCache.getWriterManager();
    }

    @Override
    public void initialise() {
        this.underlyingCache.initialise();
    }

    @Override
    @Deprecated
    public boolean isClusterCoherent() {
        return this.underlyingCache.isClusterCoherent();
    }

    @Override
    public boolean isDisabled() {
        return this.underlyingCache.isDisabled();
    }

    @Override
    public boolean isElementInMemory(Object key) {
        return this.underlyingCache.isElementInMemory(key);
    }

    @Override
    public boolean isElementInMemory(Serializable key) {
        return this.underlyingCache.isElementInMemory(key);
    }

    @Override
    public boolean isElementOnDisk(Object key) {
        return this.underlyingCache.isElementOnDisk(key);
    }

    @Override
    public boolean isElementOnDisk(Serializable key) {
        return this.underlyingCache.isElementOnDisk(key);
    }

    @Override
    public boolean isExpired(Element element) throws IllegalStateException, NullPointerException {
        return this.underlyingCache.isExpired(element);
    }

    @Override
    public boolean isKeyInCache(Object key) {
        return this.underlyingCache.isKeyInCache(key);
    }

    @Override
    @Deprecated
    public boolean isNodeCoherent() {
        return this.underlyingCache.isNodeCoherent();
    }

    @Override
    public boolean isValueInCache(Object value) {
        return this.underlyingCache.isValueInCache(value);
    }

    @Override
    public void registerCacheExtension(CacheExtension cacheExtension) {
        this.underlyingCache.registerCacheExtension(cacheExtension);
    }

    @Override
    public void registerCacheWriter(CacheWriter cacheWriter) {
        this.underlyingCache.registerCacheWriter(cacheWriter);
    }

    @Override
    public void registerDynamicAttributesExtractor(DynamicAttributesExtractor extractor) {
        this.underlyingCache.registerDynamicAttributesExtractor(extractor);
    }

    @Override
    public boolean removeQuiet(Object key) throws IllegalStateException {
        return this.underlyingCache.removeQuiet(key);
    }

    @Override
    public boolean removeQuiet(Serializable key) throws IllegalStateException {
        return this.underlyingCache.removeQuiet(key);
    }

    @Override
    public boolean removeWithWriter(Object key) throws IllegalStateException, CacheException {
        return this.underlyingCache.removeWithWriter(key);
    }

    @Override
    public void setBootstrapCacheLoader(BootstrapCacheLoader bootstrapCacheLoader) throws CacheException {
        this.underlyingCache.setBootstrapCacheLoader(bootstrapCacheLoader);
    }

    @Override
    public void setCacheExceptionHandler(CacheExceptionHandler cacheExceptionHandler) {
        this.underlyingCache.setCacheExceptionHandler(cacheExceptionHandler);
    }

    @Override
    public void setCacheManager(CacheManager cacheManager) {
        this.underlyingCache.setCacheManager(cacheManager);
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.underlyingCache.setDisabled(disabled);
    }

    @Override
    public void setName(String name) {
        this.underlyingCache.setName(name);
    }

    @Override
    @Deprecated
    public void setNodeCoherent(boolean coherent) throws UnsupportedOperationException {
        this.underlyingCache.setNodeCoherent(coherent);
    }

    @Override
    public void setTransactionManagerLookup(TransactionManagerLookup transactionManagerLookup) {
        this.underlyingCache.setTransactionManagerLookup(transactionManagerLookup);
    }

    @Override
    public void unregisterCacheExtension(CacheExtension cacheExtension) {
        this.underlyingCache.unregisterCacheExtension(cacheExtension);
    }

    @Override
    public void unregisterCacheWriter() {
        this.underlyingCache.unregisterCacheWriter();
    }

    @Override
    @Deprecated
    public void waitUntilClusterCoherent() throws UnsupportedOperationException {
        this.underlyingCache.waitUntilClusterCoherent();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public Element putIfAbsent(Element element) throws NullPointerException {
        return this.underlyingCache.putIfAbsent(element);
    }

    @Override
    public Element putIfAbsent(Element element, boolean doNotNotifyCacheReplicators) throws NullPointerException {
        return this.underlyingCache.putIfAbsent(element, doNotNotifyCacheReplicators);
    }

    @Override
    public boolean removeElement(Element element) throws NullPointerException {
        return this.underlyingCache.removeElement(element);
    }

    @Override
    public boolean replace(Element old, Element element) throws NullPointerException, IllegalArgumentException {
        return this.underlyingCache.replace(old, element);
    }

    @Override
    public Element replace(Element element) throws NullPointerException {
        return this.underlyingCache.replace(element);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.underlyingCache.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.underlyingCache.addPropertyChangeListener(listener);
    }

    @Override
    public String toString() {
        return this.underlyingCache.toString();
    }

    @Override
    public Query createQuery() {
        return this.underlyingCache.createQuery();
    }

    @Override
    public <T> Attribute<T> getSearchAttribute(String attributeName) throws CacheException {
        return this.underlyingCache.getSearchAttribute(attributeName);
    }

    @Override
    public Set<Attribute> getSearchAttributes() throws CacheException {
        return this.underlyingCache.getSearchAttributes();
    }

    @Override
    public boolean isSearchable() {
        return this.underlyingCache.isSearchable();
    }

    @Override
    public void acquireReadLockOnKey(Object key) {
        this.underlyingCache.acquireReadLockOnKey(key);
    }

    @Override
    public void acquireWriteLockOnKey(Object key) {
        this.underlyingCache.acquireWriteLockOnKey(key);
    }

    @Override
    public void releaseReadLockOnKey(Object key) {
        this.underlyingCache.releaseReadLockOnKey(key);
    }

    @Override
    public void releaseWriteLockOnKey(Object key) {
        this.underlyingCache.releaseWriteLockOnKey(key);
    }

    @Override
    public boolean tryReadLockOnKey(Object key, long timeout) throws InterruptedException {
        return this.underlyingCache.tryReadLockOnKey(key, timeout);
    }

    @Override
    public boolean tryWriteLockOnKey(Object key, long timeout) throws InterruptedException {
        return this.underlyingCache.tryWriteLockOnKey(key, timeout);
    }

    @Override
    public boolean isReadLockedByCurrentThread(Object key) {
        return this.underlyingCache.isReadLockedByCurrentThread(key);
    }

    @Override
    public boolean isWriteLockedByCurrentThread(Object key) {
        return this.underlyingCache.isWriteLockedByCurrentThread(key);
    }

    @Override
    public boolean isClusterBulkLoadEnabled() throws UnsupportedOperationException, TerracottaNotRunningException {
        return this.underlyingCache.isClusterBulkLoadEnabled();
    }

    @Override
    public boolean isNodeBulkLoadEnabled() throws UnsupportedOperationException, TerracottaNotRunningException {
        return this.underlyingCache.isNodeBulkLoadEnabled();
    }

    @Override
    public void setNodeBulkLoadEnabled(boolean enabledBulkLoad) throws UnsupportedOperationException, TerracottaNotRunningException {
        this.underlyingCache.setNodeBulkLoadEnabled(enabledBulkLoad);
    }

    @Override
    public void waitUntilClusterBulkLoadComplete() throws UnsupportedOperationException, TerracottaNotRunningException {
        this.underlyingCache.waitUntilClusterBulkLoadComplete();
    }

    private InternalEhcache asInternalEhcache() {
        return (InternalEhcache)this.underlyingCache;
    }

    @Override
    public Element removeAndReturnElement(Object key) throws IllegalStateException {
        if (this.underlyingCache instanceof InternalEhcache) {
            return this.asInternalEhcache().removeAndReturnElement(key);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public void recalculateSize(Object key) {
        if (!(this.underlyingCache instanceof InternalEhcache)) {
            throw new UnsupportedOperationException();
        }
        this.asInternalEhcache().recalculateSize(key);
    }

    @Override
    public StatisticsGateway getStatistics() throws IllegalStateException {
        return this.underlyingCache.getStatistics();
    }
}

