/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.config.MapConfig;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.util.Preconditions;
import java.util.concurrent.TimeUnit;

public final class ExpirationTimeSetter {
    private ExpirationTimeSetter() {
    }

    public static void setExpirationTime(Record record) {
        long expirationTime = ExpirationTimeSetter.calculateExpirationTime(record);
        record.setExpirationTime(expirationTime);
    }

    private static long calculateExpirationTime(Record record) {
        long ttl = ExpirationTimeSetter.checkedTime(record.getTtl());
        long ttlExpirationTime = ExpirationTimeSetter.sumForExpiration(ttl, ExpirationTimeSetter.getLifeStartTime(record));
        long maxIdle = ExpirationTimeSetter.checkedTime(record.getMaxIdle());
        long maxIdleExpirationTime = ExpirationTimeSetter.sumForExpiration(maxIdle, ExpirationTimeSetter.getIdlenessStartTime(record));
        return Math.min(ttlExpirationTime, maxIdleExpirationTime);
    }

    public static long getIdlenessStartTime(Record record) {
        long lastAccessTime = record.getLastAccessTime();
        return lastAccessTime <= 0L ? record.getCreationTime() : lastAccessTime;
    }

    public static long getLifeStartTime(Record record) {
        long lastUpdateTime = record.getLastUpdateTime();
        return lastUpdateTime <= 0L ? record.getCreationTime() : lastUpdateTime;
    }

    private static long checkedTime(long time) {
        return time <= 0L ? Long.MAX_VALUE : time;
    }

    private static long sumForExpiration(long criteriaTime, long now) {
        if (criteriaTime < 0L || now < 0L) {
            throw new IllegalArgumentException("Parameters can not have negative values");
        }
        if (criteriaTime == 0L) {
            return Long.MAX_VALUE;
        }
        long expirationTime = criteriaTime + now;
        if (expirationTime < 0L) {
            return Long.MAX_VALUE;
        }
        return expirationTime;
    }

    public static void setExpirationTimes(long operationTTLMillis, long operationMaxIdleMillis, Record record, MapConfig mapConfig, boolean consultMapConfig) {
        long ttlMillis = ExpirationTimeSetter.pickTTLMillis(operationTTLMillis, record.getTtl(), mapConfig, consultMapConfig);
        long maxIdleMillis = ExpirationTimeSetter.pickMaxIdleMillis(operationMaxIdleMillis, record.getMaxIdle(), mapConfig, consultMapConfig);
        record.setTtl(ttlMillis);
        record.setMaxIdle(maxIdleMillis);
        ExpirationTimeSetter.setExpirationTime(record);
    }

    private static long pickTTLMillis(long operationTTLMillis, long existingTTLMillis, MapConfig mapConfig, boolean consultMapConfig) {
        if (operationTTLMillis > 0L) {
            return ExpirationTimeSetter.checkedTime(operationTTLMillis);
        }
        if (consultMapConfig && operationTTLMillis < 0L && mapConfig.getTimeToLiveSeconds() > 0) {
            return ExpirationTimeSetter.checkedTime(TimeUnit.SECONDS.toMillis(mapConfig.getTimeToLiveSeconds()));
        }
        if (operationTTLMillis < 0L) {
            return ExpirationTimeSetter.checkedTime(existingTTLMillis);
        }
        return Long.MAX_VALUE;
    }

    private static long pickMaxIdleMillis(long operationMaxIdleMillis, long existingMaxIdleMillis, MapConfig mapConfig, boolean entryCreated) {
        if (operationMaxIdleMillis > 0L) {
            return ExpirationTimeSetter.checkedTime(operationMaxIdleMillis);
        }
        if (entryCreated && operationMaxIdleMillis < 0L && mapConfig.getMaxIdleSeconds() > 0) {
            return ExpirationTimeSetter.checkedTime(TimeUnit.SECONDS.toMillis(mapConfig.getMaxIdleSeconds()));
        }
        if (operationMaxIdleMillis < 0L) {
            return ExpirationTimeSetter.checkedTime(existingMaxIdleMillis);
        }
        return Long.MAX_VALUE;
    }

    public static long calculateExpirationWithDelay(long timeInMillis, long delayMillis, boolean backup) {
        Preconditions.checkNotNegative(timeInMillis, "timeInMillis can't be negative");
        if (backup) {
            long delayedTime = timeInMillis + delayMillis;
            if (delayedTime < 0L) {
                return Long.MAX_VALUE;
            }
            return delayedTime;
        }
        return timeInMillis;
    }
}

