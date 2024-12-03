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
import net.sf.ehcache.Status;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.Policy;
import net.sf.ehcache.store.StoreListener;
import net.sf.ehcache.store.StoreQuery;
import net.sf.ehcache.terracotta.TerracottaNotRunningException;
import net.sf.ehcache.writer.CacheWriterManager;

public interface Store {
    public static final String CLUSTER_COHERENT = "ClusterCoherent";
    public static final String NODE_COHERENT = "NodeCoherent";

    public void addStoreListener(StoreListener var1);

    public void removeStoreListener(StoreListener var1);

    public boolean put(Element var1) throws CacheException;

    public void putAll(Collection<Element> var1) throws CacheException;

    public boolean putWithWriter(Element var1, CacheWriterManager var2) throws CacheException;

    public Element get(Object var1);

    public Element getQuiet(Object var1);

    public List getKeys();

    public Element remove(Object var1);

    public void removeAll(Collection<?> var1);

    public Element removeWithWriter(Object var1, CacheWriterManager var2) throws CacheException;

    public void removeAll() throws CacheException;

    public Element putIfAbsent(Element var1) throws NullPointerException;

    public Element removeElement(Element var1, ElementValueComparator var2) throws NullPointerException;

    public boolean replace(Element var1, Element var2, ElementValueComparator var3) throws NullPointerException, IllegalArgumentException;

    public Element replace(Element var1) throws NullPointerException;

    public void dispose();

    public int getSize();

    public int getInMemorySize();

    public int getOffHeapSize();

    public int getOnDiskSize();

    public int getTerracottaClusteredSize();

    public long getInMemorySizeInBytes();

    public long getOffHeapSizeInBytes();

    public long getOnDiskSizeInBytes();

    public boolean hasAbortedSizeOf();

    public Status getStatus();

    public boolean containsKey(Object var1);

    public boolean containsKeyOnDisk(Object var1);

    public boolean containsKeyOffHeap(Object var1);

    public boolean containsKeyInMemory(Object var1);

    public void expireElements();

    public void flush() throws IOException;

    public boolean bufferFull();

    public Policy getInMemoryEvictionPolicy();

    public void setInMemoryEvictionPolicy(Policy var1);

    public Object getInternalContext();

    public boolean isCacheCoherent();

    public boolean isClusterCoherent() throws TerracottaNotRunningException;

    public boolean isNodeCoherent() throws TerracottaNotRunningException;

    public void setNodeCoherent(boolean var1) throws UnsupportedOperationException, TerracottaNotRunningException;

    public void waitUntilClusterCoherent() throws UnsupportedOperationException, TerracottaNotRunningException, InterruptedException;

    public Object getMBean();

    public void setAttributeExtractors(Map<String, AttributeExtractor> var1);

    public Results executeQuery(StoreQuery var1) throws SearchException;

    public Set<Attribute> getSearchAttributes();

    public <T> Attribute<T> getSearchAttribute(String var1);

    public Map<Object, Element> getAllQuiet(Collection<?> var1);

    public Map<Object, Element> getAll(Collection<?> var1);

    public void recalculateSize(Object var1);
}

