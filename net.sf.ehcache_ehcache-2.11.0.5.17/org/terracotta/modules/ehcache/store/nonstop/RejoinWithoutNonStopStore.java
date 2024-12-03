/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.store.nonstop;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.constructs.nonstop.RejoinCacheException;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.Policy;
import net.sf.ehcache.store.StoreListener;
import net.sf.ehcache.store.StoreQuery;
import net.sf.ehcache.store.TerracottaStore;
import net.sf.ehcache.writer.CacheWriterManager;
import net.sf.ehcache.writer.writebehind.WriteBehind;

public final class RejoinWithoutNonStopStore
implements TerracottaStore {
    private static final RejoinWithoutNonStopStore INSTANCE = new RejoinWithoutNonStopStore();

    private RejoinWithoutNonStopStore() {
    }

    public static RejoinWithoutNonStopStore getInstance() {
        return INSTANCE;
    }

    @Override
    public Element get(Object key) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during get(Object key)");
    }

    @Override
    public Element getQuiet(Object key) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during getQuiet(Object key)");
    }

    @Override
    public Map<Object, Element> getAllQuiet(Collection<?> keys) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during getAllQuiet(Collection<?> keys)");
    }

    @Override
    public Map<Object, Element> getAll(Collection<?> keys) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during getAll(Collection<?> keys)");
    }

    @Override
    public List getKeys() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during getKeys()");
    }

    @Override
    public boolean put(Element element) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during put(Element element)");
    }

    @Override
    public void putAll(Collection<Element> elements) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during putAll(Collection<Element> elements)");
    }

    @Override
    public Element remove(Object key) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during remove(Object key)");
    }

    @Override
    public void removeAll(Collection<?> keys) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during removeAll(Collection<?> keys)");
    }

    @Override
    public void removeAll() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during removeAll()");
    }

    @Override
    public void flush() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during flush()");
    }

    @Override
    public Object getInternalContext() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during getInternalContext()");
    }

    @Override
    public int getSize() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during getSize()");
    }

    @Override
    public Element putIfAbsent(Element element) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during putIfAbsent(Element element)");
    }

    @Override
    public Element replace(Element element) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during replace(Element element)");
    }

    @Override
    public void addStoreListener(StoreListener listener) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during addStoreListener(StoreListener listener)");
    }

    @Override
    public boolean bufferFull() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during bufferFull()");
    }

    @Override
    public boolean containsKey(Object key) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during containsKey(Object key)");
    }

    @Override
    public boolean containsKeyInMemory(Object key) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during containsKeyInMemory(Object key)");
    }

    @Override
    public boolean containsKeyOffHeap(Object key) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during containsKeyOffHeap(Object key)");
    }

    @Override
    public boolean containsKeyOnDisk(Object key) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during containsKeyOnDisk(Object key)");
    }

    @Override
    public void dispose() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during dispose()");
    }

    @Override
    public Results executeQuery(StoreQuery query) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during executeQuery(StoreQuery query)");
    }

    @Override
    public void expireElements() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during expireElements()");
    }

    @Override
    public Policy getInMemoryEvictionPolicy() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during getInMemoryEvictionPolicy()");
    }

    @Override
    public int getInMemorySize() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during getInMemorySize()");
    }

    @Override
    public long getInMemorySizeInBytes() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during getInMemorySizeInBytes()");
    }

    @Override
    public Object getMBean() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during getMBean()");
    }

    @Override
    public int getOffHeapSize() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during getOffHeapSize()");
    }

    @Override
    public long getOffHeapSizeInBytes() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during getOffHeapSizeInBytes()");
    }

    @Override
    public int getOnDiskSize() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during getOnDiskSize()");
    }

    @Override
    public long getOnDiskSizeInBytes() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during getOnDiskSizeInBytes()");
    }

    @Override
    public boolean hasAbortedSizeOf() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during hasAbortedSizeOf()");
    }

    @Override
    public Status getStatus() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during getStatus()");
    }

    @Override
    public int getTerracottaClusteredSize() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during getTerracottaClusteredSize()");
    }

    @Override
    public boolean isCacheCoherent() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during isCacheCoherent()");
    }

    @Override
    public boolean isClusterCoherent() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during isClusterCoherent()");
    }

    @Override
    public boolean isNodeCoherent() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during isNodeCoherent()");
    }

    @Override
    public boolean putWithWriter(Element element, CacheWriterManager writerManager) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during putWithWriter(Element element, CacheWriterManager writerManager)");
    }

    @Override
    public Element removeElement(Element element, ElementValueComparator comparator) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during removeElement(Element element, ElementValueComparator comparator)");
    }

    @Override
    public void removeStoreListener(StoreListener listener) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during removeStoreListener(StoreListener listener)");
    }

    @Override
    public Element removeWithWriter(Object key, CacheWriterManager writerManager) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during removeWithWriter(Object key, CacheWriterManager writerManager)");
    }

    @Override
    public boolean replace(Element old, Element element, ElementValueComparator comparator) throws NullPointerException, IllegalArgumentException {
        throw new RejoinCacheException("Client started rejoin during replace(Element old, Element element, ElementValueComparator comparator)");
    }

    @Override
    public void setAttributeExtractors(Map<String, AttributeExtractor> extractors) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during setAttributeExtractors(Map<String, AttributeExtractor> extractors)");
    }

    @Override
    public void setInMemoryEvictionPolicy(Policy policy) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during setInMemoryEvictionPolicy(Policy policy)");
    }

    @Override
    public void setNodeCoherent(boolean coherent) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during setNodeCoherent(boolean coherent)");
    }

    @Override
    public void waitUntilClusterCoherent() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during waitUntilClusterCoherent()");
    }

    @Override
    public Set<Attribute> getSearchAttributes() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during getSearchAttributes()");
    }

    @Override
    public <T> Attribute<T> getSearchAttribute(String attributeName) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during getSearchAttribute(String attributeName)");
    }

    @Override
    public Set getLocalKeys() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during getLocalKeys()");
    }

    @Override
    public CacheConfiguration.TransactionalMode getTransactionalMode() throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during getTransactionalMode()");
    }

    public Element unlockedGet(Object key) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during unlockedGet(Object key)");
    }

    public Element unlockedGetQuiet(Object key) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during unlockedGetQuiet(Object key)");
    }

    @Override
    public Element unsafeGet(Object key) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during unsafeGet(Object key)");
    }

    @Override
    public void quickClear() {
        throw new RejoinCacheException("Client started rejoin during quickClear()");
    }

    @Override
    public int quickSize() {
        throw new RejoinCacheException("Client started rejoin during quickSize()");
    }

    public Element unsafeGetQuiet(Object key) throws RejoinCacheException {
        throw new RejoinCacheException("Client started rejoin during unsafeGetQuiet(Object key)");
    }

    @Override
    public void recalculateSize(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public WriteBehind createWriteBehind() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void notifyCacheEventListenersChanged() {
        throw new RejoinCacheException("Client started rejoin during notifyCacheEventListenersChanged()");
    }
}

