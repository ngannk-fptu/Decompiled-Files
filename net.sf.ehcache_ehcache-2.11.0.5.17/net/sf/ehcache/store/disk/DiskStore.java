/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store.disk;

import java.io.File;
import java.io.IOException;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheOperationOutcomes;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.concurrent.CacheLockProvider;
import net.sf.ehcache.concurrent.LockType;
import net.sf.ehcache.concurrent.StripedReadWriteLock;
import net.sf.ehcache.concurrent.Sync;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheConfigurationListener;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import net.sf.ehcache.pool.Pool;
import net.sf.ehcache.pool.PoolAccessor;
import net.sf.ehcache.pool.PoolParticipant;
import net.sf.ehcache.pool.impl.UnboundedPool;
import net.sf.ehcache.statistics.StatisticBuilder;
import net.sf.ehcache.store.AbstractStore;
import net.sf.ehcache.store.AuthoritativeTier;
import net.sf.ehcache.store.CacheStore;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.Policy;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.store.StoreOperationOutcomes;
import net.sf.ehcache.store.StripedReadWriteLockProvider;
import net.sf.ehcache.store.cachingtier.OnHeapCachingTier;
import net.sf.ehcache.store.disk.DiskSizeOfEngine;
import net.sf.ehcache.store.disk.DiskStorageFactory;
import net.sf.ehcache.store.disk.ElementSubstituteFilter;
import net.sf.ehcache.store.disk.HashEntry;
import net.sf.ehcache.store.disk.Segment;
import net.sf.ehcache.store.disk.StoreUpdateException;
import net.sf.ehcache.writer.CacheWriterManager;
import org.terracotta.statistics.OperationStatistic;
import org.terracotta.statistics.Statistic;
import org.terracotta.statistics.StatisticsManager;
import org.terracotta.statistics.derived.EventRateSimpleMovingAverage;
import org.terracotta.statistics.derived.OperationResultFilter;
import org.terracotta.statistics.observer.OperationObserver;

public final class DiskStore
extends AbstractStore
implements StripedReadWriteLockProvider,
AuthoritativeTier {
    private static final int FFFFCD7D = -12931;
    private static final int FIFTEEN = 15;
    private static final int TEN = 10;
    private static final int THREE = 3;
    private static final int SIX = 6;
    private static final int FOURTEEN = 14;
    private static final int SIXTEEN = 16;
    private static final int RETRIES_BEFORE_LOCK = 2;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final int DEFAULT_SEGMENT_COUNT = 64;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private final DiskStorageFactory disk;
    private final Random rndm = new Random();
    private final Segment[] segments;
    private final int segmentShift;
    private final AtomicReference<Status> status = new AtomicReference<Status>(Status.STATUS_UNINITIALISED);
    private final OperationObserver<StoreOperationOutcomes.GetOutcome> getObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(StoreOperationOutcomes.GetOutcome.class).of(this)).named("get")).tag(new String[]{"local-disk"})).build();
    private final OperationObserver<StoreOperationOutcomes.PutOutcome> putObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(StoreOperationOutcomes.PutOutcome.class).of(this)).named("put")).tag(new String[]{"local-disk"})).build();
    private final OperationObserver<StoreOperationOutcomes.RemoveOutcome> removeObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(StoreOperationOutcomes.RemoveOutcome.class).of(this)).named("remove")).tag(new String[]{"local-disk"})).build();
    private final OperationObserver<CacheOperationOutcomes.EvictionOutcome> evictionObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(CacheOperationOutcomes.EvictionOutcome.class).named("eviction")).of(this)).build();
    private final PoolAccessor onHeapPoolAccessor;
    private final PoolAccessor onDiskPoolAccessor;
    private volatile CacheLockProvider lockProvider;
    private volatile Set<Object> keySet;

    private DiskStore(DiskStorageFactory disk, Ehcache cache, Pool onHeapPool, Pool onDiskPool) {
        this.segments = new Segment[64];
        this.segmentShift = Integer.numberOfLeadingZeros(this.segments.length - 1);
        EventRateSimpleMovingAverage hitRate = new EventRateSimpleMovingAverage(1L, TimeUnit.SECONDS);
        EventRateSimpleMovingAverage missRate = new EventRateSimpleMovingAverage(1L, TimeUnit.SECONDS);
        OperationStatistic<StoreOperationOutcomes.GetOutcome> getStatistic = StatisticsManager.getOperationStatisticFor(this.getObserver);
        getStatistic.addDerivedStatistic((StoreOperationOutcomes.GetOutcome)((Object)new OperationResultFilter<StoreOperationOutcomes.GetOutcome>(EnumSet.of(StoreOperationOutcomes.GetOutcome.HIT), hitRate)));
        getStatistic.addDerivedStatistic((StoreOperationOutcomes.GetOutcome)((Object)new OperationResultFilter<StoreOperationOutcomes.GetOutcome>(EnumSet.of(StoreOperationOutcomes.GetOutcome.MISS), missRate)));
        this.onHeapPoolAccessor = onHeapPool.createPoolAccessor(new DiskStoreHeapPoolParticipant(hitRate, missRate), SizeOfPolicyConfiguration.resolveMaxDepth(cache), SizeOfPolicyConfiguration.resolveBehavior(cache).equals((Object)SizeOfPolicyConfiguration.MaxDepthExceededBehavior.ABORT));
        this.onDiskPoolAccessor = onDiskPool.createPoolAccessor(new DiskStoreDiskPoolParticipant(hitRate, missRate), new DiskSizeOfEngine());
        for (int i = 0; i < this.segments.length; ++i) {
            this.segments[i] = new Segment(16, 0.75f, disk, cache.getCacheConfiguration(), this.onHeapPoolAccessor, this.onDiskPoolAccessor, cache.getCacheEventNotificationService(), this.evictionObserver);
        }
        this.disk = disk;
        this.disk.bind(this);
        this.status.set(Status.STATUS_ALIVE);
    }

    public static DiskStore create(Ehcache cache, Pool onHeapPool, Pool onDiskPool) {
        if (cache.getCacheManager() == null) {
            throw new CacheException("Can't create diskstore without a cache manager");
        }
        DiskStorageFactory disk = new DiskStorageFactory(cache, cache.getCacheEventNotificationService());
        DiskStore store = new DiskStore(disk, cache, onHeapPool, onDiskPool);
        cache.getCacheConfiguration().addConfigurationListener(new CacheConfigurationListenerAdapter(disk, onDiskPool));
        return store;
    }

    public static DiskStore create(Cache cache) {
        return DiskStore.create(cache, new UnboundedPool(), new UnboundedPool());
    }

    public static Store createCacheStore(Ehcache cache, Pool onHeapPool, Pool onDiskPool) {
        CacheConfiguration config = cache.getCacheConfiguration();
        if (!config.isOverflowToDisk()) {
            throw new CacheException("DiskBackedMemoryStore can only be used for cache overflowing to disk");
        }
        DiskStore result = DiskStore.create(cache, onHeapPool, onDiskPool);
        DiskStore diskStore = result;
        OnHeapCachingTier<Object, Element> onHeapCache = OnHeapCachingTier.createOnHeapCache(cache, onHeapPool);
        return new CacheStore(onHeapCache, diskStore, cache.getCacheConfiguration());
    }

    @Override
    public StripedReadWriteLock createStripedReadWriteLock() {
        return new DiskStoreStripedReadWriteLock();
    }

    @Override
    public Element fault(Object key, boolean updateStats) {
        this.getObserver.begin();
        if (key == null) {
            this.getObserver.end(StoreOperationOutcomes.GetOutcome.MISS);
            return null;
        }
        int hash = DiskStore.hash(key.hashCode());
        Element e = this.segmentFor(hash).get(key, hash, true);
        if (e == null) {
            this.getObserver.end(StoreOperationOutcomes.GetOutcome.MISS);
        } else {
            this.getObserver.end(StoreOperationOutcomes.GetOutcome.HIT);
        }
        return e;
    }

    @Override
    public boolean putFaulted(Element element) {
        if (element == null) {
            return false;
        }
        this.putObserver.begin();
        Object key = element.getObjectKey();
        int hash = DiskStore.hash(key.hashCode());
        Element oldElement = this.segmentFor(hash).put(key, hash, element, false, true);
        if (oldElement == null) {
            this.putObserver.end(StoreOperationOutcomes.PutOutcome.ADDED);
            return true;
        }
        this.putObserver.end(StoreOperationOutcomes.PutOutcome.UPDATED);
        return false;
    }

    @Override
    public void flush(Element element) {
        Object key = element.getObjectKey();
        int hash = DiskStore.hash(key.hashCode());
        if (this.disk.getOnDiskSize() > this.disk.getDiskCapacity()) {
            this.segmentFor(hash).flush(key, hash, element);
            this.segmentFor(hash).evict(key, hash, null);
        } else {
            this.segmentFor(hash).flush(key, hash, element);
        }
    }

    public boolean isFaulted(Object key) {
        int hash = DiskStore.hash(key.hashCode());
        return this.segmentFor(hash).isFaulted(hash, key);
    }

    public void changeDiskCapacity(int newCapacity) {
        this.disk.setOnDiskCapacity(newCapacity);
    }

    @Override
    public boolean bufferFull() {
        return this.disk.bufferFull();
    }

    @Override
    public boolean containsKeyInMemory(Object key) {
        return false;
    }

    @Override
    public boolean containsKeyOffHeap(Object key) {
        return false;
    }

    @Override
    public boolean containsKeyOnDisk(Object key) {
        return this.containsKey(key);
    }

    @Override
    public void expireElements() {
        this.disk.expireElements();
    }

    @Override
    public void flush() throws IOException {
        this.disk.flush();
    }

    @Override
    public Policy getInMemoryEvictionPolicy() {
        return null;
    }

    @Override
    public int getInMemorySize() {
        return 0;
    }

    @Override
    public long getInMemorySizeInBytes() {
        long size = this.onHeapPoolAccessor.getSize();
        if (size < 0L) {
            return 0L;
        }
        return size;
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
    @Statistic(name="size", tags={"local-disk"})
    public int getOnDiskSize() {
        return this.disk.getOnDiskSize();
    }

    @Override
    @Statistic(name="size-in-bytes", tags={"local-disk"})
    public long getOnDiskSizeInBytes() {
        long size = this.onDiskPoolAccessor.getSize();
        if (size < 0L) {
            return this.disk.getOnDiskSizeInBytes();
        }
        return size;
    }

    @Override
    public int getTerracottaClusteredSize() {
        return 0;
    }

    @Override
    public void setInMemoryEvictionPolicy(Policy policy) {
    }

    public File getDataFile() {
        return this.disk.getDataFile();
    }

    public File getIndexFile() {
        return this.disk.getIndexFile();
    }

    @Override
    public Object getMBean() {
        return null;
    }

    @Override
    public boolean put(Element element) {
        if (element == null) {
            return false;
        }
        this.putObserver.begin();
        Object key = element.getObjectKey();
        int hash = DiskStore.hash(key.hashCode());
        Element oldElement = this.segmentFor(hash).put(key, hash, element, false, false);
        if (oldElement == null) {
            this.putObserver.end(StoreOperationOutcomes.PutOutcome.ADDED);
            return true;
        }
        this.putObserver.end(StoreOperationOutcomes.PutOutcome.UPDATED);
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean putWithWriter(Element element, CacheWriterManager writerManager) {
        Object key = element.getObjectKey();
        int hash = DiskStore.hash(key.hashCode());
        ReentrantReadWriteLock.WriteLock writeLock = this.segmentFor(hash).writeLock();
        writeLock.lock();
        try {
            boolean newPut = this.put(element);
            if (writerManager != null) {
                try {
                    writerManager.put(element);
                }
                catch (RuntimeException e) {
                    throw new StoreUpdateException(e, !newPut);
                }
            }
            boolean bl = newPut;
            return bl;
        }
        finally {
            writeLock.unlock();
        }
    }

    @Override
    public Element get(Object key) {
        this.getObserver.begin();
        Element e = this.getQuiet(key);
        if (e == null) {
            this.getObserver.end(StoreOperationOutcomes.GetOutcome.MISS);
            return null;
        }
        this.getObserver.end(StoreOperationOutcomes.GetOutcome.HIT);
        return e;
    }

    @Override
    public Element getQuiet(Object key) {
        if (key == null) {
            return null;
        }
        int hash = DiskStore.hash(key.hashCode());
        return this.segmentFor(hash).get(key, hash, false);
    }

    public Object unretrievedGet(Object key) {
        if (key == null) {
            return null;
        }
        int hash = DiskStore.hash(key.hashCode());
        return this.segmentFor(hash).unretrievedGet(key, hash);
    }

    public boolean putRawIfAbsent(Object key, DiskStorageFactory.DiskMarker encoded) throws IllegalArgumentException {
        int hash = DiskStore.hash(key.hashCode());
        return this.segmentFor(hash).putRawIfAbsent(key, hash, encoded);
    }

    @Override
    public List getKeys() {
        return new ArrayList<Object>(this.keySet());
    }

    public Set<Object> keySet() {
        if (this.keySet != null) {
            return this.keySet;
        }
        this.keySet = new KeySet();
        return this.keySet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element remove(Object key) {
        if (key == null) {
            return null;
        }
        this.removeObserver.begin();
        try {
            int hash = DiskStore.hash(key.hashCode());
            Element element = this.segmentFor(hash).remove(key, hash, null, null);
            return element;
        }
        finally {
            this.removeObserver.end(StoreOperationOutcomes.RemoveOutcome.SUCCESS);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element removeWithWriter(Object key, CacheWriterManager writerManager) {
        int hash = DiskStore.hash(key.hashCode());
        ReentrantReadWriteLock.WriteLock writeLock = this.segmentFor(hash).writeLock();
        writeLock.lock();
        try {
            Element removed = this.remove(key);
            if (writerManager != null) {
                writerManager.remove(new CacheEntry(key, removed));
            }
            Element element = removed;
            return element;
        }
        finally {
            writeLock.unlock();
        }
    }

    @Override
    public void removeAll() {
        for (Segment s : this.segments) {
            s.clear();
        }
    }

    @Override
    public void dispose() {
        if (this.status.compareAndSet(Status.STATUS_ALIVE, Status.STATUS_SHUTDOWN)) {
            this.clearFaultedBit();
            this.disk.unbind();
            this.onHeapPoolAccessor.unlink();
            this.onDiskPoolAccessor.unlink();
        }
    }

    public void clearFaultedBit() {
        for (Segment segment : this.segments) {
            segment.clearFaultedBit();
        }
    }

    @Override
    public int getSize() {
        Segment[] segs = this.segments;
        long size = -1L;
        for (int k = 0; k < 2 && (size = DiskStore.volatileSize(segs)) < 0L; ++k) {
        }
        if (size < 0L) {
            size = DiskStore.lockedSize(segs);
        }
        if (size > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)size;
    }

    private static long volatileSize(Segment[] segs) {
        int i;
        int[] mc = new int[segs.length];
        long check = 0L;
        long sum = 0L;
        int mcsum = 0;
        for (i = 0; i < segs.length; ++i) {
            sum += (long)segs[i].count;
            mc[i] = segs[i].modCount;
            mcsum += mc[i];
        }
        if (mcsum != 0) {
            for (i = 0; i < segs.length; ++i) {
                check += (long)segs[i].count;
                if (mc[i] == segs[i].modCount) continue;
                return -1L;
            }
        }
        if (check == sum) {
            return sum;
        }
        return -1L;
    }

    private static long lockedSize(Segment[] segs) {
        long size = 0L;
        for (Segment seg : segs) {
            seg.readLock().lock();
        }
        for (Segment seg : segs) {
            size += (long)seg.count;
        }
        for (Segment seg : segs) {
            seg.readLock().unlock();
        }
        return size;
    }

    @Override
    public Status getStatus() {
        return this.status.get();
    }

    @Override
    public boolean containsKey(Object key) {
        int hash = DiskStore.hash(key.hashCode());
        return this.segmentFor(hash).containsKey(key, hash);
    }

    @Override
    public Object getInternalContext() {
        if (this.lockProvider != null) {
            return this.lockProvider;
        }
        this.lockProvider = new LockProvider();
        return this.lockProvider;
    }

    @Override
    public Element putIfAbsent(Element element) throws NullPointerException {
        Object key = element.getObjectKey();
        int hash = DiskStore.hash(key.hashCode());
        return this.segmentFor(hash).put(key, hash, element, true, false);
    }

    @Override
    public Element removeElement(Element element, ElementValueComparator comparator) throws NullPointerException {
        Object key = element.getObjectKey();
        int hash = DiskStore.hash(key.hashCode());
        return this.segmentFor(hash).remove(key, hash, element, comparator);
    }

    @Override
    public boolean replace(Element old, Element element, ElementValueComparator comparator) throws NullPointerException, IllegalArgumentException {
        Object key = element.getObjectKey();
        int hash = DiskStore.hash(key.hashCode());
        return this.segmentFor(hash).replace(key, hash, old, element, comparator);
    }

    @Override
    public Element replace(Element element) throws NullPointerException {
        Object key = element.getObjectKey();
        int hash = DiskStore.hash(key.hashCode());
        return this.segmentFor(hash).replace(key, hash, element);
    }

    public boolean fault(Object key, DiskStorageFactory.Placeholder expect, DiskStorageFactory.DiskMarker fault) {
        int hash = DiskStore.hash(key.hashCode());
        return this.segmentFor(hash).fault(key, hash, expect, fault, this.status.get() == Status.STATUS_SHUTDOWN);
    }

    public boolean evict(Object key, DiskStorageFactory.DiskSubstitute substitute) {
        return this.evictElement(key, substitute) != null;
    }

    public Element evictElement(Object key, DiskStorageFactory.DiskSubstitute substitute) {
        int hash = DiskStore.hash(key.hashCode());
        return this.segmentFor(hash).evict(key, hash, substitute);
    }

    public List<DiskStorageFactory.DiskSubstitute> getRandomSample(ElementSubstituteFilter factory, int sampleSize, Object keyHint) {
        ArrayList<DiskStorageFactory.DiskSubstitute> sampled = new ArrayList<DiskStorageFactory.DiskSubstitute>(sampleSize);
        int randomHash = this.rndm.nextInt();
        int segmentStart = keyHint == null ? randomHash >>> this.segmentShift : DiskStore.hash(keyHint.hashCode()) >>> this.segmentShift;
        int segmentIndex = segmentStart;
        do {
            this.segments[segmentIndex].addRandomSample(factory, sampleSize, sampled, randomHash);
        } while (sampled.size() < sampleSize && (segmentIndex = segmentIndex + 1 & this.segments.length - 1) != segmentStart);
        return sampled;
    }

    private static int hash(int hash) {
        int spread = hash;
        spread += spread << 15 ^ 0xFFFFCD7D;
        spread ^= spread >>> 10;
        spread += spread << 3;
        spread ^= spread >>> 6;
        spread += (spread << 2) + (spread << 14);
        return spread ^ spread >>> 16;
    }

    private Segment segmentFor(int hash) {
        return this.segments[hash >>> this.segmentShift];
    }

    private class DiskStoreDiskPoolParticipant
    extends DiskStorePoolParticipant {
        DiskStoreDiskPoolParticipant(EventRateSimpleMovingAverage hitRate, EventRateSimpleMovingAverage missRate) {
            super(hitRate, missRate);
        }

        @Override
        public long getApproximateCountSize() {
            return DiskStore.this.getOnDiskSize();
        }
    }

    private class DiskStoreHeapPoolParticipant
    extends DiskStorePoolParticipant {
        public DiskStoreHeapPoolParticipant(EventRateSimpleMovingAverage hitRate, EventRateSimpleMovingAverage missRate) {
            super(hitRate, missRate);
        }

        @Override
        public long getApproximateCountSize() {
            return DiskStore.this.getInMemorySize();
        }
    }

    private abstract class DiskStorePoolParticipant
    implements PoolParticipant {
        protected final EventRateSimpleMovingAverage hitRate;
        protected final EventRateSimpleMovingAverage missRate;

        public DiskStorePoolParticipant(EventRateSimpleMovingAverage hitRate, EventRateSimpleMovingAverage missRate) {
            this.hitRate = hitRate;
            this.missRate = missRate;
        }

        @Override
        public boolean evict(int count, long size) {
            return DiskStore.this.disk.evict(count) == count;
        }

        @Override
        public float getApproximateHitRate() {
            return this.hitRate.rate(TimeUnit.SECONDS).floatValue();
        }

        @Override
        public float getApproximateMissRate() {
            return this.missRate.rate(TimeUnit.SECONDS).floatValue();
        }
    }

    private final class DiskStoreStripedReadWriteLock
    implements StripedReadWriteLock {
        private final net.sf.ehcache.concurrent.ReadWriteLockSync[] locks = new net.sf.ehcache.concurrent.ReadWriteLockSync[64];

        private DiskStoreStripedReadWriteLock() {
            for (int i = 0; i < this.locks.length; ++i) {
                this.locks[i] = new net.sf.ehcache.concurrent.ReadWriteLockSync();
            }
        }

        @Override
        public ReadWriteLock getLockForKey(Object key) {
            return this.getSyncForKey(key).getReadWriteLock();
        }

        @Override
        public List<net.sf.ehcache.concurrent.ReadWriteLockSync> getAllSyncs() {
            ArrayList<net.sf.ehcache.concurrent.ReadWriteLockSync> syncs = new ArrayList<net.sf.ehcache.concurrent.ReadWriteLockSync>(this.locks.length);
            Collections.addAll(syncs, this.locks);
            return syncs;
        }

        @Override
        public net.sf.ehcache.concurrent.ReadWriteLockSync getSyncForKey(Object key) {
            return this.locks[this.indexFor(key)];
        }

        private int indexFor(Object key) {
            return DiskStore.hash(key.hashCode()) >>> DiskStore.this.segmentShift;
        }
    }

    private static final class ReadWriteLockSync
    implements Sync {
        private final ReentrantReadWriteLock lock;

        private ReadWriteLockSync(ReentrantReadWriteLock lock) {
            this.lock = lock;
        }

        @Override
        public void lock(LockType type) {
            switch (type) {
                case READ: {
                    this.lock.readLock().lock();
                    break;
                }
                case WRITE: {
                    this.lock.writeLock().lock();
                    break;
                }
                default: {
                    throw new IllegalArgumentException("We don't support any other lock type than READ or WRITE!");
                }
            }
        }

        @Override
        public boolean tryLock(LockType type, long msec) throws InterruptedException {
            switch (type) {
                case READ: {
                    return this.lock.readLock().tryLock(msec, TimeUnit.MILLISECONDS);
                }
                case WRITE: {
                    return this.lock.writeLock().tryLock(msec, TimeUnit.MILLISECONDS);
                }
            }
            throw new IllegalArgumentException("We don't support any other lock type than READ or WRITE!");
        }

        @Override
        public void unlock(LockType type) {
            switch (type) {
                case READ: {
                    this.lock.readLock().unlock();
                    break;
                }
                case WRITE: {
                    this.lock.writeLock().unlock();
                    break;
                }
                default: {
                    throw new IllegalArgumentException("We don't support any other lock type than READ or WRITE!");
                }
            }
        }

        @Override
        public boolean isHeldByCurrentThread(LockType type) {
            switch (type) {
                case READ: {
                    throw new UnsupportedOperationException("Querying of read lock is not supported.");
                }
                case WRITE: {
                    return this.lock.isWriteLockedByCurrentThread();
                }
            }
            throw new IllegalArgumentException("We don't support any other lock type than READ or WRITE!");
        }
    }

    private final class KeyIterator
    extends HashIterator
    implements Iterator<Object> {
        private KeyIterator() {
        }

        @Override
        public Object next() {
            return super.nextEntry().key;
        }
    }

    abstract class HashIterator {
        private int segmentIndex;
        private Iterator<HashEntry> currentIterator;

        HashIterator() {
            this.segmentIndex = DiskStore.this.segments.length;
            while (this.segmentIndex > 0) {
                --this.segmentIndex;
                this.currentIterator = DiskStore.this.segments[this.segmentIndex].hashIterator();
                if (!this.currentIterator.hasNext()) continue;
                return;
            }
        }

        public boolean hasNext() {
            if (this.currentIterator == null) {
                return false;
            }
            if (this.currentIterator.hasNext()) {
                return true;
            }
            while (this.segmentIndex > 0) {
                --this.segmentIndex;
                this.currentIterator = DiskStore.this.segments[this.segmentIndex].hashIterator();
                if (!this.currentIterator.hasNext()) continue;
                return true;
            }
            return false;
        }

        protected HashEntry nextEntry() {
            if (this.currentIterator == null) {
                return null;
            }
            if (this.currentIterator.hasNext()) {
                return this.currentIterator.next();
            }
            while (this.segmentIndex > 0) {
                --this.segmentIndex;
                this.currentIterator = DiskStore.this.segments[this.segmentIndex].hashIterator();
                if (!this.currentIterator.hasNext()) continue;
                return this.currentIterator.next();
            }
            return null;
        }

        public void remove() {
            this.currentIterator.remove();
        }
    }

    private class LockProvider
    implements CacheLockProvider {
        private LockProvider() {
        }

        @Override
        public Sync getSyncForKey(Object key) {
            int hash = key == null ? 0 : DiskStore.hash(key.hashCode());
            return new ReadWriteLockSync(DiskStore.this.segmentFor(hash));
        }
    }

    final class KeySet
    extends AbstractSet<Object> {
        KeySet() {
        }

        @Override
        public Iterator<Object> iterator() {
            return new KeyIterator();
        }

        @Override
        public int size() {
            return DiskStore.this.getSize();
        }

        @Override
        public boolean contains(Object o) {
            return DiskStore.this.containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return DiskStore.this.remove(o) != null;
        }

        @Override
        public void clear() {
            DiskStore.this.removeAll();
        }

        @Override
        public Object[] toArray() {
            ArrayList<Object> c = new ArrayList<Object>();
            for (Object object : this) {
                c.add(object);
            }
            return c.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            ArrayList<Object> c = new ArrayList<Object>();
            for (Object object : this) {
                c.add(object);
            }
            return c.toArray(a);
        }
    }

    private static final class CacheConfigurationListenerAdapter
    implements CacheConfigurationListener {
        private final DiskStorageFactory disk;
        private final Pool diskPool;

        private CacheConfigurationListenerAdapter(DiskStorageFactory disk, Pool diskPool) {
            this.disk = disk;
            this.diskPool = diskPool;
        }

        @Override
        public void timeToIdleChanged(long oldTimeToIdle, long newTimeToIdle) {
        }

        @Override
        public void timeToLiveChanged(long oldTimeToLive, long newTimeToLive) {
        }

        @Override
        public void diskCapacityChanged(int oldCapacity, int newCapacity) {
            this.disk.setOnDiskCapacity(newCapacity);
        }

        @Override
        public void memoryCapacityChanged(int oldCapacity, int newCapacity) {
        }

        @Override
        public void loggingChanged(boolean oldValue, boolean newValue) {
        }

        @Override
        public void registered(CacheConfiguration config) {
        }

        @Override
        public void deregistered(CacheConfiguration config) {
        }

        @Override
        public void maxBytesLocalHeapChanged(long oldValue, long newValue) {
        }

        @Override
        public void maxBytesLocalDiskChanged(long oldValue, long newValue) {
            this.diskPool.setMaxSize(newValue);
        }

        @Override
        public void maxEntriesInCacheChanged(long oldValue, long newValue) {
        }
    }
}

