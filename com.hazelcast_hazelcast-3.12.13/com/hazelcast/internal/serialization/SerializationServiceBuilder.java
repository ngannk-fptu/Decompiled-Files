/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization;

import com.hazelcast.config.SerializationConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ManagedContext;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.function.Supplier;
import java.nio.ByteOrder;

public interface SerializationServiceBuilder {
    public SerializationServiceBuilder setVersion(byte var1);

    public SerializationServiceBuilder setPortableVersion(int var1);

    public SerializationServiceBuilder setClassLoader(ClassLoader var1);

    public SerializationServiceBuilder setConfig(SerializationConfig var1);

    public SerializationServiceBuilder addDataSerializableFactory(int var1, DataSerializableFactory var2);

    public SerializationServiceBuilder addPortableFactory(int var1, PortableFactory var2);

    public SerializationServiceBuilder addClassDefinition(ClassDefinition var1);

    public SerializationServiceBuilder setCheckClassDefErrors(boolean var1);

    public SerializationServiceBuilder setManagedContext(ManagedContext var1);

    public SerializationServiceBuilder setUseNativeByteOrder(boolean var1);

    public SerializationServiceBuilder setByteOrder(ByteOrder var1);

    public SerializationServiceBuilder setHazelcastInstance(HazelcastInstance var1);

    public SerializationServiceBuilder setEnableCompression(boolean var1);

    public SerializationServiceBuilder setEnableSharedObject(boolean var1);

    public SerializationServiceBuilder setAllowUnsafe(boolean var1);

    public SerializationServiceBuilder setPartitioningStrategy(PartitioningStrategy var1);

    public SerializationServiceBuilder setNotActiveExceptionSupplier(Supplier<RuntimeException> var1);

    public SerializationServiceBuilder setInitialOutputBufferSize(int var1);

    public <T extends SerializationService> T build();
}

