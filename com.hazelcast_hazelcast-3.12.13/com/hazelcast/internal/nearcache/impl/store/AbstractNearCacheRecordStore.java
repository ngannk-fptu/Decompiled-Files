/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.store;

import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.core.IFunction;
import com.hazelcast.internal.eviction.EvictionChecker;
import com.hazelcast.internal.eviction.EvictionListener;
import com.hazelcast.internal.eviction.EvictionPolicyEvaluatorProvider;
import com.hazelcast.internal.eviction.impl.evaluator.EvictionPolicyEvaluator;
import com.hazelcast.internal.eviction.impl.strategy.sampling.SamplingEvictionStrategy;
import com.hazelcast.internal.memory.GlobalMemoryAccessorRegistry;
import com.hazelcast.internal.nearcache.NearCache;
import com.hazelcast.internal.nearcache.NearCacheRecord;
import com.hazelcast.internal.nearcache.NearCacheRecordStore;
import com.hazelcast.internal.nearcache.impl.SampleableNearCacheRecordMap;
import com.hazelcast.internal.nearcache.impl.invalidation.MetaDataContainer;
import com.hazelcast.internal.nearcache.impl.invalidation.StaleReadDetector;
import com.hazelcast.monitor.NearCacheStats;
import com.hazelcast.monitor.impl.NearCacheStatsImpl;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.Clock;
import com.hazelcast.util.ExceptionUtil;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public abstract class AbstractNearCacheRecordStore<K, V, KS, R extends NearCacheRecord, NCRM extends SampleableNearCacheRecordMap<KS, R>>
implements NearCacheRecordStore<K, V>,
EvictionListener<KS, R> {
    protected static final AtomicLongFieldUpdater<AbstractNearCacheRecordStore> RESERVATION_ID = AtomicLongFieldUpdater.newUpdater(AbstractNearCacheRecordStore.class, "reservationId");
    protected static final long REFERENCE_SIZE = GlobalMemoryAccessorRegistry.MEM_AVAILABLE ? (long)GlobalMemoryAccessorRegistry.MEM.arrayIndexScale(Object[].class) : 4L;
    protected static final long MILLI_SECONDS_IN_A_SECOND = 1000L;
    protected final long timeToLiveMillis;
    protected final long maxIdleMillis;
    protected final boolean evictionDisabled;
    protected final ClassLoader classLoader;
    protected final InMemoryFormat inMemoryFormat;
    protected final NearCacheConfig nearCacheConfig;
    protected final NearCacheStatsImpl nearCacheStats;
    protected final SerializationService serializationService;
    protected NCRM records;
    protected EvictionChecker evictionChecker;
    protected SamplingEvictionStrategy<KS, R, NCRM> evictionStrategy;
    protected EvictionPolicyEvaluator<KS, R> evictionPolicyEvaluator;
    protected volatile long reservationId;
    protected volatile StaleReadDetector staleReadDetector = StaleReadDetector.ALWAYS_FRESH;

    public AbstractNearCacheRecordStore(NearCacheConfig nearCacheConfig, SerializationService serializationService, ClassLoader classLoader) {
        this(nearCacheConfig, new NearCacheStatsImpl(), serializationService, classLoader);
    }

    protected AbstractNearCacheRecordStore(NearCacheConfig nearCacheConfig, NearCacheStatsImpl nearCacheStats, SerializationService serializationService, ClassLoader classLoader) {
        this.nearCacheConfig = nearCacheConfig;
        this.inMemoryFormat = nearCacheConfig.getInMemoryFormat();
        this.timeToLiveMillis = (long)nearCacheConfig.getTimeToLiveSeconds() * 1000L;
        this.maxIdleMillis = (long)nearCacheConfig.getMaxIdleSeconds() * 1000L;
        this.serializationService = serializationService;
        this.classLoader = classLoader;
        this.nearCacheStats = nearCacheStats;
        this.evictionDisabled = nearCacheConfig.getEvictionConfig().getEvictionPolicy() == EvictionPolicy.NONE;
    }

    @Override
    public void initialize() {
        this.records = this.createNearCacheRecordMap(this.nearCacheConfig);
        EvictionConfig evictionConfig = this.nearCacheConfig.getEvictionConfig();
        this.evictionChecker = this.createNearCacheEvictionChecker(evictionConfig, this.nearCacheConfig);
        if (!this.evictionDisabled) {
            this.evictionStrategy = SamplingEvictionStrategy.INSTANCE;
            this.evictionPolicyEvaluator = EvictionPolicyEvaluatorProvider.getEvictionPolicyEvaluator(evictionConfig, this.classLoader);
        }
    }

    @Override
    public void setStaleReadDetector(StaleReadDetector staleReadDetector) {
        this.staleReadDetector = staleReadDetector;
    }

    public abstract R getRecord(K var1);

    protected abstract EvictionChecker createNearCacheEvictionChecker(EvictionConfig var1, NearCacheConfig var2);

    protected abstract NCRM createNearCacheRecordMap(NearCacheConfig var1);

    protected abstract long getKeyStorageMemoryCost(K var1);

    protected abstract long getRecordStorageMemoryCost(R var1);

    protected abstract R createRecord(V var1);

    protected abstract void updateRecordValue(R var1, V var2);

    protected abstract R getOrCreateToReserve(K var1, Data var2);

    protected abstract V updateAndGetReserved(K var1, V var2, long var3, boolean var5);

    protected abstract R putRecord(K var1, R var2);

    protected abstract boolean containsRecordKey(K var1);

    protected void checkAvailable() {
        if (!this.isAvailable()) {
            throw new IllegalStateException(this.nearCacheConfig.getName() + " named Near Cache record store is not available");
        }
    }

    private boolean isAvailable() {
        return this.records != null;
    }

    protected Data toData(Object obj) {
        return this.serializationService.toData(obj);
    }

    protected V toValue(Object obj) {
        return (V)this.serializationService.toObject(obj);
    }

    protected long getTotalStorageMemoryCost(K key, R record) {
        return this.getKeyStorageMemoryCost(key) + this.getRecordStorageMemoryCost(record);
    }

    protected boolean isRecordExpired(R record) {
        long now = Clock.currentTimeMillis();
        if (record.isExpiredAt(now)) {
            return true;
        }
        return record.isIdleAt(this.maxIdleMillis, now);
    }

    protected V recordToValue(R record) {
        if (record.getValue() == null) {
            return (V)NearCache.CACHED_AS_NULL;
        }
        return this.toValue(record.getValue());
    }

    protected void onGet(K key, V value, R record) {
    }

    protected void onGetError(K key, V value, R record, Throwable error) {
    }

    protected void onPut(K key, V value, R record, R oldRecord) {
    }

    protected void onPutError(K key, V value, R record, R oldRecord, Throwable error) {
    }

    protected void onRemove(K key, R record, boolean removed) {
    }

    protected void onRemoveError(K key, R record, boolean removed, Throwable error) {
    }

    protected void onExpire(K key, R record) {
        this.nearCacheStats.incrementExpirations();
    }

    @Override
    public void onEvict(KS key, R record, boolean wasExpired) {
        if (wasExpired) {
            this.nearCacheStats.incrementExpirations();
        } else {
            this.nearCacheStats.incrementEvictions();
        }
        this.nearCacheStats.decrementOwnedEntryCount();
    }

    @Override
    public V get(K key) {
        this.checkAvailable();
        NearCacheRecord record = null;
        V value = null;
        try {
            record = (NearCacheRecord)this.getRecord(key);
            if (record != null) {
                if (record.getRecordState() != -4L) {
                    return null;
                }
                if (this.staleReadDetector.isStaleRead(key, record)) {
                    this.invalidate(key);
                    this.nearCacheStats.incrementMisses();
                    return null;
                }
                if (this.isRecordExpired(record)) {
                    this.invalidate(key);
                    this.onExpire(key, record);
                    return null;
                }
                this.onRecordAccess(record);
                this.nearCacheStats.incrementHits();
                value = this.recordToValue(record);
                this.onGet(key, value, record);
                return value;
            }
            this.nearCacheStats.incrementMisses();
            return null;
        }
        catch (Throwable error) {
            this.onGetError(key, value, record, error);
            throw ExceptionUtil.rethrow(error);
        }
    }

    @Override
    public void put(K key, Data keyData, V value, Data valueData) {
        this.checkAvailable();
        if (this.evictionDisabled && this.evictionChecker.isEvictionRequired() && !this.containsRecordKey(key)) {
            return;
        }
        R record = null;
        R oldRecord = null;
        try {
            record = this.createRecord(AbstractNearCacheRecordStore.selectInMemoryFormatFriendlyValue(this.inMemoryFormat, value, valueData));
            this.onRecordCreate(key, keyData, record);
            oldRecord = this.putRecord(key, record);
            if (oldRecord == null) {
                this.nearCacheStats.incrementOwnedEntryCount();
            }
            this.onPut(key, value, record, oldRecord);
        }
        catch (Throwable error) {
            this.onPutError(key, value, record, oldRecord, error);
            throw ExceptionUtil.rethrow(error);
        }
    }

    private static Object selectInMemoryFormatFriendlyValue(InMemoryFormat inMemoryFormat, Object value1, Object value2) {
        switch (inMemoryFormat) {
            case OBJECT: {
                return AbstractNearCacheRecordStore.prioritizeObjectValue(value1, value2);
            }
            case BINARY: 
            case NATIVE: {
                return AbstractNearCacheRecordStore.prioritizeDataValue(value1, value2);
            }
        }
        throw new IllegalArgumentException(String.format("Unrecognized in memory format was found: '%s'", new Object[]{inMemoryFormat}));
    }

    private static Object prioritizeObjectValue(Object value1, Object value2) {
        boolean value2NotNull;
        boolean value1NotNull;
        boolean bl = value1NotNull = value1 != null;
        if (value1NotNull && !(value1 instanceof Data)) {
            return value1;
        }
        boolean bl2 = value2NotNull = value2 != null;
        if (value2NotNull && !(value2 instanceof Data)) {
            return value2;
        }
        if (value1NotNull) {
            return value1;
        }
        if (value2NotNull) {
            return value2;
        }
        return null;
    }

    private static Object prioritizeDataValue(Object value1, Object value2) {
        if (value1 instanceof Data) {
            return value1;
        }
        if (value2 instanceof Data) {
            return value2;
        }
        if (value1 != null) {
            return value1;
        }
        if (value2 != null) {
            return value2;
        }
        return null;
    }

    protected boolean canUpdateStats(R record) {
        return record != null && record.getRecordState() == -4L;
    }

    @Override
    public void clear() {
        this.checkAvailable();
        int size = this.records.size();
        this.records.clear();
        this.nearCacheStats.setOwnedEntryCount(0L);
        this.nearCacheStats.setOwnedEntryMemoryCost(0L);
        this.nearCacheStats.incrementInvalidations(size);
        this.nearCacheStats.incrementInvalidationRequests();
    }

    @Override
    public void destroy() {
        this.clear();
    }

    @Override
    public NearCacheStats getNearCacheStats() {
        this.checkAvailable();
        return this.nearCacheStats;
    }

    @Override
    public int size() {
        this.checkAvailable();
        return this.records.size();
    }

    @Override
    public void doEviction(boolean withoutMaxSizeCheck) {
        this.checkAvailable();
        if (!this.evictionDisabled) {
            EvictionChecker evictionChecker = withoutMaxSizeCheck ? null : this.evictionChecker;
            this.evictionStrategy.evict(this.records, this.evictionPolicyEvaluator, evictionChecker, this);
        }
    }

    @Override
    public long tryReserveForUpdate(K key, Data keyData) {
        long reservationId;
        this.checkAvailable();
        if (this.evictionDisabled && this.evictionChecker.isEvictionRequired() && !this.containsRecordKey(key)) {
            return -1L;
        }
        R reservedRecord = this.getOrCreateToReserve(key, keyData);
        if (reservedRecord.casRecordState(-2L, reservationId = this.nextReservationId())) {
            return reservationId;
        }
        return -1L;
    }

    @Override
    public V tryPublishReserved(K key, V value, long reservationId, boolean deserialize) {
        this.checkAvailable();
        return this.updateAndGetReserved(key, value, reservationId, deserialize);
    }

    public StaleReadDetector getStaleReadDetector() {
        return this.staleReadDetector;
    }

    protected void onRecordCreate(K key, Data keyData, R record) {
        record.setCreationTime(Clock.currentTimeMillis());
        this.initInvalidationMetaData(record, key, keyData);
    }

    protected R updateReservedRecordInternal(K key, V value, R reservedRecord, long reservationId) {
        if (!reservedRecord.casRecordState(reservationId, -3L)) {
            return reservedRecord;
        }
        this.updateRecordValue(reservedRecord, value);
        reservedRecord.casRecordState(-3L, -4L);
        this.nearCacheStats.incrementOwnedEntryMemoryCost(this.getTotalStorageMemoryCost(key, reservedRecord));
        this.nearCacheStats.incrementOwnedEntryCount();
        return reservedRecord;
    }

    private void onRecordAccess(R record) {
        record.setAccessTime(Clock.currentTimeMillis());
        record.incrementAccessHit();
    }

    private void initInvalidationMetaData(R record, K key, Data keyData) {
        if (this.staleReadDetector == StaleReadDetector.ALWAYS_FRESH) {
            return;
        }
        int partitionId = this.staleReadDetector.getPartitionId(keyData == null ? this.toData(key) : keyData);
        MetaDataContainer metaDataContainer = this.staleReadDetector.getMetaDataContainer(partitionId);
        record.setPartitionId(partitionId);
        record.setInvalidationSequence(metaDataContainer.getSequence());
        record.setUuid(metaDataContainer.getUuid());
    }

    private long nextReservationId() {
        return RESERVATION_ID.incrementAndGet(this);
    }

    @SerializableByConvention
    protected class ReserveForUpdateFunction
    implements IFunction<K, R> {
        private final Data keyData;

        public ReserveForUpdateFunction(Data keyData) {
            this.keyData = keyData;
        }

        @Override
        public R apply(K key) {
            NearCacheRecord record = null;
            try {
                record = (NearCacheRecord)AbstractNearCacheRecordStore.this.createRecord(null);
                AbstractNearCacheRecordStore.this.onRecordCreate(key, this.keyData, record);
                record.casRecordState(-4L, -2L);
            }
            catch (Throwable throwable) {
                AbstractNearCacheRecordStore.this.onPutError(key, null, record, null, throwable);
                throw ExceptionUtil.rethrow(throwable);
            }
            return record;
        }
    }
}

