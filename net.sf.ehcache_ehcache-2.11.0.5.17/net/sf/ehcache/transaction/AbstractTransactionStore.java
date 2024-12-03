/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.InvalidConfigurationException;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.store.AbstractStore;
import net.sf.ehcache.store.Policy;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.store.StoreQuery;
import net.sf.ehcache.store.TerracottaStore;
import net.sf.ehcache.terracotta.TerracottaNotRunningException;
import net.sf.ehcache.transaction.SoftLockID;
import net.sf.ehcache.writer.writebehind.WriteBehind;
import org.terracotta.context.annotations.ContextChild;

public abstract class AbstractTransactionStore
extends AbstractStore
implements TerracottaStore {
    @ContextChild
    protected final Store underlyingStore;

    protected AbstractTransactionStore(Store underlyingStore) {
        this.underlyingStore = underlyingStore;
    }

    @Override
    public Results executeQuery(StoreQuery query) {
        return this.underlyingStore.executeQuery(query);
    }

    @Override
    public int getInMemorySize() {
        return this.underlyingStore.getInMemorySize();
    }

    @Override
    public int getOffHeapSize() {
        return this.underlyingStore.getOffHeapSize();
    }

    @Override
    public int getOnDiskSize() {
        return this.underlyingStore.getOnDiskSize();
    }

    @Override
    public long getInMemorySizeInBytes() {
        return this.underlyingStore.getInMemorySizeInBytes();
    }

    @Override
    public long getOffHeapSizeInBytes() {
        return this.underlyingStore.getOffHeapSizeInBytes();
    }

    @Override
    public long getOnDiskSizeInBytes() {
        return this.underlyingStore.getOnDiskSizeInBytes();
    }

    @Override
    public boolean containsKeyOnDisk(Object key) {
        return this.underlyingStore.containsKeyOnDisk(key);
    }

    @Override
    public boolean containsKeyOffHeap(Object key) {
        return this.underlyingStore.containsKeyOffHeap(key);
    }

    @Override
    public boolean containsKeyInMemory(Object key) {
        return this.underlyingStore.containsKeyInMemory(key);
    }

    @Override
    public void dispose() {
        this.underlyingStore.dispose();
    }

    @Override
    public Status getStatus() {
        return this.underlyingStore.getStatus();
    }

    @Override
    public void expireElements() {
        this.underlyingStore.expireElements();
    }

    @Override
    public void flush() throws IOException {
        this.underlyingStore.flush();
    }

    @Override
    public boolean bufferFull() {
        return this.underlyingStore.bufferFull();
    }

    @Override
    public Policy getInMemoryEvictionPolicy() {
        return this.underlyingStore.getInMemoryEvictionPolicy();
    }

    @Override
    public void setInMemoryEvictionPolicy(Policy policy) {
        this.underlyingStore.setInMemoryEvictionPolicy(policy);
    }

    @Override
    public Object getInternalContext() {
        return this.underlyingStore.getInternalContext();
    }

    @Override
    public Object getMBean() {
        return this.underlyingStore.getMBean();
    }

    @Override
    public void setNodeCoherent(boolean coherent) {
        if (!coherent) {
            throw new InvalidConfigurationException("a transactional cache cannot be incoherent");
        }
        this.underlyingStore.setNodeCoherent(coherent);
    }

    @Override
    public boolean isNodeCoherent() {
        return this.underlyingStore.isNodeCoherent();
    }

    @Override
    public boolean isCacheCoherent() {
        return this.underlyingStore.isCacheCoherent();
    }

    @Override
    public boolean isClusterCoherent() {
        return this.underlyingStore.isClusterCoherent();
    }

    @Override
    public void waitUntilClusterCoherent() throws TerracottaNotRunningException, UnsupportedOperationException, InterruptedException {
        this.underlyingStore.waitUntilClusterCoherent();
    }

    @Override
    public void setAttributeExtractors(Map<String, AttributeExtractor> extractors) {
        this.underlyingStore.setAttributeExtractors(extractors);
    }

    @Override
    public <T> Attribute<T> getSearchAttribute(String attributeName) throws CacheException {
        return this.underlyingStore.getSearchAttribute(attributeName);
    }

    @Override
    public Set<Attribute> getSearchAttributes() {
        return this.underlyingStore.getSearchAttributes();
    }

    @Override
    public boolean hasAbortedSizeOf() {
        return this.underlyingStore.hasAbortedSizeOf();
    }

    @Override
    public Element unsafeGet(Object key) {
        if (this.underlyingStore instanceof TerracottaStore) {
            return ((TerracottaStore)this.underlyingStore).unsafeGet(key);
        }
        throw new CacheException("underlying store is not an instance of TerracottaStore");
    }

    @Override
    public Set getLocalKeys() {
        if (this.underlyingStore instanceof TerracottaStore) {
            return ((TerracottaStore)this.underlyingStore).getLocalKeys();
        }
        throw new CacheException("underlying store is not an instance of TerracottaStore");
    }

    @Override
    public CacheConfiguration.TransactionalMode getTransactionalMode() {
        if (this.underlyingStore instanceof TerracottaStore) {
            return ((TerracottaStore)this.underlyingStore).getTransactionalMode();
        }
        throw new CacheException("underlying store is not an instance of TerracottaStore");
    }

    @Override
    public WriteBehind createWriteBehind() {
        if (this.underlyingStore instanceof TerracottaStore) {
            return ((TerracottaStore)this.underlyingStore).createWriteBehind();
        }
        throw new CacheException("underlying store is not an instance of TerracottaStore");
    }

    @Override
    public void quickClear() {
        if (this.underlyingStore instanceof TerracottaStore) {
            ((TerracottaStore)this.underlyingStore).quickClear();
        }
        throw new CacheException("underlying store is not an instance of TerracottaStore");
    }

    @Override
    public int quickSize() {
        if (this.underlyingStore instanceof TerracottaStore) {
            return ((TerracottaStore)this.underlyingStore).quickSize();
        }
        throw new CacheException("underlying store is not an instance of TerracottaStore");
    }

    public Element getOldElement(Object key) {
        if (key == null) {
            return null;
        }
        Element oldElement = this.underlyingStore.getQuiet(key);
        if (oldElement == null) {
            return null;
        }
        Object value = oldElement.getObjectValue();
        if (value instanceof SoftLockID) {
            return ((SoftLockID)value).getOldElement();
        }
        return oldElement;
    }

    @Override
    public void notifyCacheEventListenersChanged() {
        if (this.underlyingStore instanceof TerracottaStore) {
            ((TerracottaStore)this.underlyingStore).notifyCacheEventListenersChanged();
        }
    }
}

