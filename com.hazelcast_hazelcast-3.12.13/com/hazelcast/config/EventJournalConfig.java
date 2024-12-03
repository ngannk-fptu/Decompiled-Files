/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public class EventJournalConfig
implements IdentifiedDataSerializable {
    public static final int DEFAULT_CAPACITY = 10000;
    public static final int DEFAULT_TTL_SECONDS = 0;
    private String mapName;
    private String cacheName;
    private boolean enabled = true;
    private int capacity = 10000;
    private int timeToLiveSeconds = 0;

    public EventJournalConfig() {
    }

    public EventJournalConfig(EventJournalConfig config) {
        Preconditions.checkNotNull(config, "config can't be null");
        this.enabled = config.enabled;
        this.mapName = config.mapName;
        this.cacheName = config.cacheName;
        this.capacity = config.capacity;
        this.timeToLiveSeconds = config.timeToLiveSeconds;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public EventJournalConfig setCapacity(int capacity) {
        Preconditions.checkPositive(capacity, "capacity can't be smaller than 1");
        this.capacity = capacity;
        return this;
    }

    public int getTimeToLiveSeconds() {
        return this.timeToLiveSeconds;
    }

    public EventJournalConfig setTimeToLiveSeconds(int timeToLiveSeconds) {
        this.timeToLiveSeconds = Preconditions.checkNotNegative(timeToLiveSeconds, "timeToLiveSeconds can't be smaller than 0");
        return this;
    }

    public String toString() {
        return "EventJournalConfig{mapName='" + this.mapName + '\'' + ", cacheName='" + this.cacheName + '\'' + ", enabled=" + this.enabled + ", capacity=" + this.capacity + ", timeToLiveSeconds=" + this.timeToLiveSeconds + '}';
    }

    EventJournalConfig getAsReadOnly() {
        return new EventJournalConfigReadOnly(this);
    }

    public String getMapName() {
        return this.mapName;
    }

    public EventJournalConfig setMapName(String mapName) {
        this.mapName = mapName;
        return this;
    }

    public String getCacheName() {
        return this.cacheName;
    }

    public EventJournalConfig setCacheName(String cacheName) {
        this.cacheName = Preconditions.checkHasText(cacheName, "name must contain text");
        return this;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public EventJournalConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 44;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.mapName);
        out.writeUTF(this.cacheName);
        out.writeBoolean(this.enabled);
        out.writeInt(this.capacity);
        out.writeInt(this.timeToLiveSeconds);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.mapName = in.readUTF();
        this.cacheName = in.readUTF();
        this.enabled = in.readBoolean();
        this.capacity = in.readInt();
        this.timeToLiveSeconds = in.readInt();
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventJournalConfig)) {
            return false;
        }
        EventJournalConfig that = (EventJournalConfig)o;
        if (this.enabled != that.enabled) {
            return false;
        }
        if (this.capacity != that.capacity) {
            return false;
        }
        if (this.timeToLiveSeconds != that.timeToLiveSeconds) {
            return false;
        }
        if (this.mapName != null ? !this.mapName.equals(that.mapName) : that.mapName != null) {
            return false;
        }
        return this.cacheName != null ? this.cacheName.equals(that.cacheName) : that.cacheName == null;
    }

    public final int hashCode() {
        int result = this.mapName != null ? this.mapName.hashCode() : 0;
        result = 31 * result + (this.cacheName != null ? this.cacheName.hashCode() : 0);
        result = 31 * result + (this.enabled ? 1 : 0);
        result = 31 * result + this.capacity;
        result = 31 * result + this.timeToLiveSeconds;
        return result;
    }

    static class EventJournalConfigReadOnly
    extends EventJournalConfig {
        EventJournalConfigReadOnly(EventJournalConfig config) {
            super(config);
        }

        @Override
        public EventJournalConfig setCapacity(int capacity) {
            throw new UnsupportedOperationException("This config is read-only");
        }

        @Override
        public EventJournalConfig setTimeToLiveSeconds(int timeToLiveSeconds) {
            throw new UnsupportedOperationException("This config is read-only");
        }

        @Override
        public EventJournalConfig setEnabled(boolean enabled) {
            throw new UnsupportedOperationException("This config is read-only");
        }

        @Override
        public EventJournalConfig setMapName(String mapName) {
            throw new UnsupportedOperationException("This config is read-only");
        }

        @Override
        public EventJournalConfig setCacheName(String cacheName) {
            throw new UnsupportedOperationException("This config is read-only");
        }
    }
}

