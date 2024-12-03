/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.store.disk;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.sf.ehcache.CacheOperationOutcomes;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PinningConfiguration;
import net.sf.ehcache.event.RegisteredEventListeners;
import net.sf.ehcache.pool.PoolAccessor;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.disk.DiskStorageFactory;
import net.sf.ehcache.store.disk.ElementSubstituteFilter;
import net.sf.ehcache.store.disk.HashEntry;
import net.sf.ehcache.util.FindBugsSuppressWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.statistics.observer.OperationObserver;

public class Segment
extends ReentrantReadWriteLock {
    private static final Logger LOG = LoggerFactory.getLogger((String)Segment.class.getName());
    private static final HashEntry NULL_HASH_ENTRY = new HashEntry(null, 0, null, null, new AtomicBoolean(false));
    private static final float LOAD_FACTOR = 0.75f;
    private static final int MAXIMUM_CAPACITY = Integer.highestOneBit(Integer.MAX_VALUE);
    protected volatile int count;
    protected int modCount;
    private final DiskStorageFactory disk;
    private volatile HashEntry[] table;
    private int threshold;
    private final PoolAccessor onHeapPoolAccessor;
    private final PoolAccessor onDiskPoolAccessor;
    private final RegisteredEventListeners cacheEventNotificationService;
    private volatile boolean cachePinned;
    private final OperationObserver<CacheOperationOutcomes.EvictionOutcome> evictionObserver;

    public Segment(int initialCapacity, float loadFactor, DiskStorageFactory primary, CacheConfiguration cacheConfiguration, PoolAccessor onHeapPoolAccessor, PoolAccessor onDiskPoolAccessor, RegisteredEventListeners cacheEventNotificationService, OperationObserver<CacheOperationOutcomes.EvictionOutcome> evictionObserver) {
        this.onHeapPoolAccessor = onHeapPoolAccessor;
        this.onDiskPoolAccessor = onDiskPoolAccessor;
        this.cacheEventNotificationService = cacheEventNotificationService;
        this.evictionObserver = evictionObserver;
        this.table = new HashEntry[initialCapacity];
        this.threshold = (int)((float)this.table.length * loadFactor);
        this.modCount = 0;
        this.disk = primary;
        this.cachePinned = Segment.determineCachePinned(cacheConfiguration);
    }

    private static boolean determineCachePinned(CacheConfiguration cacheConfiguration) {
        PinningConfiguration pinningConfiguration = cacheConfiguration.getPinningConfiguration();
        if (pinningConfiguration == null) {
            return false;
        }
        switch (pinningConfiguration.getStore()) {
            case LOCALMEMORY: {
                return false;
            }
            case INCACHE: {
                return cacheConfiguration.isOverflowToDisk();
            }
        }
        throw new IllegalArgumentException();
    }

    private HashEntry getFirst(int hash) {
        HashEntry[] tab = this.table;
        return tab[hash & tab.length - 1];
    }

    private Element decode(Object object) {
        DiskStorageFactory.DiskSubstitute substitute = (DiskStorageFactory.DiskSubstitute)object;
        return substitute.getFactory().retrieve(substitute);
    }

    private Element decodeHit(Object object) {
        DiskStorageFactory.DiskSubstitute substitute = (DiskStorageFactory.DiskSubstitute)object;
        return substitute.getFactory().retrieve(substitute, this);
    }

    private void free(Object object) {
        this.free(object, false);
    }

    private void free(Object object, boolean faultFailure) {
        DiskStorageFactory.DiskSubstitute diskSubstitute = (DiskStorageFactory.DiskSubstitute)object;
        diskSubstitute.getFactory().free(this.writeLock(), diskSubstitute, faultFailure);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Element get(Object key, int hash, boolean markFaulted) {
        this.readLock().lock();
        try {
            if (this.count != 0) {
                HashEntry e = this.getFirst(hash);
                while (e != null) {
                    if (e.hash == hash && key.equals(e.key)) {
                        if (markFaulted) {
                            e.faulted.set(true);
                        }
                        Element element = this.decodeHit(e.element);
                        return element;
                    }
                    e = e.next;
                }
            }
            Element element = null;
            return element;
        }
        finally {
            this.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Object unretrievedGet(Object key, int hash) {
        this.readLock().lock();
        try {
            if (this.count != 0) {
                HashEntry e = this.getFirst(hash);
                while (e != null) {
                    if (e.hash == hash && key.equals(e.key)) {
                        DiskStorageFactory.DiskSubstitute diskSubstitute = e.element;
                        return diskSubstitute;
                    }
                    e = e.next;
                }
            }
        }
        finally {
            this.readLock().unlock();
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean containsKey(Object key, int hash) {
        this.readLock().lock();
        try {
            if (this.count != 0) {
                HashEntry e = this.getFirst(hash);
                while (e != null) {
                    if (e.hash == hash && key.equals(e.key)) {
                        boolean bl = true;
                        return bl;
                    }
                    e = e.next;
                }
            }
            boolean bl = false;
            return bl;
        }
        finally {
            this.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean replace(Object key, int hash, Element oldElement, Element newElement, ElementValueComparator comparator) {
        boolean installed = false;
        DiskStorageFactory.DiskSubstitute encoded = this.disk.create(newElement);
        this.writeLock().lock();
        try {
            HashEntry e = this.getFirst(hash);
            while (!(e == null || e.hash == hash && key.equals(e.key))) {
                e = e.next;
            }
            boolean replaced = false;
            if (e != null && comparator.equals(oldElement, this.decode(e.element))) {
                replaced = true;
                DiskStorageFactory.DiskSubstitute onDiskSubstitute = e.element;
                long deltaHeapSize = this.onHeapPoolAccessor.replace(onDiskSubstitute.onHeapSize, key, encoded, NULL_HASH_ENTRY, this.cachePinned);
                if (deltaHeapSize == Long.MIN_VALUE) {
                    LOG.debug("replace3 failed to add on heap");
                    this.free(encoded);
                    boolean bl = false;
                    return bl;
                }
                LOG.debug("replace3 added {} on heap", (Object)deltaHeapSize);
                encoded.onHeapSize = onDiskSubstitute.onHeapSize + deltaHeapSize;
                e.element = encoded;
                e.faulted.set(false);
                installed = true;
                this.free(onDiskSubstitute);
                if (onDiskSubstitute instanceof DiskStorageFactory.DiskMarker) {
                    long outgoingDiskSize = this.onDiskPoolAccessor.delete(((DiskStorageFactory.DiskMarker)onDiskSubstitute).getSize());
                    LOG.debug("replace3 removed {} from disk", (Object)outgoingDiskSize);
                }
                this.cacheEventNotificationService.notifyElementUpdatedOrdered(oldElement, newElement);
            } else {
                this.free(encoded);
            }
            boolean bl = replaced;
            return bl;
        }
        finally {
            this.writeLock().unlock();
            if (installed) {
                encoded.installed();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Element replace(Object key, int hash, Element newElement) {
        boolean installed = false;
        DiskStorageFactory.DiskSubstitute encoded = this.disk.create(newElement);
        this.writeLock().lock();
        try {
            HashEntry e = this.getFirst(hash);
            while (!(e == null || e.hash == hash && key.equals(e.key))) {
                e = e.next;
            }
            Element oldElement = null;
            if (e != null) {
                DiskStorageFactory.DiskSubstitute onDiskSubstitute = e.element;
                long deltaHeapSize = this.onHeapPoolAccessor.replace(onDiskSubstitute.onHeapSize, key, encoded, NULL_HASH_ENTRY, this.cachePinned);
                if (deltaHeapSize == Long.MIN_VALUE) {
                    LOG.debug("replace2 failed to add on heap");
                    this.free(encoded);
                    Element element = null;
                    return element;
                }
                LOG.debug("replace2 added {} on heap", (Object)deltaHeapSize);
                encoded.onHeapSize = onDiskSubstitute.onHeapSize + deltaHeapSize;
                e.element = encoded;
                e.faulted.set(false);
                installed = true;
                oldElement = this.decode(onDiskSubstitute);
                this.free(onDiskSubstitute);
                if (onDiskSubstitute instanceof DiskStorageFactory.DiskMarker) {
                    long outgoingDiskSize = this.onDiskPoolAccessor.delete(((DiskStorageFactory.DiskMarker)onDiskSubstitute).getSize());
                    LOG.debug("replace2 removed {} from disk", (Object)outgoingDiskSize);
                }
                this.cacheEventNotificationService.notifyElementUpdatedOrdered(oldElement, newElement);
            } else {
                this.free(encoded);
            }
            Element element = oldElement;
            return element;
        }
        finally {
            this.writeLock().unlock();
            if (installed) {
                encoded.installed();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Element put(Object key, int hash, Element element, boolean onlyIfAbsent, boolean faulted) {
        boolean installed = false;
        DiskStorageFactory.DiskSubstitute encoded = this.disk.create(element);
        long incomingHeapSize = this.onHeapPoolAccessor.add(key, encoded, NULL_HASH_ENTRY, this.cachePinned || faulted);
        if (incomingHeapSize < 0L) {
            LOG.debug("put failed to add on heap");
            this.evictionObserver.end(CacheOperationOutcomes.EvictionOutcome.SUCCESS);
            this.cacheEventNotificationService.notifyElementEvicted(element, false);
            return null;
        }
        LOG.debug("put added {} on heap", (Object)incomingHeapSize);
        encoded.onHeapSize = incomingHeapSize;
        this.writeLock().lock();
        try {
            Element oldElement;
            HashEntry first;
            if (this.count + 1 > this.threshold) {
                this.rehash();
            }
            HashEntry[] tab = this.table;
            int index = hash & tab.length - 1;
            HashEntry e = first = tab[index];
            while (!(e == null || e.hash == hash && key.equals(e.key))) {
                e = e.next;
            }
            if (e != null) {
                DiskStorageFactory.DiskSubstitute onDiskSubstitute = e.element;
                if (!onlyIfAbsent) {
                    e.element = encoded;
                    installed = true;
                    oldElement = this.decode(onDiskSubstitute);
                    this.free(onDiskSubstitute);
                    long existingHeapSize = this.onHeapPoolAccessor.delete(onDiskSubstitute.onHeapSize);
                    LOG.debug("put updated, deleted {} on heap", (Object)existingHeapSize);
                    if (onDiskSubstitute instanceof DiskStorageFactory.DiskMarker) {
                        long existingDiskSize = this.onDiskPoolAccessor.delete(((DiskStorageFactory.DiskMarker)onDiskSubstitute).getSize());
                        LOG.debug("put updated, deleted {} on disk", (Object)existingDiskSize);
                    }
                    e.faulted.set(faulted);
                    this.cacheEventNotificationService.notifyElementUpdatedOrdered(oldElement, element);
                } else {
                    oldElement = this.decode(onDiskSubstitute);
                    this.free(encoded);
                    long outgoingHeapSize = this.onHeapPoolAccessor.delete(encoded.onHeapSize);
                    LOG.debug("put if absent failed, deleted {} on heap", (Object)outgoingHeapSize);
                }
            } else {
                oldElement = null;
                ++this.modCount;
                tab[index] = new HashEntry(key, hash, first, encoded, new AtomicBoolean(faulted));
                installed = true;
                ++this.count;
                this.cacheEventNotificationService.notifyElementPutOrdered(element);
            }
            Element element2 = oldElement;
            return element2;
        }
        finally {
            this.writeLock().unlock();
            if (installed) {
                encoded.installed();
            }
        }
    }

    boolean putRawIfAbsent(Object key, int hash, DiskStorageFactory.DiskMarker encoded) throws IllegalArgumentException {
        this.writeLock().lock();
        try {
            HashEntry first;
            if (!this.onDiskPoolAccessor.canAddWithoutEvicting(key, null, encoded)) {
                boolean bl = false;
                return bl;
            }
            long incomingHeapSize = this.onHeapPoolAccessor.add(key, encoded, NULL_HASH_ENTRY, this.cachePinned);
            if (incomingHeapSize < 0L) {
                boolean bl = false;
                return bl;
            }
            encoded.onHeapSize = incomingHeapSize;
            if (this.onDiskPoolAccessor.add(key, null, encoded, this.cachePinned) < 0L) {
                this.onHeapPoolAccessor.delete(encoded.onHeapSize);
                boolean bl = false;
                return bl;
            }
            if (this.count + 1 > this.threshold) {
                this.rehash();
            }
            HashEntry[] tab = this.table;
            int index = hash & tab.length - 1;
            HashEntry e = first = tab[index];
            while (!(e == null || e.hash == hash && key.equals(e.key))) {
                e = e.next;
            }
            if (e == null) {
                ++this.modCount;
                tab[index] = new HashEntry(key, hash, first, encoded, new AtomicBoolean(false));
                ++this.count;
                boolean bl = true;
                return bl;
            }
            this.onHeapPoolAccessor.delete(encoded.onHeapSize);
            this.onDiskPoolAccessor.delete(encoded.getSize());
            throw new IllegalArgumentException("Duplicate key detected");
        }
        finally {
            this.writeLock().unlock();
        }
    }

    private void rehash() {
        HashEntry[] oldTable = this.table;
        int oldCapacity = oldTable.length;
        if (oldCapacity >= MAXIMUM_CAPACITY) {
            return;
        }
        HashEntry[] newTable = new HashEntry[oldCapacity << 1];
        this.threshold = (int)((float)newTable.length * 0.75f);
        int sizeMask = newTable.length - 1;
        for (int i = 0; i < oldCapacity; ++i) {
            int k;
            HashEntry e = oldTable[i];
            if (e == null) continue;
            HashEntry next = e.next;
            int idx = e.hash & sizeMask;
            if (next == null) {
                newTable[idx] = e;
                continue;
            }
            HashEntry lastRun = e;
            int lastIdx = idx;
            HashEntry last = next;
            while (last != null) {
                k = last.hash & sizeMask;
                if (k != lastIdx) {
                    lastIdx = k;
                    lastRun = last;
                }
                last = last.next;
            }
            newTable[lastIdx] = lastRun;
            HashEntry p = e;
            while (p != lastRun) {
                k = p.hash & sizeMask;
                HashEntry n = newTable[k];
                newTable[k] = new HashEntry(p.key, p.hash, n, p.element, p.faulted);
                p = p.next;
            }
        }
        this.table = newTable;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Element remove(Object key, int hash, Element value, ElementValueComparator comparator) {
        this.writeLock().lock();
        try {
            HashEntry first;
            HashEntry[] tab = this.table;
            int index = hash & tab.length - 1;
            HashEntry e = first = tab[index];
            while (!(e == null || e.hash == hash && key.equals(e.key))) {
                e = e.next;
            }
            Element oldValue = null;
            if (e != null) {
                oldValue = this.decode(e.element);
                if (value == null || comparator.equals(value, oldValue)) {
                    ++this.modCount;
                    HashEntry newFirst = e.next;
                    HashEntry p = first;
                    while (p != e) {
                        newFirst = new HashEntry(p.key, p.hash, newFirst, p.element, p.faulted);
                        p = p.next;
                    }
                    tab[index] = newFirst;
                    DiskStorageFactory.DiskSubstitute onDiskSubstitute = e.element;
                    this.free(onDiskSubstitute);
                    long outgoingHeapSize = this.onHeapPoolAccessor.delete(onDiskSubstitute.onHeapSize);
                    LOG.debug("remove deleted {} from heap", (Object)outgoingHeapSize);
                    if (onDiskSubstitute instanceof DiskStorageFactory.DiskMarker) {
                        long outgoingDiskSize = this.onDiskPoolAccessor.delete(((DiskStorageFactory.DiskMarker)onDiskSubstitute).getSize());
                        LOG.debug("remove deleted {} from disk", (Object)outgoingDiskSize);
                    }
                    this.cacheEventNotificationService.notifyElementRemovedOrdered(oldValue);
                    --this.count;
                } else {
                    oldValue = null;
                }
            }
            if (oldValue == null) {
                LOG.debug("remove deleted nothing");
            }
            Element element = oldValue;
            return element;
        }
        finally {
            this.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void clear() {
        this.writeLock().lock();
        try {
            if (this.count != 0) {
                HashEntry[] tab = this.table;
                for (int i = 0; i < tab.length; ++i) {
                    HashEntry e = tab[i];
                    while (e != null) {
                        this.free(e.element);
                        e = e.next;
                    }
                    tab[i] = null;
                }
                ++this.modCount;
                this.count = 0;
            }
            this.onHeapPoolAccessor.clear();
            LOG.debug("cleared heap usage");
            this.onDiskPoolAccessor.clear();
            LOG.debug("cleared disk usage");
        }
        finally {
            this.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean fault(Object key, int hash, DiskStorageFactory.Placeholder expect, DiskStorageFactory.DiskMarker fault, boolean skipFaulted) {
        this.writeLock().lock();
        try {
            boolean bl = this.faultInternal(key, hash, expect, fault, skipFaulted);
            return bl;
        }
        finally {
            this.writeLock().unlock();
        }
    }

    private boolean faultInternal(Object key, int hash, DiskStorageFactory.Placeholder expect, DiskStorageFactory.DiskMarker fault, boolean skipFaulted) {
        boolean faulted = this.cachePinned;
        if (this.count != 0 && !faulted) {
            HashEntry e = this.getFirst(hash);
            while (e != null) {
                if (e.hash == hash && key.equals(e.key)) {
                    faulted = e.faulted.get();
                }
                e = e.next;
            }
            if (skipFaulted && faulted) {
                this.free(fault, false);
                return true;
            }
            long deltaHeapSize = this.onHeapPoolAccessor.replace(expect.onHeapSize, key, fault, NULL_HASH_ENTRY, faulted || this.cachePinned);
            if (deltaHeapSize == Long.MIN_VALUE) {
                this.remove(key, hash, null, null);
                return false;
            }
            fault.onHeapSize = expect.onHeapSize + deltaHeapSize;
            LOG.debug("fault removed {} from heap", (Object)deltaHeapSize);
            long incomingDiskSize = this.onDiskPoolAccessor.add(key, null, fault, faulted || this.cachePinned);
            if (incomingDiskSize < 0L) {
                this.free(fault, true);
                long deleteSize = this.onHeapPoolAccessor.replace(fault.onHeapSize, key, expect, NULL_HASH_ENTRY, true);
                LOG.debug("fault failed to add on disk, deleted {} from heap", (Object)deleteSize);
                expect.onHeapSize = fault.onHeapSize + deleteSize;
                Element element = this.get(key, hash, false);
                return this.returnSafeDeprecated(key, hash, element);
            }
            LOG.debug("fault added {} on disk", (Object)incomingDiskSize);
            if (this.findAndFree(key, hash, expect, fault)) {
                return true;
            }
            long failDeltaHeapSize = this.onHeapPoolAccessor.replace(fault.onHeapSize, key, expect, NULL_HASH_ENTRY, true);
            LOG.debug("fault installation failed, deleted {} from heap", (Object)failDeltaHeapSize);
            expect.onHeapSize = fault.onHeapSize + failDeltaHeapSize;
            this.onDiskPoolAccessor.delete(incomingDiskSize);
            LOG.debug("fault installation failed deleted {} from disk", (Object)incomingDiskSize);
        }
        this.free(fault, true);
        return false;
    }

    private boolean findAndFree(Object key, int hash, DiskStorageFactory.Placeholder expect, DiskStorageFactory.DiskMarker fault) {
        HashEntry e = this.getFirst(hash);
        while (e != null) {
            if (e.hash == hash && key.equals(e.key) && expect == e.element) {
                e.element = fault;
                this.free(expect);
                return true;
            }
            e = e.next;
        }
        return false;
    }

    @Deprecated
    private boolean returnSafeDeprecated(Object key, int hash, Element element) {
        this.notifyEviction(this.remove(key, hash, null, null));
        return false;
    }

    private void notifyEviction(Element evicted) {
        if (evicted != null) {
            this.cacheEventNotificationService.notifyElementEvicted(evicted, false);
        }
    }

    Element evict(Object key, int hash, DiskStorageFactory.DiskSubstitute value) {
        return this.evict(key, hash, value, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Element evict(Object key, int hash, DiskStorageFactory.DiskSubstitute value, boolean notify) {
        if (this.writeLock().tryLock()) {
            this.evictionObserver.begin();
            Element evictedElement = null;
            try {
                HashEntry first;
                HashEntry[] tab = this.table;
                int index = hash & tab.length - 1;
                HashEntry e = first = tab[index];
                while (!(e == null || e.hash == hash && key.equals(e.key))) {
                    e = e.next;
                }
                if (e != null && !e.faulted.get()) {
                    evictedElement = this.decode(e.element);
                }
                if (!(e == null || value != null && value != e.element || e.faulted.get())) {
                    ++this.modCount;
                    HashEntry newFirst = e.next;
                    HashEntry p = first;
                    while (p != e) {
                        newFirst = new HashEntry(p.key, p.hash, newFirst, p.element, p.faulted);
                        p = p.next;
                    }
                    tab[index] = newFirst;
                    DiskStorageFactory.DiskSubstitute onDiskSubstitute = e.element;
                    this.free(onDiskSubstitute);
                    long outgoingHeapSize = this.onHeapPoolAccessor.delete(onDiskSubstitute.onHeapSize);
                    LOG.debug("evicted {} from heap", (Object)outgoingHeapSize);
                    if (onDiskSubstitute instanceof DiskStorageFactory.DiskMarker) {
                        long outgoingDiskSize = this.onDiskPoolAccessor.delete(((DiskStorageFactory.DiskMarker)onDiskSubstitute).getSize());
                        LOG.debug("evicted {} from disk", (Object)outgoingDiskSize);
                    }
                    if (notify) {
                        this.cacheEventNotificationService.notifyElementRemovedOrdered(evictedElement);
                    }
                    --this.count;
                } else {
                    evictedElement = null;
                }
                Element element = evictedElement;
                return element;
            }
            finally {
                this.writeLock().unlock();
                if (notify && evictedElement != null) {
                    if (evictedElement.isExpired()) {
                        this.cacheEventNotificationService.notifyElementExpiry(evictedElement, false);
                    } else {
                        this.evictionObserver.end(CacheOperationOutcomes.EvictionOutcome.SUCCESS);
                        this.cacheEventNotificationService.notifyElementEvicted(evictedElement, false);
                    }
                }
            }
        }
        return null;
    }

    void addRandomSample(ElementSubstituteFilter filter, int sampleSize, Collection<DiskStorageFactory.DiskSubstitute> sampled, int seed) {
        int tableStart;
        if (this.count == 0) {
            return;
        }
        HashEntry[] tab = this.table;
        int tableIndex = tableStart = seed & tab.length - 1;
        do {
            HashEntry e = tab[tableIndex];
            while (e != null) {
                DiskStorageFactory.DiskSubstitute value = e.element;
                if (!e.faulted.get() && filter.allows(value)) {
                    sampled.add(value);
                }
                e = e.next;
            }
            if (sampled.size() < sampleSize) continue;
            return;
        } while ((tableIndex = tableIndex + 1 & tab.length - 1) != tableStart);
    }

    Iterator<HashEntry> hashIterator() {
        return new HashIterator();
    }

    @Override
    public String toString() {
        return super.toString() + " count: " + this.count;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @FindBugsSuppressWarnings(value={"UL_UNRELEASED_LOCK"})
    boolean cleanUpFailedMarker(Serializable key, int hash) {
        DiskStorageFactory.DiskSubstitute substitute;
        boolean failedMarker;
        block8: {
            boolean readLocked = false;
            failedMarker = false;
            if (!this.isWriteLockedByCurrentThread()) {
                this.readLock().lock();
                readLocked = true;
            }
            substitute = null;
            try {
                if (this.count == 0) break block8;
                HashEntry e = this.getFirst(hash);
                while (e != null) {
                    if (e.hash == hash && key.equals(e.key) && (substitute = e.element) instanceof DiskStorageFactory.Placeholder) {
                        failedMarker = ((DiskStorageFactory.Placeholder)substitute).hasFailedToFlush();
                        break;
                    }
                    e = e.next;
                }
            }
            finally {
                if (readLocked) {
                    this.readLock().unlock();
                }
            }
        }
        if (failedMarker) {
            this.evict(key, hash, substitute, false);
        }
        return failedMarker;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean flush(Object key, int hash, Element element) {
        boolean bl;
        HashEntry e;
        DiskStorageFactory.DiskSubstitute diskSubstitute = null;
        this.readLock().lock();
        try {
            e = this.getFirst(hash);
            while (e != null) {
                if (e.hash != hash || !key.equals(e.key)) break block10;
                boolean b = e.faulted.compareAndSet(true, false);
                diskSubstitute = e.element;
                if (diskSubstitute instanceof DiskStorageFactory.Placeholder) {
                    if (((DiskStorageFactory.Placeholder)diskSubstitute).hasFailedToFlush() && this.evict(key, hash, diskSubstitute) != null) {
                        diskSubstitute = null;
                    }
                } else if (diskSubstitute instanceof DiskStorageFactory.DiskMarker) {
                    DiskStorageFactory.DiskMarker diskMarker = (DiskStorageFactory.DiskMarker)diskSubstitute;
                    diskMarker.updateStats(element);
                }
                bl = b;
                this.readLock().unlock();
                if (diskSubstitute == null || !element.isExpired()) break block11;
                this.evict(key, hash, diskSubstitute);
            }
        }
        catch (Throwable throwable) {
            this.readLock().unlock();
            if (diskSubstitute != null && element.isExpired()) {
                this.evict(key, hash, diskSubstitute);
            }
            throw throwable;
        }
        {
            block10: {
                block11: {
                }
                return bl;
            }
            e = e.next;
            continue;
        }
        this.readLock().unlock();
        if (diskSubstitute != null && element.isExpired()) {
            this.evict(key, hash, diskSubstitute);
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void clearFaultedBit() {
        this.writeLock().lock();
        try {
            HashEntry[] hashEntryArray = this.table;
            int n = hashEntryArray.length;
            for (int i = 0; i < n; ++i) {
                HashEntry hashEntry;
                HashEntry entry = hashEntry = hashEntryArray[i];
                while (entry != null) {
                    entry.faulted.set(false);
                    entry = entry.next;
                }
            }
        }
        finally {
            this.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isFaulted(int hash, Object key) {
        this.readLock().lock();
        try {
            if (this.count != 0) {
                HashEntry e = this.getFirst(hash);
                while (e != null) {
                    if (e.hash == hash && key.equals(e.key)) {
                        boolean bl = e.faulted.get();
                        return bl;
                    }
                    e = e.next;
                }
            }
            boolean bl = false;
            return bl;
        }
        finally {
            this.readLock().unlock();
        }
    }

    final class HashIterator
    implements Iterator<HashEntry> {
        private int nextTableIndex;
        private final HashEntry[] ourTable;
        private HashEntry nextEntry;
        private HashEntry lastReturned;

        private HashIterator() {
            if (Segment.this.count != 0) {
                this.ourTable = Segment.this.table;
                for (int j = this.ourTable.length - 1; j >= 0; --j) {
                    this.nextEntry = this.ourTable[j];
                    if (this.nextEntry == null) continue;
                    this.nextTableIndex = j - 1;
                    return;
                }
            } else {
                this.ourTable = null;
                this.nextTableIndex = -1;
            }
            this.advance();
        }

        private void advance() {
            if (this.nextEntry != null) {
                this.nextEntry = this.nextEntry.next;
                if (this.nextEntry != null) {
                    return;
                }
            }
            while (this.nextTableIndex >= 0) {
                this.nextEntry = this.ourTable[this.nextTableIndex--];
                if (this.nextEntry == null) continue;
                return;
            }
        }

        @Override
        public boolean hasNext() {
            return this.nextEntry != null;
        }

        @Override
        public HashEntry next() {
            if (this.nextEntry == null) {
                throw new NoSuchElementException();
            }
            this.lastReturned = this.nextEntry;
            this.advance();
            return this.lastReturned;
        }

        @Override
        public void remove() {
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            }
            Segment.this.remove(this.lastReturned.key, this.lastReturned.hash, null, null);
            this.lastReturned = null;
        }
    }
}

