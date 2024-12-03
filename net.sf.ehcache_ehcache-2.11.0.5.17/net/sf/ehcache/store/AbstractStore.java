/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.InvalidConfigurationException;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.search.attribute.DynamicAttributesExtractor;
import net.sf.ehcache.search.impl.SearchManager;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.store.StoreListener;
import net.sf.ehcache.store.StoreQuery;
import net.sf.ehcache.terracotta.TerracottaNotRunningException;

public abstract class AbstractStore
implements Store {
    protected final Map<String, AttributeExtractor> attributeExtractors = new ConcurrentHashMap<String, AttributeExtractor>();
    protected final SearchManager searchManager;
    private transient List<StoreListener> listenerList;
    private final String cacheName;

    protected AbstractStore() {
        this(null, null);
    }

    protected AbstractStore(SearchManager searchManager, String cacheName) {
        this.searchManager = searchManager;
        this.cacheName = cacheName;
    }

    protected synchronized List<StoreListener> getEventListenerList() {
        if (this.listenerList == null) {
            this.listenerList = new ArrayList<StoreListener>();
        }
        return this.listenerList;
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
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNodeCoherent(boolean coherent) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void waitUntilClusterCoherent() throws UnsupportedOperationException, TerracottaNotRunningException, InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void addStoreListener(StoreListener listener) {
        this.removeStoreListener(listener);
        this.getEventListenerList().add(listener);
    }

    @Override
    public synchronized void removeStoreListener(StoreListener listener) {
        this.getEventListenerList().remove(listener);
    }

    @Override
    public void setAttributeExtractors(Map<String, AttributeExtractor> extractors) {
        if (this.searchManager == null && !extractors.isEmpty()) {
            throw new InvalidConfigurationException("Search attributes not supported by this store type: " + this.getClass().getName());
        }
        this.attributeExtractors.putAll(extractors);
    }

    @Override
    public Results executeQuery(StoreQuery query) {
        if (this.searchManager == null) {
            throw new UnsupportedOperationException("Query execution not supported by this store type: " + this.getClass().getName());
        }
        DynamicAttributesExtractor dynExtractor = query.getCache().getCacheConfiguration().getDynamicExtractor();
        return this.searchManager.executeQuery(query, this.attributeExtractors, dynExtractor);
    }

    @Override
    public <T> Attribute<T> getSearchAttribute(String attributeName) throws CacheException {
        Attribute attr = new Attribute(attributeName);
        return this.getSearchAttributes().contains(attr) ? attr : null;
    }

    @Override
    public Set<Attribute> getSearchAttributes() {
        if (this.searchManager == null) {
            throw new InvalidConfigurationException("Search attributes not supported by this store type: " + this.getClass().getName());
        }
        return new HashSet<Attribute>(this.searchManager.getSearchAttributes(this.cacheName));
    }

    @Override
    public void putAll(Collection<Element> elements) throws CacheException {
        for (Element element : elements) {
            this.put(element);
        }
    }

    @Override
    public void removeAll(Collection<?> keys) {
        for (Object key : keys) {
            this.remove(key);
        }
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
    public boolean hasAbortedSizeOf() {
        return false;
    }

    @Override
    public void recalculateSize(Object key) {
    }
}

