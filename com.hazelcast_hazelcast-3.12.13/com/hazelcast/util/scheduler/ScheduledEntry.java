/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.scheduler;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class ScheduledEntry<K, V>
implements Map.Entry<K, V> {
    private final K key;
    private final V value;
    private final long scheduledDelayMillis;
    private final int actualDelaySeconds;
    private final long scheduleId;

    public ScheduledEntry(K key, V value, long scheduledDelayMillis, int actualDelaySeconds, long scheduleId) {
        this.key = key;
        this.value = value;
        this.scheduledDelayMillis = scheduledDelayMillis;
        this.actualDelaySeconds = actualDelaySeconds;
        this.scheduleId = scheduleId;
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public V setValue(V value) {
        throw new RuntimeException("Operation is not supported");
    }

    public long getScheduledDelayMillis() {
        return this.scheduledDelayMillis;
    }

    public int getActualDelaySeconds() {
        return this.actualDelaySeconds;
    }

    public long getScheduleId() {
        return this.scheduleId;
    }

    public long getActualDelayMillis() {
        return TimeUnit.SECONDS.toMillis(this.actualDelaySeconds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ScheduledEntry that = (ScheduledEntry)o;
        if (this.key != null ? !this.key.equals(that.key) : that.key != null) {
            return false;
        }
        return !(this.value != null ? !this.value.equals(that.value) : that.value != null);
    }

    @Override
    public int hashCode() {
        int result = this.key != null ? this.key.hashCode() : 0;
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "ScheduledEntry{key=" + this.key + ", value=" + this.value + ", scheduledDelayMillis=" + this.scheduledDelayMillis + ", actualDelaySeconds=" + this.actualDelaySeconds + ", scheduleId=" + this.scheduleId + '}';
    }
}

