/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.EntryListenerConfigReadOnly;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.MapEvent;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.map.listener.MapClearedListener;
import com.hazelcast.map.listener.MapEvictedListener;
import com.hazelcast.map.listener.MapListener;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.EventListener;

public class EntryListenerConfig
extends ListenerConfig {
    private boolean local;
    private boolean includeValue = true;
    private EntryListenerConfigReadOnly readOnly;

    public EntryListenerConfig() {
    }

    public EntryListenerConfig(String className, boolean local, boolean includeValue) {
        super(className);
        this.local = local;
        this.includeValue = includeValue;
    }

    public EntryListenerConfig(EntryListener implementation, boolean local, boolean includeValue) {
        super(implementation);
        this.local = local;
        this.includeValue = includeValue;
    }

    public EntryListenerConfig(MapListener implementation, boolean local, boolean includeValue) {
        super(EntryListenerConfig.toEntryListener(implementation));
        this.local = local;
        this.includeValue = includeValue;
    }

    public EntryListenerConfig(EntryListenerConfig config) {
        this.includeValue = config.isIncludeValue();
        this.local = config.isLocal();
        this.implementation = config.getImplementation();
        this.className = config.getClassName();
    }

    @Override
    public EntryListenerConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new EntryListenerConfigReadOnly(this);
        }
        return this.readOnly;
    }

    @Override
    public ListenerConfig setImplementation(EventListener implementation) {
        Preconditions.isNotNull(implementation, "implementation");
        this.implementation = EntryListenerConfig.toEntryListener(implementation);
        this.className = null;
        return this;
    }

    @Override
    public EntryListener getImplementation() {
        return (EntryListener)this.implementation;
    }

    private static EventListener toEntryListener(Object implementation) {
        if (implementation instanceof EntryListener) {
            return (EventListener)implementation;
        }
        if (implementation instanceof MapListener) {
            return new MapListenerToEntryListenerAdapter((MapListener)implementation);
        }
        throw new IllegalArgumentException(implementation + " is not an expected EventListener implementation. A valid one has to be an implementation of EntryListener or MapListener");
    }

    public EntryListenerConfig setImplementation(EntryListener implementation) {
        super.setImplementation(implementation);
        return this;
    }

    @Override
    public boolean isLocal() {
        return this.local;
    }

    public EntryListenerConfig setLocal(boolean local) {
        this.local = local;
        return this;
    }

    @Override
    public boolean isIncludeValue() {
        return this.includeValue;
    }

    public EntryListenerConfig setIncludeValue(boolean includeValue) {
        this.includeValue = includeValue;
        return this;
    }

    @Override
    public String toString() {
        return "EntryListenerConfig{local=" + this.local + ", includeValue=" + this.includeValue + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        EntryListenerConfig that = (EntryListenerConfig)o;
        if (this.includeValue != that.includeValue) {
            return false;
        }
        return this.local == that.local;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.local ? 1 : 0);
        result = 31 * result + (this.includeValue ? 1 : 0);
        return result;
    }

    @Override
    public int getId() {
        return 9;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeBoolean(this.local);
        out.writeBoolean(this.includeValue);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.local = in.readBoolean();
        this.includeValue = in.readBoolean();
    }

    public static class MapListenerToEntryListenerAdapter
    implements EntryListener,
    HazelcastInstanceAware,
    IdentifiedDataSerializable {
        private MapListener mapListener;

        public MapListenerToEntryListenerAdapter() {
        }

        public MapListenerToEntryListenerAdapter(MapListener mapListener) {
            this.mapListener = mapListener;
        }

        @Override
        public void entryAdded(EntryEvent event) {
            if (this.mapListener instanceof EntryAddedListener) {
                ((EntryAddedListener)this.mapListener).entryAdded(event);
            }
        }

        @Override
        public void entryEvicted(EntryEvent event) {
            if (this.mapListener instanceof EntryEvictedListener) {
                ((EntryEvictedListener)this.mapListener).entryEvicted(event);
            }
        }

        @Override
        public void entryRemoved(EntryEvent event) {
            if (this.mapListener instanceof EntryRemovedListener) {
                ((EntryRemovedListener)this.mapListener).entryRemoved(event);
            }
        }

        @Override
        public void entryUpdated(EntryEvent event) {
            if (this.mapListener instanceof EntryUpdatedListener) {
                ((EntryUpdatedListener)this.mapListener).entryUpdated(event);
            }
        }

        @Override
        public void mapCleared(MapEvent event) {
            if (this.mapListener instanceof MapClearedListener) {
                ((MapClearedListener)this.mapListener).mapCleared(event);
            }
        }

        @Override
        public void mapEvicted(MapEvent event) {
            if (this.mapListener instanceof MapEvictedListener) {
                ((MapEvictedListener)this.mapListener).mapEvicted(event);
            }
        }

        @Override
        public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
            if (this.mapListener instanceof HazelcastInstanceAware) {
                ((HazelcastInstanceAware)((Object)this.mapListener)).setHazelcastInstance(hazelcastInstance);
            }
        }

        public MapListener getMapListener() {
            return this.mapListener;
        }

        @Override
        public int getFactoryId() {
            return ConfigDataSerializerHook.F_ID;
        }

        @Override
        public int getId() {
            return 43;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeObject(this.mapListener);
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            this.mapListener = (MapListener)in.readObject();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            MapListenerToEntryListenerAdapter that = (MapListenerToEntryListenerAdapter)o;
            return this.mapListener.equals(that.mapListener);
        }

        public int hashCode() {
            return this.mapListener.hashCode();
        }
    }
}

