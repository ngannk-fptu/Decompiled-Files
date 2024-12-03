/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.cache.impl.event.CachePartitionLostListener;
import com.hazelcast.config.CachePartitionLostListenerConfig;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.ItemListenerConfig;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.MapPartitionLostListenerConfig;
import com.hazelcast.config.QuorumListenerConfig;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.ItemListener;
import com.hazelcast.map.listener.MapPartitionLostListener;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.quorum.QuorumListener;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.EventListener;

public class ListenerConfigHolder {
    public static final int TYPE_LISTENER_CONFIG = 0;
    public static final int TYPE_ITEM_LISTENER_CONFIG = 1;
    public static final int TYPE_ENTRY_LISTENER_CONFIG = 2;
    public static final int TYPE_QUORUM_LISTENER_CONFIG = 3;
    public static final int TYPE_CACHE_PARTITION_LOST_LISTENER_CONFIG = 4;
    public static final int TYPE_MAP_PARTITION_LOST_LISTENER_CONFIG = 5;
    private final String className;
    private final Data listenerImplementation;
    private final boolean includeValue;
    private final boolean local;
    private final int listenerType;

    public ListenerConfigHolder(int listenerType, String className) {
        this(listenerType, className, true, false);
    }

    public ListenerConfigHolder(int listenerType, Data listenerImplementation) {
        this(listenerType, listenerImplementation, true, false);
    }

    public ListenerConfigHolder(int listenerType, String className, boolean includeValue, boolean local) {
        this.listenerType = listenerType;
        this.className = className;
        this.listenerImplementation = null;
        this.includeValue = includeValue;
        this.local = local;
    }

    public ListenerConfigHolder(int listenerType, Data listenerImplementation, boolean includeValue, boolean local) {
        this.listenerType = listenerType;
        this.className = null;
        this.listenerImplementation = listenerImplementation;
        this.includeValue = includeValue;
        this.local = local;
    }

    public String getClassName() {
        return this.className;
    }

    public Data getListenerImplementation() {
        return this.listenerImplementation;
    }

    public int getListenerType() {
        return this.listenerType;
    }

    public boolean isIncludeValue() {
        return this.includeValue;
    }

    public boolean isLocal() {
        return this.local;
    }

    public <T extends ListenerConfig> T asListenerConfig(SerializationService serializationService) {
        ListenerConfig listenerConfig;
        block17: {
            block16: {
                this.validate();
                listenerConfig = null;
                if (this.className == null) break block16;
                switch (this.listenerType) {
                    case 0: {
                        listenerConfig = new ListenerConfig(this.className);
                        break block17;
                    }
                    case 1: {
                        listenerConfig = new ItemListenerConfig(this.className, this.includeValue);
                        break block17;
                    }
                    case 2: {
                        listenerConfig = new EntryListenerConfig(this.className, this.local, this.includeValue);
                        break block17;
                    }
                    case 3: {
                        listenerConfig = new QuorumListenerConfig(this.className);
                        break block17;
                    }
                    case 4: {
                        listenerConfig = new CachePartitionLostListenerConfig(this.className);
                        break block17;
                    }
                    case 5: {
                        listenerConfig = new MapPartitionLostListenerConfig(this.className);
                        break block17;
                    }
                    default: {
                        throw new HazelcastSerializationException("Unrecognized listener type " + listenerConfig);
                    }
                }
            }
            EventListener eventListener = (EventListener)serializationService.toObject(this.listenerImplementation);
            switch (this.listenerType) {
                case 0: {
                    listenerConfig = new ListenerConfig(eventListener);
                    break;
                }
                case 1: {
                    listenerConfig = new ItemListenerConfig((ItemListener)eventListener, this.includeValue);
                    break;
                }
                case 2: {
                    listenerConfig = new EntryListenerConfig((EntryListener)eventListener, this.local, this.includeValue);
                    break;
                }
                case 3: {
                    listenerConfig = new QuorumListenerConfig((QuorumListener)eventListener);
                    break;
                }
                case 4: {
                    listenerConfig = new CachePartitionLostListenerConfig((CachePartitionLostListener)eventListener);
                    break;
                }
                case 5: {
                    listenerConfig = new MapPartitionLostListenerConfig((MapPartitionLostListener)eventListener);
                    break;
                }
                default: {
                    throw new HazelcastSerializationException("Unrecognized listener type " + listenerConfig);
                }
            }
        }
        return (T)listenerConfig;
    }

    void validate() {
        if (this.className == null && this.listenerImplementation == null) {
            throw new IllegalArgumentException("Either class name or listener implementation must be not null");
        }
    }

    public static ListenerConfigHolder of(ListenerConfig config, SerializationService serializationService) {
        int listenerType = ListenerConfigHolder.listenerTypeOf(config);
        if (config.getClassName() != null) {
            return new ListenerConfigHolder(listenerType, config.getClassName(), config.isIncludeValue(), config.isLocal());
        }
        Object implementationData = serializationService.toData(config.getImplementation());
        return new ListenerConfigHolder(listenerType, (Data)implementationData, config.isIncludeValue(), config.isLocal());
    }

    private static int listenerTypeOf(ListenerConfig config) {
        if (config instanceof ItemListenerConfig) {
            return 1;
        }
        if (config instanceof CachePartitionLostListenerConfig) {
            return 4;
        }
        if (config instanceof QuorumListenerConfig) {
            return 3;
        }
        if (config instanceof EntryListenerConfig) {
            return 2;
        }
        if (config instanceof MapPartitionLostListenerConfig) {
            return 5;
        }
        return 0;
    }
}

