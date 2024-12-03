/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.ElementIdHelper;
import net.sf.ehcache.Status;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.Policy;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.store.StoreListener;
import net.sf.ehcache.store.StoreQuery;
import net.sf.ehcache.terracotta.TerracottaNotRunningException;
import net.sf.ehcache.util.LongSequence;
import net.sf.ehcache.writer.CacheWriterManager;
import org.terracotta.context.annotations.ContextChild;

public class ElementIdAssigningStore
implements Store {
    @ContextChild
    private final Store delegate;
    private final LongSequence elementIdSequence;

    public ElementIdAssigningStore(Store delegate, LongSequence sequence) {
        this.delegate = delegate;
        this.elementIdSequence = sequence;
    }

    private void setId(Element element) {
        long id = this.elementIdSequence.next();
        if (id <= 0L) {
            throw new CacheException("Element ID must be > 0");
        }
        ElementIdHelper.setId(element, id);
    }

    @Override
    public void addStoreListener(StoreListener listener) {
        this.delegate.addStoreListener(listener);
    }

    @Override
    public void removeStoreListener(StoreListener listener) {
        this.delegate.removeStoreListener(listener);
    }

    @Override
    public boolean put(Element element) throws CacheException {
        this.setId(element);
        return this.delegate.put(element);
    }

    @Override
    public void putAll(Collection<Element> elements) throws CacheException {
        for (Element e : elements) {
            this.setId(e);
        }
        this.delegate.putAll(elements);
    }

    @Override
    public boolean putWithWriter(Element element, CacheWriterManager writerManager) throws CacheException {
        this.setId(element);
        return this.delegate.putWithWriter(element, writerManager);
    }

    @Override
    public Element get(Object key) {
        return this.delegate.get(key);
    }

    @Override
    public Element getQuiet(Object key) {
        return this.delegate.getQuiet(key);
    }

    @Override
    public List getKeys() {
        return this.delegate.getKeys();
    }

    @Override
    public Element remove(Object key) {
        return this.delegate.remove(key);
    }

    @Override
    public void removeAll(Collection<?> keys) {
        this.delegate.removeAll(keys);
    }

    @Override
    public Element removeWithWriter(Object key, CacheWriterManager writerManager) throws CacheException {
        return this.delegate.removeWithWriter(key, writerManager);
    }

    @Override
    public void removeAll() throws CacheException {
        this.delegate.removeAll();
    }

    @Override
    public Element putIfAbsent(Element element) throws NullPointerException {
        this.setId(element);
        return this.delegate.putIfAbsent(element);
    }

    @Override
    public Element removeElement(Element element, ElementValueComparator comparator) throws NullPointerException {
        return this.delegate.removeElement(element, comparator);
    }

    @Override
    public boolean replace(Element old, Element element, ElementValueComparator comparator) throws NullPointerException, IllegalArgumentException {
        this.setId(element);
        return this.delegate.replace(old, element, comparator);
    }

    @Override
    public Element replace(Element element) throws NullPointerException {
        this.setId(element);
        return this.delegate.replace(element);
    }

    @Override
    public void dispose() {
        this.delegate.dispose();
    }

    @Override
    public int getSize() {
        return this.delegate.getSize();
    }

    @Override
    public int getInMemorySize() {
        return this.delegate.getInMemorySize();
    }

    @Override
    public int getOffHeapSize() {
        return this.delegate.getOffHeapSize();
    }

    @Override
    public int getOnDiskSize() {
        return this.delegate.getOnDiskSize();
    }

    @Override
    public int getTerracottaClusteredSize() {
        return this.delegate.getTerracottaClusteredSize();
    }

    @Override
    public long getInMemorySizeInBytes() {
        return this.delegate.getInMemorySizeInBytes();
    }

    @Override
    public long getOffHeapSizeInBytes() {
        return this.delegate.getOffHeapSizeInBytes();
    }

    @Override
    public long getOnDiskSizeInBytes() {
        return this.delegate.getOnDiskSizeInBytes();
    }

    @Override
    public boolean hasAbortedSizeOf() {
        return this.delegate.hasAbortedSizeOf();
    }

    @Override
    public Status getStatus() {
        return this.delegate.getStatus();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsKeyOnDisk(Object key) {
        return this.delegate.containsKeyOnDisk(key);
    }

    @Override
    public boolean containsKeyOffHeap(Object key) {
        return this.delegate.containsKeyOffHeap(key);
    }

    @Override
    public boolean containsKeyInMemory(Object key) {
        return this.delegate.containsKeyInMemory(key);
    }

    @Override
    public void expireElements() {
        this.delegate.expireElements();
    }

    @Override
    public void flush() throws IOException {
        this.delegate.flush();
    }

    @Override
    public boolean bufferFull() {
        return this.delegate.bufferFull();
    }

    @Override
    public Policy getInMemoryEvictionPolicy() {
        return this.delegate.getInMemoryEvictionPolicy();
    }

    @Override
    public void setInMemoryEvictionPolicy(Policy policy) {
        this.delegate.setInMemoryEvictionPolicy(policy);
    }

    @Override
    public Object getInternalContext() {
        return this.delegate.getInternalContext();
    }

    @Override
    public boolean isCacheCoherent() {
        return this.delegate.isCacheCoherent();
    }

    @Override
    public boolean isClusterCoherent() throws TerracottaNotRunningException {
        return this.delegate.isClusterCoherent();
    }

    @Override
    public boolean isNodeCoherent() throws TerracottaNotRunningException {
        return this.delegate.isNodeCoherent();
    }

    @Override
    public void setNodeCoherent(boolean coherent) throws UnsupportedOperationException, TerracottaNotRunningException {
        this.delegate.setNodeCoherent(coherent);
    }

    @Override
    public void waitUntilClusterCoherent() throws UnsupportedOperationException, TerracottaNotRunningException, InterruptedException {
        this.delegate.waitUntilClusterCoherent();
    }

    @Override
    public Object getMBean() {
        return this.delegate.getMBean();
    }

    @Override
    public void setAttributeExtractors(Map<String, AttributeExtractor> extractors) {
        this.delegate.setAttributeExtractors(extractors);
    }

    @Override
    public Results executeQuery(StoreQuery query) throws SearchException {
        return this.delegate.executeQuery(query);
    }

    @Override
    public <T> Attribute<T> getSearchAttribute(String attributeName) {
        return this.delegate.getSearchAttribute(attributeName);
    }

    @Override
    public Set<Attribute> getSearchAttributes() {
        return this.delegate.getSearchAttributes();
    }

    @Override
    public Map<Object, Element> getAllQuiet(Collection<?> keys) {
        return this.delegate.getAllQuiet(keys);
    }

    @Override
    public Map<Object, Element> getAll(Collection<?> keys) {
        return this.delegate.getAll(keys);
    }

    @Override
    public void recalculateSize(Object key) {
        this.delegate.recalculateSize(key);
    }
}

