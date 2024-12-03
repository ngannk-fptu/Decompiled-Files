/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.map.impl.record;

import com.hazelcast.map.impl.record.Record;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Metadata;
import com.hazelcast.util.JVMUtil;
import com.hazelcast.util.TimeUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.concurrent.TimeUnit;

public abstract class AbstractRecord<V>
implements Record<V> {
    public static final long EPOCH_TIME = TimeUtil.zeroOutMs(1514764800000L);
    private static final int NUMBER_OF_LONGS = 2;
    private static final int NUMBER_OF_INTS = 5;
    protected Data key;
    protected long version;
    protected int ttl;
    protected int maxIdle;
    @SuppressFBWarnings(value={"VO_VOLATILE_INCREMENT"}, justification="Record can be accessed by only its own partition thread.")
    protected volatile long hits;
    private volatile int lastAccessTime = -1;
    private volatile int lastUpdateTime = -1;
    private int creationTime = -1;
    private Metadata metadata;

    AbstractRecord() {
    }

    @Override
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public Metadata getMetadata() {
        return this.metadata;
    }

    @Override
    public final long getVersion() {
        return this.version;
    }

    @Override
    public final void setVersion(long version) {
        this.version = version;
    }

    @Override
    public long getTtl() {
        return this.ttl == Integer.MAX_VALUE ? Long.MAX_VALUE : TimeUnit.SECONDS.toMillis(this.ttl);
    }

    @Override
    public void setTtl(long ttl) {
        long ttlSeconds = TimeUnit.MILLISECONDS.toSeconds(ttl);
        if (ttlSeconds == 0L && ttl != 0L) {
            ttlSeconds = 1L;
        }
        this.ttl = ttlSeconds > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)ttlSeconds;
    }

    @Override
    public long getMaxIdle() {
        return this.maxIdle == Integer.MAX_VALUE ? Long.MAX_VALUE : TimeUnit.SECONDS.toMillis(this.maxIdle);
    }

    @Override
    public void setMaxIdle(long maxIdle) {
        long maxIdleSeconds = TimeUnit.MILLISECONDS.toSeconds(maxIdle);
        if (maxIdleSeconds == 0L && maxIdle != 0L) {
            maxIdleSeconds = 1L;
        }
        this.maxIdle = maxIdleSeconds > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)maxIdleSeconds;
    }

    @Override
    public long getLastAccessTime() {
        return this.recomputeWithBaseTime(this.lastAccessTime);
    }

    @Override
    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = this.stripBaseTime(lastAccessTime);
    }

    @Override
    public long getLastUpdateTime() {
        return this.recomputeWithBaseTime(this.lastUpdateTime);
    }

    @Override
    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = this.stripBaseTime(lastUpdateTime);
    }

    @Override
    public long getCreationTime() {
        return this.recomputeWithBaseTime(this.creationTime);
    }

    @Override
    public void setCreationTime(long creationTime) {
        this.creationTime = this.stripBaseTime(creationTime);
    }

    @Override
    public long getHits() {
        return this.hits;
    }

    @Override
    public void setHits(long hits) {
        this.hits = hits;
    }

    @Override
    public long getCost() {
        return JVMUtil.REFERENCE_COST_IN_BYTES + 16 + 20;
    }

    @Override
    public void onUpdate(long now) {
        ++this.version;
        this.lastUpdateTime = this.stripBaseTime(now);
    }

    @Override
    public Object getCachedValueUnsafe() {
        return Record.NOT_CACHED;
    }

    @Override
    public void onAccess(long now) {
        ++this.hits;
        this.lastAccessTime = this.stripBaseTime(now);
    }

    @Override
    public void onStore() {
    }

    @Override
    public boolean casCachedValue(Object expectedValue, Object newValue) {
        return true;
    }

    @Override
    public Data getKey() {
        return this.key;
    }

    @Override
    public void setKey(Data key) {
        this.key = key;
    }

    @Override
    public final long getSequence() {
        return -1L;
    }

    @Override
    public final void setSequence(long sequence) {
    }

    @Override
    public long getExpirationTime() {
        return -1L;
    }

    @Override
    public void setExpirationTime(long expirationTime) {
    }

    @Override
    public long getLastStoredTime() {
        return -1L;
    }

    @Override
    public void setLastStoredTime(long lastStoredTime) {
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbstractRecord that = (AbstractRecord)o;
        if (this.version != that.version) {
            return false;
        }
        if (this.ttl != that.ttl) {
            return false;
        }
        if (this.maxIdle != that.maxIdle) {
            return false;
        }
        if (this.creationTime != that.creationTime) {
            return false;
        }
        if (this.hits != that.hits) {
            return false;
        }
        if (this.lastAccessTime != that.lastAccessTime) {
            return false;
        }
        if (this.lastUpdateTime != that.lastUpdateTime) {
            return false;
        }
        return this.key.equals(that.key);
    }

    public int hashCode() {
        int result = this.key != null ? this.key.hashCode() : 0;
        result = 31 * result + (int)(this.version ^ this.version >>> 32);
        result = 31 * result + this.ttl;
        result = 31 * result + this.maxIdle;
        result = 31 * result + this.creationTime;
        result = 31 * result + (int)(this.hits ^ this.hits >>> 32);
        result = 31 * result + this.lastAccessTime;
        result = 31 * result + this.lastUpdateTime;
        return result;
    }

    protected long recomputeWithBaseTime(int value) {
        if (value == -1) {
            return 0L;
        }
        long exploded = TimeUnit.SECONDS.toMillis(value);
        return exploded + EPOCH_TIME;
    }

    protected int stripBaseTime(long value) {
        int diff = -1;
        if (value > 0L) {
            diff = (int)TimeUnit.MILLISECONDS.toSeconds(value - EPOCH_TIME);
        }
        return diff;
    }
}

