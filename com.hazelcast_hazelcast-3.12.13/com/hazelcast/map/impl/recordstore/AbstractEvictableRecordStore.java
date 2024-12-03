/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.recordstore;

import com.hazelcast.config.MapConfig;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.EntryView;
import com.hazelcast.internal.eviction.ClearExpiredRecordsTask;
import com.hazelcast.internal.eviction.ExpiredKey;
import com.hazelcast.internal.nearcache.impl.invalidation.InvalidationQueue;
import com.hazelcast.internal.util.ToHeapDataConverter;
import com.hazelcast.map.impl.ExpirationTimeSetter;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.event.MapEventPublisher;
import com.hazelcast.map.impl.eviction.Evictor;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.recordstore.AbstractRecordStore;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public abstract class AbstractEvictableRecordStore
extends AbstractRecordStore {
    protected final long expiryDelayMillis;
    protected final Address thisAddress;
    protected final EventService eventService;
    protected final MapEventPublisher mapEventPublisher;
    protected final ClearExpiredRecordsTask clearExpiredRecordsTask;
    protected final InvalidationQueue<ExpiredKey> expiredKeys = new InvalidationQueue();
    protected Iterator<Record> expirationIterator;
    protected volatile boolean hasEntryWithCustomExpiration;

    protected AbstractEvictableRecordStore(MapContainer mapContainer, int partitionId) {
        super(mapContainer, partitionId);
        NodeEngine nodeEngine = this.mapServiceContext.getNodeEngine();
        HazelcastProperties hazelcastProperties = nodeEngine.getProperties();
        this.expiryDelayMillis = hazelcastProperties.getMillis(GroupProperty.MAP_EXPIRY_DELAY_SECONDS);
        this.eventService = nodeEngine.getEventService();
        this.mapEventPublisher = this.mapServiceContext.getMapEventPublisher();
        this.thisAddress = nodeEngine.getThisAddress();
        this.clearExpiredRecordsTask = this.mapServiceContext.getExpirationManager().getTask();
    }

    private boolean isRecordStoreExpirable() {
        MapConfig mapConfig = this.mapContainer.getMapConfig();
        return this.hasEntryWithCustomExpiration || mapConfig.getMaxIdleSeconds() > 0 || mapConfig.getTimeToLiveSeconds() > 0;
    }

    @Override
    public void evictExpiredEntries(int percentage, boolean backup) {
        long now = this.getNow();
        int size = this.size();
        int maxIterationCount = this.getMaxIterationCount(size, percentage);
        int maxRetry = 3;
        int loop = 0;
        int evictedEntryCount = 0;
        while ((evictedEntryCount += this.evictExpiredEntriesInternal(maxIterationCount, now, backup)) < maxIterationCount && ++loop <= maxRetry) {
        }
        this.accumulateOrSendExpiredKey(null);
    }

    @Override
    public boolean isExpirable() {
        return this.isRecordStoreExpirable();
    }

    @Override
    public Object get(Data dataKey, boolean backup, Address callerAddress) {
        return this.get(dataKey, backup, callerAddress, true);
    }

    private int getMaxIterationCount(int size, int percentage) {
        int defaultMaxIterationCount = 100;
        float oneHundred = 100.0f;
        float maxIterationCount = (float)size * ((float)percentage / 100.0f);
        if (maxIterationCount <= 100.0f) {
            return 100;
        }
        return Math.round(maxIterationCount);
    }

    private int evictExpiredEntriesInternal(int maxIterationCount, long now, boolean backup) {
        int evictedEntryCount = 0;
        this.initExpirationIterator();
        LinkedList<Record> records = new LinkedList<Record>();
        for (int checkedEntryCount = 0; this.expirationIterator.hasNext() && checkedEntryCount < maxIterationCount; ++checkedEntryCount) {
            records.add(this.expirationIterator.next());
        }
        while (!records.isEmpty()) {
            if (this.getOrNullIfExpired((Record)records.poll(), now, backup) != null) continue;
            ++evictedEntryCount;
        }
        return evictedEntryCount;
    }

    private void initExpirationIterator() {
        if (this.expirationIterator == null || !this.expirationIterator.hasNext()) {
            this.expirationIterator = this.storage.mutationTolerantIterator();
        }
    }

    @Override
    public void evictEntries(Data excludedKey) {
        if (this.shouldEvict()) {
            this.mapContainer.getEvictor().evict(this, excludedKey);
        }
    }

    @Override
    public void sampleAndForceRemoveEntries(int entryCountToRemove) {
        Data dataKey;
        LinkedList<Data> keysToRemove = new LinkedList<Data>();
        Iterable<EntryView> sample = this.storage.getRandomSamples(entryCountToRemove);
        for (EntryView entryView : sample) {
            Data dataKey2 = this.storage.extractRecordFromLazy(entryView).getKey();
            keysToRemove.add(dataKey2);
        }
        while ((dataKey = (Data)keysToRemove.poll()) != null) {
            this.evict(dataKey, true);
        }
    }

    @Override
    public boolean shouldEvict() {
        Evictor evictor = this.mapContainer.getEvictor();
        return evictor != Evictor.NULL_EVICTOR && evictor.checkEvictable(this);
    }

    protected void markRecordStoreExpirable(long ttl, long maxIdle) {
        if (this.isTtlDefined(ttl) || this.isMaxIdleDefined(maxIdle)) {
            this.hasEntryWithCustomExpiration = true;
        }
        if (this.isRecordStoreExpirable()) {
            this.mapServiceContext.getExpirationManager().scheduleExpirationTask();
        }
    }

    protected boolean isTtlDefined(long ttl) {
        return ttl > 0L && ttl < Long.MAX_VALUE;
    }

    protected boolean isMaxIdleDefined(long maxIdle) {
        return maxIdle > 0L && maxIdle < Long.MAX_VALUE;
    }

    protected Record getOrNullIfExpired(Record record, long now, boolean backup) {
        if (!this.isRecordStoreExpirable()) {
            return record;
        }
        if (record == null) {
            return null;
        }
        Data key = record.getKey();
        if (this.isLocked(key)) {
            return record;
        }
        if (!this.isExpired(record, now, backup)) {
            return record;
        }
        this.evict(key, backup);
        if (!backup) {
            this.doPostEvictionOperations(record);
        }
        return null;
    }

    @Override
    public boolean isExpired(Record record, long now, boolean backup) {
        return record == null || this.isIdleExpired(record, now, backup) || this.isTTLExpired(record, now, backup);
    }

    private boolean isIdleExpired(Record record, long now, boolean backup) {
        long idleMillis;
        if (backup && this.mapServiceContext.getClearExpiredRecordsTask().canPrimaryDriveExpiration()) {
            return false;
        }
        long maxIdleMillis = this.getRecordMaxIdleOrConfig(record);
        if (maxIdleMillis < 1L || maxIdleMillis == Long.MAX_VALUE) {
            return false;
        }
        long idlenessStartTime = ExpirationTimeSetter.getIdlenessStartTime(record);
        long elapsedMillis = now - idlenessStartTime;
        return elapsedMillis >= (idleMillis = ExpirationTimeSetter.calculateExpirationWithDelay(maxIdleMillis, this.expiryDelayMillis, backup));
    }

    private boolean isTTLExpired(Record record, long now, boolean backup) {
        long ttlMillis;
        if (record == null) {
            return false;
        }
        long ttl = this.getRecordTTLOrConfig(record);
        if (ttl < 1L || ttl == Long.MAX_VALUE) {
            return false;
        }
        long ttlStartTime = ExpirationTimeSetter.getLifeStartTime(record);
        long elapsedMillis = now - ttlStartTime;
        return elapsedMillis >= (ttlMillis = ExpirationTimeSetter.calculateExpirationWithDelay(ttl, this.expiryDelayMillis, backup));
    }

    private long getRecordMaxIdleOrConfig(Record record) {
        if (record.getMaxIdle() != -1L) {
            return record.getMaxIdle();
        }
        return TimeUnit.SECONDS.toMillis(this.mapContainer.getMapConfig().getMaxIdleSeconds());
    }

    private long getRecordTTLOrConfig(Record record) {
        if (record.getTtl() != -1L) {
            return record.getTtl();
        }
        return TimeUnit.SECONDS.toMillis(this.mapContainer.getMapConfig().getTimeToLiveSeconds());
    }

    @Override
    public void doPostEvictionOperations(Record record) {
        boolean expired;
        Data key = record.getKey();
        Object value = record.getValue();
        boolean hasEventRegistration = this.eventService.hasEventRegistration("hz:impl:mapService", this.name);
        if (hasEventRegistration) {
            this.mapEventPublisher.publishEvent(this.thisAddress, this.name, EntryEventType.EVICTED, key, value, null);
        }
        long now = this.getNow();
        boolean idleExpired = this.isIdleExpired(record, now, false);
        boolean ttlExpired = this.isTTLExpired(record, now, false);
        boolean bl = expired = idleExpired || ttlExpired;
        if (expired && hasEventRegistration) {
            this.mapEventPublisher.publishEvent(this.thisAddress, this.name, EntryEventType.EXPIRED, key, value, null);
        }
        if (!ttlExpired && idleExpired) {
            this.accumulateOrSendExpiredKey(record);
        }
    }

    @Override
    public InvalidationQueue<ExpiredKey> getExpiredKeysQueue() {
        return this.expiredKeys;
    }

    private void accumulateOrSendExpiredKey(Record record) {
        if (this.mapContainer.getTotalBackupCount() == 0) {
            return;
        }
        if (record != null) {
            this.expiredKeys.offer(new ExpiredKey(ToHeapDataConverter.toHeapData(record.getKey()), record.getCreationTime()));
        }
        this.clearExpiredRecordsTask.tryToSendBackupExpiryOp(this, true);
    }

    @Override
    public void accessRecord(Record record, long now) {
        record.onAccess(now);
        this.updateStatsOnGet(now);
        ExpirationTimeSetter.setExpirationTime(record);
    }

    protected void mergeRecordExpiration(Record record, EntryView mergingEntry) {
        this.mergeRecordExpiration(record, mergingEntry.getTtl(), mergingEntry.getMaxIdle(), mergingEntry.getCreationTime(), mergingEntry.getLastAccessTime(), mergingEntry.getLastUpdateTime());
    }

    protected void mergeRecordExpiration(Record record, SplitBrainMergeTypes.MapMergeTypes mergingEntry) {
        this.mergeRecordExpiration(record, mergingEntry.getTtl(), mergingEntry.getMaxIdle(), mergingEntry.getCreationTime(), mergingEntry.getLastAccessTime(), mergingEntry.getLastUpdateTime());
    }

    private void mergeRecordExpiration(Record record, long ttlMillis, Long maxIdleMillis, long creationTime, long lastAccessTime, long lastUpdateTime) {
        record.setTtl(ttlMillis);
        if (maxIdleMillis != null) {
            record.setMaxIdle(maxIdleMillis);
        }
        record.setCreationTime(creationTime);
        record.setLastAccessTime(lastAccessTime);
        record.setLastUpdateTime(lastUpdateTime);
        ExpirationTimeSetter.setExpirationTime(record);
        this.markRecordStoreExpirable(record.getTtl(), record.getMaxIdle());
    }

    protected final class ReadOnlyRecordIterator
    implements Iterator<Record> {
        private final long now;
        private final boolean checkExpiration;
        private final boolean backup;
        private final Iterator<Record> iterator;
        private Record nextRecord;
        private Record lastReturned;

        protected ReadOnlyRecordIterator(Collection<Record> values, long now, boolean backup) {
            this(values, now, true, backup);
        }

        protected ReadOnlyRecordIterator(Collection<Record> values) {
            this(values, -1L, false, false);
        }

        private ReadOnlyRecordIterator(Collection<Record> values, long now, boolean checkExpiration, boolean backup) {
            this.iterator = values.iterator();
            this.now = now;
            this.checkExpiration = checkExpiration;
            this.backup = backup;
            this.advance();
        }

        @Override
        public boolean hasNext() {
            return this.nextRecord != null;
        }

        @Override
        public Record next() {
            if (this.nextRecord == null) {
                throw new NoSuchElementException();
            }
            this.lastReturned = this.nextRecord;
            this.advance();
            return this.lastReturned;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove() is not supported by this iterator");
        }

        private void advance() {
            long now = this.now;
            boolean checkExpiration = this.checkExpiration;
            Iterator<Record> iterator = this.iterator;
            while (iterator.hasNext()) {
                this.nextRecord = iterator.next();
                if (this.nextRecord == null) continue;
                if (!checkExpiration) {
                    return;
                }
                if (AbstractEvictableRecordStore.this.isExpired(this.nextRecord, now, this.backup)) continue;
                return;
            }
            this.nextRecord = null;
        }
    }
}

