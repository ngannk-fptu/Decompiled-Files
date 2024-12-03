/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache;

import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
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
import net.sf.ehcache.terracotta.TerracottaNotRunningException;
import net.sf.ehcache.transaction.manager.TransactionManagerLookup;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.CacheWriterManager;

public interface Ehcache
extends Cloneable {
    public void put(Element var1) throws IllegalArgumentException, IllegalStateException, CacheException;

    public void putAll(Collection<Element> var1) throws IllegalArgumentException, IllegalStateException, CacheException;

    public void put(Element var1, boolean var2) throws IllegalArgumentException, IllegalStateException, CacheException;

    public void putQuiet(Element var1) throws IllegalArgumentException, IllegalStateException, CacheException;

    public void putWithWriter(Element var1) throws IllegalArgumentException, IllegalStateException, CacheException;

    public Element putIfAbsent(Element var1) throws NullPointerException;

    public Element putIfAbsent(Element var1, boolean var2) throws NullPointerException;

    public boolean removeElement(Element var1) throws NullPointerException;

    public boolean replace(Element var1, Element var2) throws NullPointerException, IllegalArgumentException;

    public Element replace(Element var1) throws NullPointerException;

    public Element get(Serializable var1) throws IllegalStateException, CacheException;

    public Element get(Object var1) throws IllegalStateException, CacheException;

    public Map<Object, Element> getAll(Collection<?> var1) throws IllegalStateException, CacheException, NullPointerException;

    public Element getQuiet(Serializable var1) throws IllegalStateException, CacheException;

    public Element getQuiet(Object var1) throws IllegalStateException, CacheException;

    public List getKeys() throws IllegalStateException, CacheException;

    public List getKeysWithExpiryCheck() throws IllegalStateException, CacheException;

    @Deprecated
    public List getKeysNoDuplicateCheck() throws IllegalStateException;

    public boolean remove(Serializable var1) throws IllegalStateException;

    public boolean remove(Object var1) throws IllegalStateException;

    public void removeAll(Collection<?> var1) throws IllegalStateException, NullPointerException;

    public void removeAll(Collection<?> var1, boolean var2) throws IllegalStateException, NullPointerException;

    public boolean remove(Serializable var1, boolean var2) throws IllegalStateException;

    public boolean remove(Object var1, boolean var2) throws IllegalStateException;

    public boolean removeQuiet(Serializable var1) throws IllegalStateException;

    public boolean removeQuiet(Object var1) throws IllegalStateException;

    public boolean removeWithWriter(Object var1) throws IllegalStateException, CacheException;

    public void removeAll() throws IllegalStateException, CacheException;

    public void removeAll(boolean var1) throws IllegalStateException, CacheException;

    public void flush() throws IllegalStateException, CacheException;

    public int getSize() throws IllegalStateException, CacheException;

    @Deprecated
    public long calculateInMemorySize() throws IllegalStateException, CacheException;

    @Deprecated
    public long calculateOffHeapSize() throws IllegalStateException, CacheException;

    @Deprecated
    public long calculateOnDiskSize() throws IllegalStateException, CacheException;

    public boolean hasAbortedSizeOf();

    @Deprecated
    public long getMemoryStoreSize() throws IllegalStateException;

    @Deprecated
    public long getOffHeapStoreSize() throws IllegalStateException;

    @Deprecated
    public int getDiskStoreSize() throws IllegalStateException;

    public Status getStatus();

    public String getName();

    public void setName(String var1);

    public String toString();

    public boolean isExpired(Element var1) throws IllegalStateException, NullPointerException;

    public Object clone() throws CloneNotSupportedException;

    public RegisteredEventListeners getCacheEventNotificationService();

    public boolean isElementInMemory(Serializable var1);

    public boolean isElementInMemory(Object var1);

    public boolean isElementOnDisk(Serializable var1);

    public boolean isElementOnDisk(Object var1);

    public String getGuid();

    public CacheManager getCacheManager();

    public void evictExpiredElements();

    public boolean isKeyInCache(Object var1);

    public boolean isValueInCache(Object var1);

    public StatisticsGateway getStatistics() throws IllegalStateException;

    public void setCacheManager(CacheManager var1);

    public BootstrapCacheLoader getBootstrapCacheLoader();

    public void setBootstrapCacheLoader(BootstrapCacheLoader var1) throws CacheException;

    public void initialise();

    public void bootstrap();

    public void dispose() throws IllegalStateException;

    public CacheConfiguration getCacheConfiguration();

    public void registerCacheExtension(CacheExtension var1);

    public void unregisterCacheExtension(CacheExtension var1);

    public List<CacheExtension> getRegisteredCacheExtensions();

    public void setCacheExceptionHandler(CacheExceptionHandler var1);

    public CacheExceptionHandler getCacheExceptionHandler();

    public void registerCacheLoader(CacheLoader var1);

    public void unregisterCacheLoader(CacheLoader var1);

    public List<CacheLoader> getRegisteredCacheLoaders();

    public void registerDynamicAttributesExtractor(DynamicAttributesExtractor var1);

    public void registerCacheWriter(CacheWriter var1);

    public void unregisterCacheWriter();

    public CacheWriter getRegisteredCacheWriter();

    public Element getWithLoader(Object var1, CacheLoader var2, Object var3) throws CacheException;

    public Map getAllWithLoader(Collection var1, Object var2) throws CacheException;

    public void load(Object var1) throws CacheException;

    public void loadAll(Collection var1, Object var2) throws CacheException;

    public boolean isDisabled();

    public void setDisabled(boolean var1);

    public Object getInternalContext();

    public void disableDynamicFeatures();

    public CacheWriterManager getWriterManager();

    @Deprecated
    public boolean isClusterCoherent() throws TerracottaNotRunningException;

    @Deprecated
    public boolean isNodeCoherent() throws TerracottaNotRunningException;

    @Deprecated
    public void setNodeCoherent(boolean var1) throws UnsupportedOperationException, TerracottaNotRunningException;

    @Deprecated
    public void waitUntilClusterCoherent() throws UnsupportedOperationException, TerracottaNotRunningException;

    public void setTransactionManagerLookup(TransactionManagerLookup var1);

    public void addPropertyChangeListener(PropertyChangeListener var1);

    public void removePropertyChangeListener(PropertyChangeListener var1);

    public <T> Attribute<T> getSearchAttribute(String var1) throws CacheException;

    public Set<Attribute> getSearchAttributes() throws CacheException;

    public Query createQuery();

    public boolean isSearchable();

    public void acquireReadLockOnKey(Object var1);

    public void acquireWriteLockOnKey(Object var1);

    public boolean tryReadLockOnKey(Object var1, long var2) throws InterruptedException;

    public boolean tryWriteLockOnKey(Object var1, long var2) throws InterruptedException;

    public void releaseReadLockOnKey(Object var1);

    public void releaseWriteLockOnKey(Object var1);

    public boolean isReadLockedByCurrentThread(Object var1) throws UnsupportedOperationException;

    public boolean isWriteLockedByCurrentThread(Object var1) throws UnsupportedOperationException;

    public boolean isClusterBulkLoadEnabled() throws UnsupportedOperationException, TerracottaNotRunningException;

    public boolean isNodeBulkLoadEnabled() throws UnsupportedOperationException, TerracottaNotRunningException;

    public void setNodeBulkLoadEnabled(boolean var1) throws UnsupportedOperationException, TerracottaNotRunningException;

    public void waitUntilClusterBulkLoadComplete() throws UnsupportedOperationException, TerracottaNotRunningException;
}

