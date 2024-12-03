/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer.impl;

import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.RingbufferStoreConfig;
import com.hazelcast.core.RingbufferStore;
import com.hazelcast.core.RingbufferStoreFactory;
import com.hazelcast.internal.diagnostics.Diagnostics;
import com.hazelcast.internal.diagnostics.StoreLatencyPlugin;
import com.hazelcast.internal.serialization.impl.HeapData;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.ringbuffer.impl.LatencyTrackingRingbufferStore;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.Preconditions;
import java.util.Arrays;

public final class RingbufferStoreWrapper
implements RingbufferStore<Data> {
    private final ObjectNamespace namespace;
    private boolean enabled;
    private InMemoryFormat inMemoryFormat;
    private RingbufferStore store;
    private SerializationService serializationService;

    private RingbufferStoreWrapper(ObjectNamespace namespace) {
        this.namespace = namespace;
    }

    public static RingbufferStoreWrapper create(ObjectNamespace namespace, RingbufferStoreConfig storeConfig, InMemoryFormat inMemoryFormat, SerializationService serializationService, ClassLoader classLoader) {
        Preconditions.checkNotNull(namespace, "namespace should not be null");
        Preconditions.checkNotNull(serializationService, "serializationService should not be null");
        RingbufferStoreWrapper storeWrapper = new RingbufferStoreWrapper(namespace);
        storeWrapper.serializationService = serializationService;
        if (storeConfig == null || !storeConfig.isEnabled()) {
            return storeWrapper;
        }
        RingbufferStore ringbufferStore = RingbufferStoreWrapper.createRingbufferStore(namespace, storeConfig, classLoader);
        if (ringbufferStore != null) {
            storeWrapper.enabled = storeConfig.isEnabled();
            storeWrapper.inMemoryFormat = inMemoryFormat;
            storeWrapper.store = ringbufferStore;
        }
        return storeWrapper;
    }

    private static RingbufferStore createRingbufferStore(ObjectNamespace namespace, RingbufferStoreConfig storeConfig, ClassLoader classLoader) {
        RingbufferStore store = RingbufferStoreWrapper.getRingbufferStore(storeConfig, classLoader);
        if (store == null) {
            store = RingbufferStoreWrapper.getRingbufferStoreFactory(namespace, storeConfig, classLoader);
        }
        return store;
    }

    private static RingbufferStore getRingbufferStore(RingbufferStoreConfig storeConfig, ClassLoader classLoader) {
        if (storeConfig == null) {
            return null;
        }
        return RingbufferStoreWrapper.getOrInstantiate(storeConfig.getStoreImplementation(), classLoader, storeConfig.getClassName());
    }

    private static RingbufferStore getRingbufferStoreFactory(ObjectNamespace namespace, RingbufferStoreConfig storeConfig, ClassLoader classLoader) {
        String className;
        if (storeConfig == null) {
            return null;
        }
        RingbufferStoreFactory implementation = storeConfig.getFactoryImplementation();
        RingbufferStoreFactory factory = RingbufferStoreWrapper.getOrInstantiate(implementation, classLoader, className = storeConfig.getFactoryClassName());
        return factory == null ? null : factory.newRingbufferStore(namespace.getObjectName(), storeConfig.getProperties());
    }

    private static <T> T getOrInstantiate(T instance, ClassLoader classLoader, String className) {
        if (instance != null) {
            return instance;
        }
        try {
            return ClassLoaderUtil.newInstance(classLoader, className);
        }
        catch (Exception ignored) {
            EmptyStatement.ignore(ignored);
            return null;
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    void instrument(NodeEngine nodeEngine) {
        Diagnostics diagnostics = ((NodeEngineImpl)nodeEngine).getDiagnostics();
        StoreLatencyPlugin storeLatencyPlugin = diagnostics.getPlugin(StoreLatencyPlugin.class);
        if (!this.enabled || storeLatencyPlugin == null) {
            return;
        }
        this.store = new LatencyTrackingRingbufferStore(this.store, storeLatencyPlugin, this.namespace);
    }

    @Override
    public void store(long sequence, Data value) {
        Object actualValue = this.isBinaryFormat() ? (Object)Arrays.copyOf(value.toByteArray(), value.totalSize()) : this.serializationService.toObject(value);
        this.store.store(sequence, actualValue);
    }

    public void storeAll(long firstItemSequence, Data[] items) {
        Object[] storedItems = new Object[items.length];
        for (int i = 0; i < items.length; ++i) {
            Data value = items[i];
            storedItems[i] = this.isBinaryFormat() ? (Object)Arrays.copyOf(value.toByteArray(), value.totalSize()) : this.serializationService.toObject(value);
        }
        this.store.storeAll(firstItemSequence, storedItems);
    }

    private boolean isBinaryFormat() {
        return this.inMemoryFormat.equals((Object)InMemoryFormat.BINARY) || this.inMemoryFormat.equals((Object)InMemoryFormat.NATIVE);
    }

    @Override
    public Data load(long sequence) {
        Object val = this.store.load(sequence);
        if (val == null) {
            return null;
        }
        if (this.isBinaryFormat()) {
            byte[] dataBuffer = (byte[])val;
            return new HeapData(Arrays.copyOf(dataBuffer, dataBuffer.length));
        }
        return this.serializationService.toData(val);
    }

    @Override
    public long getLargestSequence() {
        return this.store.getLargestSequence();
    }
}

