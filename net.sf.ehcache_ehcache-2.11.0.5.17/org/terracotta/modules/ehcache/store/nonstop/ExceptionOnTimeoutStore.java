/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.store.nonstop;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.constructs.nonstop.NonStopCacheException;
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

public final class ExceptionOnTimeoutStore
implements TerracottaStore {
    private static final ExceptionOnTimeoutStore INSTANCE = new ExceptionOnTimeoutStore();

    private ExceptionOnTimeoutStore() {
    }

    public static ExceptionOnTimeoutStore getInstance() {
        return INSTANCE;
    }

    @Override
    public Element get(Object key) throws IllegalStateException, CacheException {
        throw new NonStopCacheException("get timed out");
    }

    @Override
    public Element getQuiet(Object key) throws IllegalStateException, CacheException {
        throw new NonStopCacheException("getQuiet timed out");
    }

    @Override
    public Map<Object, Element> getAllQuiet(Collection<?> keys) {
        throw new NonStopCacheException("getAllQuiet for '" + keys.size() + "' keys timed out");
    }

    @Override
    public Map<Object, Element> getAll(Collection<?> keys) {
        throw new NonStopCacheException("getAll for '" + keys.size() + "' keys timed out");
    }

    @Override
    public List getKeys() throws IllegalStateException, CacheException {
        throw new NonStopCacheException("getKeys timed out");
    }

    @Override
    public boolean put(Element element) throws IllegalArgumentException, IllegalStateException, CacheException {
        throw new NonStopCacheException("put timed out");
    }

    @Override
    public void putAll(Collection<Element> elements) throws CacheException {
        throw new NonStopCacheException("putAll for " + elements.size() + " elements timed out");
    }

    @Override
    public Element remove(Object key) throws IllegalStateException {
        throw new NonStopCacheException("remove timed out");
    }

    @Override
    public void removeAll(Collection<?> keys) throws IllegalStateException {
        throw new NonStopCacheException("removeAll for " + keys.size() + "  keys timed out");
    }

    @Override
    public void removeAll() throws IllegalStateException, CacheException {
        throw new NonStopCacheException("removeAll timed out");
    }

    @Override
    public void flush() throws IllegalStateException, CacheException {
        throw new NonStopCacheException("flush timed out");
    }

    @Override
    public Object getInternalContext() {
        throw new NonStopCacheException("getInternalContext timed out");
    }

    @Override
    public int getSize() throws IllegalStateException, CacheException {
        throw new NonStopCacheException("getSize timed out");
    }

    @Override
    public Element putIfAbsent(Element element) throws NullPointerException {
        throw new NonStopCacheException("putIfAbsent timed out");
    }

    @Override
    public Element replace(Element element) throws NullPointerException {
        throw new NonStopCacheException("replace timed out");
    }

    @Override
    public void addStoreListener(StoreListener listener) {
        throw new NonStopCacheException("addStoreListener timed out");
    }

    @Override
    public boolean bufferFull() {
        throw new NonStopCacheException("bufferFull timed out");
    }

    @Override
    public boolean containsKey(Object key) {
        throw new NonStopCacheException("containsKey timed out");
    }

    @Override
    public boolean containsKeyInMemory(Object key) {
        throw new NonStopCacheException("containsKeyInMemory timed out");
    }

    @Override
    public boolean containsKeyOffHeap(Object key) {
        throw new NonStopCacheException("containsKeyOffHeap timed out");
    }

    @Override
    public boolean containsKeyOnDisk(Object key) {
        throw new NonStopCacheException("containsKeyOnDisk timed out");
    }

    @Override
    public void dispose() {
        throw new NonStopCacheException("dispose timed out");
    }

    @Override
    public Results executeQuery(StoreQuery query) {
        throw new NonStopCacheException("executeQuery timed out");
    }

    @Override
    public void expireElements() {
        throw new NonStopCacheException("expireElements timed out");
    }

    @Override
    public Policy getInMemoryEvictionPolicy() {
        throw new NonStopCacheException("getInMemoryEvictionPolicy timed out");
    }

    @Override
    public int getInMemorySize() {
        throw new NonStopCacheException("getInMemorySize timed out");
    }

    @Override
    public long getInMemorySizeInBytes() {
        throw new NonStopCacheException("getInMemorySizeInBytes timed out");
    }

    @Override
    public Object getMBean() {
        throw new NonStopCacheException("getMBean timed out");
    }

    @Override
    public int getOffHeapSize() {
        throw new NonStopCacheException("getOffHeapSize timed out");
    }

    @Override
    public long getOffHeapSizeInBytes() {
        throw new NonStopCacheException("getOffHeapSizeInBytes timed out");
    }

    @Override
    public int getOnDiskSize() {
        throw new NonStopCacheException("getOnDiskSize timed out");
    }

    @Override
    public long getOnDiskSizeInBytes() {
        throw new NonStopCacheException("getOnDiskSizeInBytes timed out");
    }

    @Override
    public boolean hasAbortedSizeOf() {
        throw new NonStopCacheException("hasAbortedSizeOf timed out");
    }

    @Override
    public Status getStatus() {
        throw new NonStopCacheException("getStatus timed out");
    }

    @Override
    public int getTerracottaClusteredSize() {
        throw new NonStopCacheException("getTerracottaClusteredSize timed out");
    }

    @Override
    public boolean isCacheCoherent() {
        throw new NonStopCacheException("isCacheCoherent timed out");
    }

    @Override
    public boolean isClusterCoherent() {
        throw new NonStopCacheException("isClusterCoherent timed out");
    }

    @Override
    public boolean isNodeCoherent() {
        throw new NonStopCacheException("isNodeCoherent timed out");
    }

    @Override
    public boolean putWithWriter(Element element, CacheWriterManager writerManager) throws CacheException {
        throw new NonStopCacheException("putWithWriter timed out");
    }

    @Override
    public Element removeElement(Element element, ElementValueComparator comparator) throws NullPointerException {
        throw new NonStopCacheException("removeElement timed out");
    }

    @Override
    public void removeStoreListener(StoreListener listener) {
        throw new NonStopCacheException("removeStoreListener timed out");
    }

    @Override
    public Element removeWithWriter(Object key, CacheWriterManager writerManager) throws CacheException {
        throw new NonStopCacheException("removeWithWriter timed out");
    }

    @Override
    public boolean replace(Element old, Element element, ElementValueComparator comparator) throws NullPointerException, IllegalArgumentException {
        throw new NonStopCacheException("replace timed out");
    }

    @Override
    public void setAttributeExtractors(Map<String, AttributeExtractor> extractors) {
        throw new NonStopCacheException("setAttributeExtractors timed out");
    }

    @Override
    public void setInMemoryEvictionPolicy(Policy policy) {
        throw new NonStopCacheException("setInMemoryEvictionPolicy timed out");
    }

    @Override
    public void setNodeCoherent(boolean coherent) throws UnsupportedOperationException {
        throw new NonStopCacheException("setNodeCoherent timed out");
    }

    @Override
    public void waitUntilClusterCoherent() throws UnsupportedOperationException {
        throw new NonStopCacheException("waitUntilClusterCoherent timed out");
    }

    @Override
    public Set<Attribute> getSearchAttributes() {
        throw new NonStopCacheException("getSearchAttributes timed out");
    }

    @Override
    public <T> Attribute<T> getSearchAttribute(String attributeName) {
        throw new NonStopCacheException("getSearchAttribute timed out");
    }

    @Override
    public Set getLocalKeys() {
        throw new NonStopCacheException("getLocalKeys() timed out");
    }

    @Override
    public CacheConfiguration.TransactionalMode getTransactionalMode() {
        throw new NonStopCacheException("getTransactionalMode() timed out");
    }

    public Element unlockedGet(Object key) {
        throw new NonStopCacheException("unlockedGet() timed out");
    }

    public Element unlockedGetQuiet(Object key) {
        throw new NonStopCacheException("unlockedGetQuiet() timed out");
    }

    @Override
    public Element unsafeGet(Object key) {
        throw new NonStopCacheException("unsafeGet() timed out");
    }

    @Override
    public void quickClear() {
        throw new NonStopCacheException("quickClear() timed out");
    }

    @Override
    public int quickSize() {
        throw new NonStopCacheException("quickSize() timed out");
    }

    public Element unsafeGetQuiet(Object key) {
        throw new NonStopCacheException("unsafeGetQuiet() timed out");
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
        throw new NonStopCacheException("notifyCacheEventListenersChanged() timed out");
    }
}

