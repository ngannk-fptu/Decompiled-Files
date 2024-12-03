/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.store.nonstop;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.Policy;
import net.sf.ehcache.store.StoreListener;
import net.sf.ehcache.store.StoreQuery;
import net.sf.ehcache.store.TerracottaStore;
import net.sf.ehcache.terracotta.TerracottaNotRunningException;
import net.sf.ehcache.writer.CacheWriterManager;
import net.sf.ehcache.writer.writebehind.WriteBehind;
import org.terracotta.modules.ehcache.store.nonstop.ExceptionOnTimeoutStore;
import org.terracotta.modules.ehcache.store.nonstop.LocalReadsOnTimeoutStore;
import org.terracotta.modules.ehcache.store.nonstop.NoOpOnTimeoutStore;

public class LocalReadsAndExceptionOnWritesTimeoutStore
implements TerracottaStore {
    private final TerracottaStore reader;
    private final TerracottaStore writer = ExceptionOnTimeoutStore.getInstance();

    public LocalReadsAndExceptionOnWritesTimeoutStore(TerracottaStore delegate) {
        this.reader = new LocalReadsOnTimeoutStore(delegate);
    }

    public LocalReadsAndExceptionOnWritesTimeoutStore() {
        this.reader = NoOpOnTimeoutStore.getInstance();
    }

    @Override
    public int getSize() {
        return this.reader.getSize();
    }

    @Override
    public int getInMemorySize() {
        return this.reader.getInMemorySize();
    }

    @Override
    public int getOffHeapSize() {
        return this.reader.getOffHeapSize();
    }

    @Override
    public int getOnDiskSize() {
        return this.reader.getOnDiskSize();
    }

    @Override
    public int getTerracottaClusteredSize() {
        return this.reader.getTerracottaClusteredSize();
    }

    @Override
    public long getInMemorySizeInBytes() {
        return this.reader.getInMemorySizeInBytes();
    }

    @Override
    public long getOffHeapSizeInBytes() {
        return this.reader.getOffHeapSizeInBytes();
    }

    @Override
    public long getOnDiskSizeInBytes() {
        return this.reader.getOnDiskSizeInBytes();
    }

    @Override
    public boolean hasAbortedSizeOf() {
        return this.reader.hasAbortedSizeOf();
    }

    @Override
    public Status getStatus() {
        return this.reader.getStatus();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.reader.containsKey(key);
    }

    @Override
    public boolean containsKeyOnDisk(Object key) {
        return this.reader.containsKeyOnDisk(key);
    }

    @Override
    public boolean containsKeyOffHeap(Object key) {
        return this.reader.containsKeyOffHeap(key);
    }

    @Override
    public boolean containsKeyInMemory(Object key) {
        return this.reader.containsKeyInMemory(key);
    }

    @Override
    public Element get(Object key) {
        return this.reader.get(key);
    }

    @Override
    public Element getQuiet(Object key) {
        return this.reader.getQuiet(key);
    }

    @Override
    public List getKeys() {
        return this.reader.getKeys();
    }

    @Override
    public boolean bufferFull() {
        return this.reader.bufferFull();
    }

    @Override
    public Policy getInMemoryEvictionPolicy() {
        return this.reader.getInMemoryEvictionPolicy();
    }

    @Override
    public Results executeQuery(StoreQuery query) throws SearchException {
        return this.reader.executeQuery(query);
    }

    @Override
    public Set<Attribute> getSearchAttributes() {
        return this.reader.getSearchAttributes();
    }

    @Override
    public <T> Attribute<T> getSearchAttribute(String attributeName) {
        return this.reader.getSearchAttribute(attributeName);
    }

    @Override
    public Map<Object, Element> getAllQuiet(Collection<?> keys) {
        return this.reader.getAllQuiet(keys);
    }

    @Override
    public Map<Object, Element> getAll(Collection<?> keys) {
        return this.reader.getAll(keys);
    }

    @Override
    public Object getInternalContext() {
        return this.reader.getInternalContext();
    }

    @Override
    public boolean isCacheCoherent() {
        return this.reader.isCacheCoherent();
    }

    @Override
    public boolean isClusterCoherent() throws TerracottaNotRunningException {
        return this.reader.isClusterCoherent();
    }

    @Override
    public boolean isNodeCoherent() throws TerracottaNotRunningException {
        return this.reader.isNodeCoherent();
    }

    @Override
    public void waitUntilClusterCoherent() throws UnsupportedOperationException, TerracottaNotRunningException, InterruptedException {
        this.reader.waitUntilClusterCoherent();
    }

    @Override
    public Object getMBean() {
        return this.reader.getMBean();
    }

    @Override
    public Element unsafeGet(Object key) {
        return this.reader.unsafeGet(key);
    }

    @Override
    public void quickClear() {
        this.writer.quickClear();
    }

    @Override
    public int quickSize() {
        return this.reader.quickSize();
    }

    @Override
    public Set getLocalKeys() {
        return this.reader.getLocalKeys();
    }

    @Override
    public CacheConfiguration.TransactionalMode getTransactionalMode() {
        return this.reader.getTransactionalMode();
    }

    @Override
    public boolean put(Element element) throws IllegalArgumentException, IllegalStateException, CacheException {
        return this.writer.put(element);
    }

    @Override
    public void putAll(Collection<Element> elements) throws CacheException {
        this.writer.putAll(elements);
    }

    @Override
    public Element remove(Object key) throws IllegalStateException {
        return this.writer.remove(key);
    }

    @Override
    public void removeAll(Collection<?> keys) throws IllegalStateException {
        this.writer.removeAll(keys);
    }

    @Override
    public void removeAll() throws IllegalStateException, CacheException {
        this.writer.removeAll();
    }

    @Override
    public void flush() throws IllegalStateException, CacheException, IOException {
        this.writer.flush();
    }

    @Override
    public Element putIfAbsent(Element element) throws NullPointerException {
        return this.writer.putIfAbsent(element);
    }

    @Override
    public Element replace(Element element) throws NullPointerException {
        return this.writer.replace(element);
    }

    @Override
    public void addStoreListener(StoreListener listener) {
        this.writer.addStoreListener(listener);
    }

    @Override
    public void dispose() {
        this.writer.dispose();
    }

    @Override
    public void expireElements() {
        this.writer.expireElements();
    }

    @Override
    public boolean putWithWriter(Element element, CacheWriterManager writerManager) throws CacheException {
        return this.writer.putWithWriter(element, writerManager);
    }

    @Override
    public Element removeElement(Element element, ElementValueComparator comparator) throws NullPointerException {
        return this.writer.removeElement(element, comparator);
    }

    @Override
    public void removeStoreListener(StoreListener listener) {
        this.writer.removeStoreListener(listener);
    }

    @Override
    public Element removeWithWriter(Object key, CacheWriterManager writerManager) throws CacheException {
        return this.writer.removeWithWriter(key, writerManager);
    }

    @Override
    public boolean replace(Element old, Element element, ElementValueComparator comparator) throws NullPointerException, IllegalArgumentException {
        return this.writer.replace(old, element, comparator);
    }

    @Override
    public void setAttributeExtractors(Map<String, AttributeExtractor> extractors) {
        this.writer.setAttributeExtractors(extractors);
    }

    @Override
    public void setInMemoryEvictionPolicy(Policy policy) {
        this.writer.setInMemoryEvictionPolicy(policy);
    }

    @Override
    public void setNodeCoherent(boolean coherent) throws UnsupportedOperationException {
        this.writer.setNodeCoherent(coherent);
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
        this.writer.notifyCacheEventListenersChanged();
    }
}

