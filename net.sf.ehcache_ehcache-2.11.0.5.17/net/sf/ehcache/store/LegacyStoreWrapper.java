/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.concurrent.LockType;
import net.sf.ehcache.concurrent.ReadWriteLockSync;
import net.sf.ehcache.concurrent.StripedReadWriteLockSync;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.event.RegisteredEventListeners;
import net.sf.ehcache.store.AbstractStore;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.Policy;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.writer.CacheWriterManager;
import org.terracotta.context.annotations.ContextChild;

public class LegacyStoreWrapper
extends AbstractStore {
    private static final int SYNC_STRIPES = 64;
    @ContextChild
    private final Store memory;
    @ContextChild
    private final Store disk;
    private final RegisteredEventListeners eventListeners;
    private final CacheConfiguration config;
    private final StripedReadWriteLockSync sync = new StripedReadWriteLockSync(64);

    public LegacyStoreWrapper(Store memory, Store disk, RegisteredEventListeners eventListeners, CacheConfiguration config) {
        this.memory = memory;
        this.disk = disk;
        this.eventListeners = eventListeners;
        this.config = config;
    }

    @Override
    public boolean bufferFull() {
        if (this.disk == null) {
            return false;
        }
        return this.disk.bufferFull();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsKey(Object key) {
        ReadWriteLockSync s = this.sync.getSyncForKey(key);
        s.lock(LockType.READ);
        try {
            if (key instanceof Serializable && this.disk != null) {
                boolean bl = this.disk.containsKey(key) || this.memory.containsKey(key);
                return bl;
            }
            boolean bl = this.memory.containsKey(key);
            return bl;
        }
        finally {
            s.unlock(LockType.READ);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsKeyInMemory(Object key) {
        ReadWriteLockSync s = this.sync.getSyncForKey(key);
        s.lock(LockType.READ);
        try {
            boolean bl = this.memory.containsKey(key);
            return bl;
        }
        finally {
            s.unlock(LockType.READ);
        }
    }

    @Override
    public boolean containsKeyOffHeap(Object key) {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsKeyOnDisk(Object key) {
        ReadWriteLockSync s = this.sync.getSyncForKey(key);
        s.lock(LockType.READ);
        try {
            if (this.disk != null) {
                boolean bl = this.disk.containsKey(key);
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            s.unlock(LockType.READ);
        }
    }

    @Override
    public void dispose() {
        this.memory.dispose();
        if (this.disk != null) {
            this.disk.dispose();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void expireElements() {
        for (Object key : this.memory.getKeys()) {
            ReadWriteLockSync s = this.sync.getSyncForKey(key);
            s.lock(LockType.WRITE);
            try {
                Element e;
                Element element = this.memory.getQuiet(key);
                if (element == null || !element.isExpired(this.config) || (e = this.remove(key)) == null) continue;
                this.eventListeners.notifyElementExpiry(e, false);
            }
            finally {
                s.unlock(LockType.WRITE);
            }
        }
        if (this.disk != null) {
            this.disk.expireElements();
        }
    }

    @Override
    public void flush() throws IOException {
        this.memory.flush();
        if (this.disk != null) {
            this.disk.flush();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element get(Object key) {
        ReadWriteLockSync s = this.sync.getSyncForKey(key);
        s.lock(LockType.READ);
        try {
            Element e = this.memory.get(key);
            if (e == null && this.disk != null && (e = this.disk.get(key)) != null) {
                this.memory.put(e);
            }
            Element element = e;
            return element;
        }
        finally {
            s.unlock(LockType.READ);
        }
    }

    @Override
    public Policy getInMemoryEvictionPolicy() {
        return this.memory.getInMemoryEvictionPolicy();
    }

    @Override
    public int getInMemorySize() {
        return this.memory.getSize();
    }

    @Override
    public long getInMemorySizeInBytes() {
        return this.memory.getInMemorySizeInBytes();
    }

    @Override
    public Object getInternalContext() {
        return this.sync;
    }

    @Override
    public List getKeys() {
        if (this.disk == null) {
            return this.memory.getKeys();
        }
        HashSet keys = new HashSet();
        keys.addAll(this.memory.getKeys());
        keys.addAll(this.disk.getKeys());
        return new ArrayList(keys);
    }

    @Override
    public int getOffHeapSize() {
        if (this.disk == null) {
            return this.memory.getOffHeapSize();
        }
        return this.memory.getOffHeapSize() + this.disk.getOffHeapSize();
    }

    @Override
    public long getOffHeapSizeInBytes() {
        if (this.disk == null) {
            return this.memory.getOffHeapSizeInBytes();
        }
        return this.memory.getOffHeapSizeInBytes() + this.disk.getOffHeapSizeInBytes();
    }

    @Override
    public int getOnDiskSize() {
        if (this.disk != null) {
            return this.disk.getSize();
        }
        return 0;
    }

    @Override
    public long getOnDiskSizeInBytes() {
        if (this.disk != null) {
            return this.disk.getOnDiskSizeInBytes();
        }
        return 0L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element getQuiet(Object key) {
        ReadWriteLockSync s = this.sync.getSyncForKey(key);
        s.lock(LockType.READ);
        try {
            Element e = this.memory.getQuiet(key);
            if (e == null && this.disk != null && (e = this.disk.getQuiet(key)) != null) {
                this.memory.put(e);
            }
            Element element = e;
            return element;
        }
        finally {
            s.unlock(LockType.READ);
        }
    }

    @Override
    public int getSize() {
        if (this.disk != null) {
            HashSet keys = new HashSet();
            keys.addAll(this.memory.getKeys());
            keys.addAll(this.disk.getKeys());
            return keys.size();
        }
        return this.memory.getSize();
    }

    @Override
    public Status getStatus() {
        return this.memory.getStatus();
    }

    @Override
    public int getTerracottaClusteredSize() {
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean put(Element element) throws CacheException {
        if (element == null) {
            return false;
        }
        ReadWriteLockSync s = this.sync.getSyncForKey(element.getObjectKey());
        s.lock(LockType.WRITE);
        try {
            boolean notOnDisk = !this.containsKeyOnDisk(element.getObjectKey());
            boolean bl = this.memory.put(element) && notOnDisk;
            return bl;
        }
        finally {
            s.unlock(LockType.WRITE);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean putWithWriter(Element element, CacheWriterManager writerManager) throws CacheException {
        if (element == null) {
            return false;
        }
        ReadWriteLockSync s = this.sync.getSyncForKey(element.getObjectKey());
        s.lock(LockType.WRITE);
        try {
            boolean notOnDisk = !this.containsKey(element.getObjectKey());
            boolean bl = this.memory.putWithWriter(element, writerManager) && notOnDisk;
            return bl;
        }
        finally {
            s.unlock(LockType.WRITE);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element remove(Object key) {
        ReadWriteLockSync s = this.sync.getSyncForKey(key);
        s.lock(LockType.WRITE);
        try {
            Element m = this.memory.remove(key);
            if (this.disk != null && key instanceof Serializable) {
                Element d = this.disk.remove(key);
                if (m == null) {
                    Element element = d;
                    return element;
                }
            }
            Element element = m;
            return element;
        }
        finally {
            s.unlock(LockType.WRITE);
        }
    }

    @Override
    public void removeAll() throws CacheException {
        this.memory.removeAll();
        if (this.disk != null) {
            this.disk.removeAll();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element removeWithWriter(Object key, CacheWriterManager writerManager) throws CacheException {
        ReadWriteLockSync s = this.sync.getSyncForKey(key);
        s.lock(LockType.WRITE);
        try {
            Element m = this.memory.removeWithWriter(key, writerManager);
            if (this.disk != null && key instanceof Serializable) {
                Element d = this.disk.removeWithWriter(key, writerManager);
                if (m == null) {
                    Element element = d;
                    return element;
                }
            }
            Element element = m;
            return element;
        }
        finally {
            s.unlock(LockType.WRITE);
        }
    }

    @Override
    public void setInMemoryEvictionPolicy(Policy policy) {
        this.memory.setInMemoryEvictionPolicy(policy);
    }

    public Store getMemoryStore() {
        return this.memory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element putIfAbsent(Element element) throws NullPointerException {
        ReadWriteLockSync lock = this.sync.getSyncForKey(element.getObjectKey());
        lock.lock(LockType.WRITE);
        try {
            Element e = this.getQuiet(element.getObjectKey());
            if (e == null) {
                this.put(element);
            }
            Element element2 = e;
            return element2;
        }
        finally {
            lock.unlock(LockType.WRITE);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element removeElement(Element element, ElementValueComparator comparator) throws NullPointerException {
        ReadWriteLockSync lock = this.sync.getSyncForKey(element.getObjectKey());
        lock.lock(LockType.WRITE);
        try {
            Element current = this.getQuiet(element.getObjectKey());
            if (comparator.equals(element, current)) {
                Element element2 = this.remove(current.getObjectKey());
                return element2;
            }
            Element element3 = null;
            return element3;
        }
        finally {
            lock.unlock(LockType.WRITE);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean replace(Element old, Element element, ElementValueComparator comparator) throws NullPointerException, IllegalArgumentException {
        ReadWriteLockSync lock = this.sync.getSyncForKey(old.getObjectKey());
        lock.lock(LockType.WRITE);
        try {
            Element current = this.getQuiet(old.getObjectKey());
            if (comparator.equals(old, current)) {
                this.put(element);
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            lock.unlock(LockType.WRITE);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element replace(Element element) throws NullPointerException {
        ReadWriteLockSync lock = this.sync.getSyncForKey(element.getObjectKey());
        lock.lock(LockType.WRITE);
        try {
            Element current = this.getQuiet(element.getObjectKey());
            if (current != null) {
                this.put(element);
            }
            Element element2 = current;
            return element2;
        }
        finally {
            lock.unlock(LockType.WRITE);
        }
    }

    @Override
    public Object getMBean() {
        return null;
    }
}

