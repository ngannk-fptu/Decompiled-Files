/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.store.nonstop;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.NullResults;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.Policy;
import net.sf.ehcache.store.StoreListener;
import net.sf.ehcache.store.StoreQuery;
import net.sf.ehcache.store.TerracottaStore;
import net.sf.ehcache.writer.CacheWriterManager;
import net.sf.ehcache.writer.writebehind.WriteBehind;

public final class NoOpOnTimeoutStore
implements TerracottaStore {
    private static final NoOpOnTimeoutStore INSTANCE = new NoOpOnTimeoutStore();

    private NoOpOnTimeoutStore() {
    }

    public static NoOpOnTimeoutStore getInstance() {
        return INSTANCE;
    }

    @Override
    public void addStoreListener(StoreListener listener) {
    }

    @Override
    public boolean bufferFull() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsKeyInMemory(Object key) {
        return false;
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
    public void dispose() {
    }

    @Override
    public Results executeQuery(StoreQuery query) {
        return NullResults.INSTANCE;
    }

    @Override
    public void expireElements() {
    }

    @Override
    public void flush() {
    }

    @Override
    public Element get(Object key) {
        return null;
    }

    @Override
    public Policy getInMemoryEvictionPolicy() {
        return null;
    }

    @Override
    public int getInMemorySize() {
        return 0;
    }

    @Override
    public long getInMemorySizeInBytes() {
        return 0L;
    }

    @Override
    public Object getInternalContext() {
        return null;
    }

    @Override
    public List getKeys() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Object getMBean() {
        return null;
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
        return false;
    }

    @Override
    public Element getQuiet(Object key) {
        return null;
    }

    @Override
    public Map<Object, Element> getAllQuiet(Collection<?> keys) {
        HashMap<Object, Element> rv = new HashMap<Object, Element>();
        for (Object key : keys) {
            rv.put(key, null);
        }
        return rv;
    }

    @Override
    public Map<Object, Element> getAll(Collection<?> keys) {
        return this.getAllQuiet(keys);
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public Status getStatus() {
        return null;
    }

    @Override
    public int getTerracottaClusteredSize() {
        return 0;
    }

    @Override
    public boolean isCacheCoherent() {
        return false;
    }

    @Override
    public boolean isClusterCoherent() {
        return false;
    }

    @Override
    public boolean isNodeCoherent() {
        return false;
    }

    @Override
    public boolean put(Element element) throws CacheException {
        return false;
    }

    @Override
    public void putAll(Collection<Element> elements) throws CacheException {
    }

    @Override
    public Element putIfAbsent(Element element) {
        return null;
    }

    @Override
    public boolean putWithWriter(Element element, CacheWriterManager writerManager) throws CacheException {
        return false;
    }

    @Override
    public Element remove(Object key) {
        return null;
    }

    @Override
    public void removeAll(Collection<?> keys) {
    }

    @Override
    public void removeAll() throws CacheException {
    }

    @Override
    public Element removeElement(Element element, ElementValueComparator comparator) {
        return null;
    }

    @Override
    public void removeStoreListener(StoreListener listener) {
    }

    @Override
    public Element removeWithWriter(Object key, CacheWriterManager writerManager) throws CacheException {
        return null;
    }

    @Override
    public boolean replace(Element old, Element element, ElementValueComparator comparator) {
        return false;
    }

    @Override
    public Element replace(Element element) {
        return null;
    }

    @Override
    public void setAttributeExtractors(Map<String, AttributeExtractor> extractors) {
    }

    @Override
    public void setInMemoryEvictionPolicy(Policy policy) {
    }

    @Override
    public void setNodeCoherent(boolean coherent) throws UnsupportedOperationException {
    }

    @Override
    public void waitUntilClusterCoherent() throws UnsupportedOperationException {
    }

    @Override
    public Set<Attribute> getSearchAttributes() {
        return Collections.emptySet();
    }

    @Override
    public <T> Attribute<T> getSearchAttribute(String attributeName) {
        return new Attribute(attributeName);
    }

    @Override
    public Set getLocalKeys() {
        return Collections.EMPTY_SET;
    }

    @Override
    public CacheConfiguration.TransactionalMode getTransactionalMode() {
        return null;
    }

    public Element unlockedGet(Object key) {
        return null;
    }

    public Element unlockedGetQuiet(Object key) {
        return null;
    }

    @Override
    public Element unsafeGet(Object key) {
        return null;
    }

    @Override
    public void quickClear() {
    }

    @Override
    public int quickSize() {
        return 0;
    }

    public Element unsafeGetQuiet(Object key) {
        return null;
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
    }
}

