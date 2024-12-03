/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.store.CopyStrategyHandler;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.Policy;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.store.StoreListener;
import net.sf.ehcache.store.StoreQuery;
import net.sf.ehcache.store.compound.ReadWriteCopyStrategy;
import net.sf.ehcache.terracotta.TerracottaNotRunningException;
import net.sf.ehcache.writer.CacheWriterManager;
import org.terracotta.context.annotations.ContextChild;

abstract class AbstractCopyingCacheStore<T extends Store>
implements Store {
    @ContextChild
    private final T store;
    private final CopyStrategyHandler copyStrategyHandler;

    public AbstractCopyingCacheStore(T store, boolean copyOnRead, boolean copyOnWrite, ReadWriteCopyStrategy<Element> copyStrategyInstance, ClassLoader loader) {
        this.store = store;
        this.copyStrategyHandler = new CopyStrategyHandler(copyOnRead, copyOnWrite, copyStrategyInstance, loader);
    }

    @Override
    public void addStoreListener(StoreListener listener) {
        this.store.addStoreListener(listener);
    }

    @Override
    public void removeStoreListener(StoreListener listener) {
        this.store.removeStoreListener(listener);
    }

    @Override
    public boolean put(Element e) throws CacheException {
        return e == null || this.store.put(this.copyStrategyHandler.copyElementForWriteIfNeeded(e));
    }

    @Override
    public void putAll(Collection<Element> elements) throws CacheException {
        for (Element element : elements) {
            this.put(element);
        }
    }

    @Override
    public boolean putWithWriter(Element element, CacheWriterManager writerManager) throws CacheException {
        return this.store.putWithWriter(this.copyStrategyHandler.copyElementForWriteIfNeeded(element), writerManager);
    }

    @Override
    public Element get(Object key) {
        return this.copyStrategyHandler.copyElementForReadIfNeeded(this.store.get(key));
    }

    @Override
    public Element getQuiet(Object key) {
        return this.copyStrategyHandler.copyElementForReadIfNeeded(this.store.getQuiet(key));
    }

    @Override
    public List getKeys() {
        return this.store.getKeys();
    }

    @Override
    public Element remove(Object key) {
        return this.copyStrategyHandler.copyElementForReadIfNeeded(this.store.remove(key));
    }

    @Override
    public void removeAll(Collection<?> keys) {
        for (Object key : keys) {
            this.remove(key);
        }
    }

    @Override
    public Element removeWithWriter(Object key, CacheWriterManager writerManager) throws CacheException {
        return this.copyStrategyHandler.copyElementForReadIfNeeded(this.store.removeWithWriter(key, writerManager));
    }

    @Override
    public void removeAll() throws CacheException {
        this.store.removeAll();
    }

    @Override
    public Element putIfAbsent(Element element) throws NullPointerException {
        return this.copyStrategyHandler.copyElementForReadIfNeeded(this.store.putIfAbsent(this.copyStrategyHandler.copyElementForWriteIfNeeded(element)));
    }

    @Override
    public Element removeElement(Element element, ElementValueComparator comparator) throws NullPointerException {
        Element removed = this.store.removeElement(this.copyStrategyHandler.copyElementForRemovalIfNeeded(element), comparator);
        return this.copyStrategyHandler.copyElementForReadIfNeeded(removed);
    }

    @Override
    public boolean replace(Element old, Element element, ElementValueComparator comparator) throws NullPointerException, IllegalArgumentException {
        Element oldElement = this.copyStrategyHandler.copyElementForRemovalIfNeeded(old);
        Element newElement = this.copyStrategyHandler.copyElementForWriteIfNeeded(element);
        return this.store.replace(oldElement, newElement, comparator);
    }

    @Override
    public Element replace(Element element) throws NullPointerException {
        return this.copyStrategyHandler.copyElementForReadIfNeeded(this.store.replace(this.copyStrategyHandler.copyElementForWriteIfNeeded(element)));
    }

    @Override
    public void dispose() {
        this.store.dispose();
    }

    @Override
    public int getSize() {
        return this.store.getSize();
    }

    @Override
    public int getInMemorySize() {
        return this.store.getInMemorySize();
    }

    @Override
    public int getOffHeapSize() {
        return this.store.getOffHeapSize();
    }

    @Override
    public int getOnDiskSize() {
        return this.store.getOnDiskSize();
    }

    @Override
    public int getTerracottaClusteredSize() {
        return this.store.getTerracottaClusteredSize();
    }

    @Override
    public long getInMemorySizeInBytes() {
        return this.store.getInMemorySizeInBytes();
    }

    @Override
    public long getOffHeapSizeInBytes() {
        return this.store.getOffHeapSizeInBytes();
    }

    @Override
    public long getOnDiskSizeInBytes() {
        return this.store.getOnDiskSizeInBytes();
    }

    @Override
    public boolean hasAbortedSizeOf() {
        return this.store.hasAbortedSizeOf();
    }

    @Override
    public Status getStatus() {
        return this.store.getStatus();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.store.containsKey(key);
    }

    @Override
    public boolean containsKeyOnDisk(Object key) {
        return this.store.containsKeyOnDisk(key);
    }

    @Override
    public boolean containsKeyOffHeap(Object key) {
        return this.store.containsKeyOffHeap(key);
    }

    @Override
    public boolean containsKeyInMemory(Object key) {
        return this.store.containsKeyInMemory(key);
    }

    @Override
    public void expireElements() {
        this.store.expireElements();
    }

    @Override
    public void flush() throws IOException {
        this.store.flush();
    }

    @Override
    public boolean bufferFull() {
        return this.store.bufferFull();
    }

    @Override
    public Policy getInMemoryEvictionPolicy() {
        return this.store.getInMemoryEvictionPolicy();
    }

    @Override
    public void setInMemoryEvictionPolicy(Policy policy) {
        this.store.setInMemoryEvictionPolicy(policy);
    }

    @Override
    public Object getInternalContext() {
        return this.store.getInternalContext();
    }

    @Override
    public boolean isCacheCoherent() {
        return this.store.isCacheCoherent();
    }

    @Override
    public boolean isClusterCoherent() throws TerracottaNotRunningException {
        return this.store.isClusterCoherent();
    }

    @Override
    public boolean isNodeCoherent() throws TerracottaNotRunningException {
        return this.store.isNodeCoherent();
    }

    @Override
    public void setNodeCoherent(boolean coherent) throws UnsupportedOperationException, TerracottaNotRunningException {
        this.store.setNodeCoherent(coherent);
    }

    @Override
    public void waitUntilClusterCoherent() throws UnsupportedOperationException, TerracottaNotRunningException, InterruptedException {
        this.store.waitUntilClusterCoherent();
    }

    @Override
    public Object getMBean() {
        return this.store.getMBean();
    }

    @Override
    public void setAttributeExtractors(Map<String, AttributeExtractor> extractors) {
        this.store.setAttributeExtractors(extractors);
    }

    @Override
    public Results executeQuery(StoreQuery query) throws SearchException {
        return this.store.executeQuery(query);
    }

    public <S> Attribute<S> getSearchAttribute(String attributeName) {
        return this.store.getSearchAttribute(attributeName);
    }

    @Override
    public Set<Attribute> getSearchAttributes() {
        return this.store.getSearchAttributes();
    }

    @Override
    public Map<Object, Element> getAllQuiet(Collection<?> keys) {
        HashMap<Object, Element> elements = new HashMap<Object, Element>();
        for (Object key : keys) {
            elements.put(key, this.getQuiet(key));
        }
        return elements;
    }

    @Override
    public Map<Object, Element> getAll(Collection<?> keys) {
        HashMap<Object, Element> elements = new HashMap<Object, Element>();
        for (Object key : keys) {
            elements.put(key, this.get(key));
        }
        return elements;
    }

    @Override
    public void recalculateSize(Object key) {
        this.store.recalculateSize(key);
    }

    public T getUnderlyingStore() {
        return this.store;
    }

    protected CopyStrategyHandler getCopyStrategyHandler() {
        return this.copyStrategyHandler;
    }
}

