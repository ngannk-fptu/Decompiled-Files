/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.store.nonstop;

import java.util.ArrayList;
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

public class LocalReadsOnTimeoutStore
implements TerracottaStore {
    private final TerracottaStore delegate;

    public LocalReadsOnTimeoutStore(TerracottaStore delegate) {
        this.delegate = delegate;
    }

    @Override
    public Element get(Object key) throws IllegalStateException, CacheException {
        return this.getQuiet(key);
    }

    @Override
    public List getKeys() throws IllegalStateException, CacheException {
        return Collections.unmodifiableList(new ArrayList(this.getUnderlyingLocalKeys()));
    }

    private Set getUnderlyingLocalKeys() {
        return this.delegate.getLocalKeys();
    }

    @Override
    public Element getQuiet(Object key) throws IllegalStateException, CacheException {
        return this.delegate.unsafeGet(key);
    }

    @Override
    public Map<Object, Element> getAllQuiet(Collection<?> keys) {
        HashMap<Object, Element> rv = new HashMap<Object, Element>();
        for (Object key : keys) {
            rv.put(key, this.delegate.unsafeGet(key));
        }
        return rv;
    }

    @Override
    public Map<Object, Element> getAll(Collection<?> keys) {
        return this.getAllQuiet(keys);
    }

    @Override
    public boolean put(Element element) throws IllegalArgumentException, IllegalStateException, CacheException {
        return false;
    }

    @Override
    public void putAll(Collection<Element> elements) throws CacheException {
    }

    @Override
    public Element remove(Object key) throws IllegalStateException {
        return null;
    }

    @Override
    public void removeAll(Collection<?> keys) throws IllegalStateException {
    }

    @Override
    public void removeAll() throws IllegalStateException, CacheException {
    }

    @Override
    public void flush() throws IllegalStateException, CacheException {
    }

    @Override
    public Object getInternalContext() {
        return null;
    }

    @Override
    public int getSize() throws IllegalStateException, CacheException {
        return this.getUnderlyingLocalKeys().size();
    }

    @Override
    public Element putIfAbsent(Element element) throws NullPointerException {
        return null;
    }

    @Override
    public Element replace(Element element) throws NullPointerException {
        return null;
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
        return this.containsKeyInMemory(key);
    }

    @Override
    public boolean containsKeyInMemory(Object key) {
        return this.getUnderlyingLocalKeys().contains(key);
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
    public Policy getInMemoryEvictionPolicy() {
        return null;
    }

    @Override
    public int getInMemorySize() {
        return this.getUnderlyingLocalKeys().size();
    }

    @Override
    public long getInMemorySizeInBytes() {
        return this.delegate.getInMemorySizeInBytes();
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
    public Status getStatus() {
        return null;
    }

    @Override
    public int getTerracottaClusteredSize() {
        return this.getUnderlyingLocalKeys().size();
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
    public boolean putWithWriter(Element element, CacheWriterManager writerManager) throws CacheException {
        return false;
    }

    @Override
    public Element removeElement(Element element, ElementValueComparator comparator) throws NullPointerException {
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
    public boolean replace(Element old, Element element, ElementValueComparator comparator) throws NullPointerException, IllegalArgumentException {
        return false;
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
        return this.delegate.getSearchAttributes();
    }

    @Override
    public <T> Attribute<T> getSearchAttribute(String attributeName) {
        return this.delegate.getSearchAttribute(attributeName);
    }

    @Override
    public Set getLocalKeys() {
        return this.getUnderlyingLocalKeys();
    }

    @Override
    public CacheConfiguration.TransactionalMode getTransactionalMode() {
        return null;
    }

    public Element unlockedGet(Object key) {
        return this.unlockedGetQuiet(key);
    }

    public Element unlockedGetQuiet(Object key) {
        return this.delegate.unsafeGet(key);
    }

    @Override
    public Element unsafeGet(Object key) {
        return this.unsafeGetQuiet(key);
    }

    @Override
    public void quickClear() {
    }

    @Override
    public int quickSize() {
        return this.getUnderlyingLocalKeys().size();
    }

    public Element unsafeGetQuiet(Object key) {
        return this.delegate.unsafeGet(key);
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
        this.delegate.notifyCacheEventListenersChanged();
    }
}

