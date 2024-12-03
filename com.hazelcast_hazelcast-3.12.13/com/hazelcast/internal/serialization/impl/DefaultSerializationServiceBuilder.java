/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.config.GlobalSerializerConfig;
import com.hazelcast.config.JavaSerializationFilterConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.ManagedContext;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.internal.memory.GlobalMemoryAccessorRegistry;
import com.hazelcast.internal.serialization.InputOutputFactory;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.SerializationServiceBuilder;
import com.hazelcast.internal.serialization.impl.AbstractSerializationService;
import com.hazelcast.internal.serialization.impl.ByteArrayInputOutputFactory;
import com.hazelcast.internal.serialization.impl.SerializationServiceV1;
import com.hazelcast.internal.serialization.impl.SerializerHookLoader;
import com.hazelcast.internal.serialization.impl.UnsafeInputOutputFactory;
import com.hazelcast.internal.serialization.impl.bufferpool.BufferPoolFactoryImpl;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.ClassNameFilter;
import com.hazelcast.nio.SerializationClassNameFilter;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.nio.serialization.Serializer;
import com.hazelcast.nio.serialization.SerializerHook;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.util.StringUtil;
import com.hazelcast.util.function.Supplier;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DefaultSerializationServiceBuilder
implements SerializationServiceBuilder {
    static final ByteOrder DEFAULT_BYTE_ORDER = ByteOrder.BIG_ENDIAN;
    private static final String BYTE_ORDER_OVERRIDE_PROPERTY = "hazelcast.serialization.byteOrder";
    private static final int DEFAULT_OUT_BUFFER_SIZE = 4096;
    protected final Map<Integer, DataSerializableFactory> dataSerializableFactories = new HashMap<Integer, DataSerializableFactory>();
    protected final Map<Integer, PortableFactory> portableFactories = new HashMap<Integer, PortableFactory>();
    protected final Set<ClassDefinition> classDefinitions = new HashSet<ClassDefinition>();
    protected ClassLoader classLoader;
    protected SerializationConfig config;
    protected byte version = (byte)-1;
    protected int portableVersion = -1;
    protected boolean checkClassDefErrors = true;
    protected ManagedContext managedContext;
    protected boolean useNativeByteOrder;
    protected ByteOrder byteOrder = DEFAULT_BYTE_ORDER;
    protected boolean enableCompression;
    protected boolean enableSharedObject;
    protected boolean allowUnsafe;
    protected int initialOutputBufferSize = 4096;
    protected PartitioningStrategy partitioningStrategy;
    protected HazelcastInstance hazelcastInstance;
    protected Supplier<RuntimeException> notActiveExceptionSupplier;
    protected ClassNameFilter classNameFilter;

    @Override
    public SerializationServiceBuilder setVersion(byte version) {
        byte maxVersion = BuildInfoProvider.getBuildInfo().getSerializationVersion();
        if (version > maxVersion) {
            throw new IllegalArgumentException("Configured serialization version is higher than the max supported version: " + maxVersion);
        }
        this.version = version;
        return this;
    }

    @Override
    public SerializationServiceBuilder setPortableVersion(int portableVersion) {
        if (portableVersion < 0) {
            throw new IllegalArgumentException("Portable Version cannot be negative!");
        }
        this.portableVersion = portableVersion;
        return this;
    }

    @Override
    public SerializationServiceBuilder setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    @Override
    public SerializationServiceBuilder setConfig(SerializationConfig config) {
        this.config = config;
        if (this.portableVersion < 0) {
            this.portableVersion = config.getPortableVersion();
        }
        this.checkClassDefErrors = config.isCheckClassDefErrors();
        this.useNativeByteOrder = config.isUseNativeByteOrder();
        this.byteOrder = config.getByteOrder();
        this.enableCompression = config.isEnableCompression();
        this.enableSharedObject = config.isEnableSharedObject();
        this.allowUnsafe = config.isAllowUnsafe();
        JavaSerializationFilterConfig filterConfig = config.getJavaSerializationFilterConfig();
        this.classNameFilter = filterConfig == null ? null : new SerializationClassNameFilter(filterConfig);
        return this;
    }

    @Override
    public SerializationServiceBuilder addDataSerializableFactory(int id, DataSerializableFactory factory) {
        this.dataSerializableFactories.put(id, factory);
        return this;
    }

    @Override
    public SerializationServiceBuilder addPortableFactory(int id, PortableFactory factory) {
        this.portableFactories.put(id, factory);
        return this;
    }

    @Override
    public SerializationServiceBuilder addClassDefinition(ClassDefinition cd) {
        this.classDefinitions.add(cd);
        return this;
    }

    @Override
    public SerializationServiceBuilder setCheckClassDefErrors(boolean checkClassDefErrors) {
        this.checkClassDefErrors = checkClassDefErrors;
        return this;
    }

    @Override
    public SerializationServiceBuilder setManagedContext(ManagedContext managedContext) {
        this.managedContext = managedContext;
        return this;
    }

    @Override
    public SerializationServiceBuilder setUseNativeByteOrder(boolean useNativeByteOrder) {
        this.useNativeByteOrder = useNativeByteOrder;
        return this;
    }

    @Override
    public SerializationServiceBuilder setByteOrder(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
        return this;
    }

    @Override
    public SerializationServiceBuilder setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
        return this;
    }

    @Override
    public SerializationServiceBuilder setEnableCompression(boolean enableCompression) {
        this.enableCompression = enableCompression;
        return this;
    }

    @Override
    public SerializationServiceBuilder setEnableSharedObject(boolean enableSharedObject) {
        this.enableSharedObject = enableSharedObject;
        return this;
    }

    @Override
    public SerializationServiceBuilder setAllowUnsafe(boolean allowUnsafe) {
        this.allowUnsafe = allowUnsafe;
        return this;
    }

    @Override
    public SerializationServiceBuilder setPartitioningStrategy(PartitioningStrategy partitionStrategy) {
        this.partitioningStrategy = partitionStrategy;
        return this;
    }

    @Override
    public SerializationServiceBuilder setNotActiveExceptionSupplier(Supplier<RuntimeException> notActiveExceptionSupplier) {
        this.notActiveExceptionSupplier = notActiveExceptionSupplier;
        return this;
    }

    @Override
    public SerializationServiceBuilder setInitialOutputBufferSize(int initialOutputBufferSize) {
        if (initialOutputBufferSize <= 0) {
            throw new IllegalArgumentException("Initial buffer size must be positive!");
        }
        this.initialOutputBufferSize = initialOutputBufferSize;
        return this;
    }

    public InternalSerializationService build() {
        this.initVersions();
        if (this.config != null) {
            this.addConfigDataSerializableFactories(this.dataSerializableFactories, this.config, this.classLoader);
            this.addConfigPortableFactories(this.portableFactories, this.config, this.classLoader);
            this.classDefinitions.addAll(this.config.getClassDefinitions());
        }
        InputOutputFactory inputOutputFactory = this.createInputOutputFactory();
        InternalSerializationService ss = this.createSerializationService(inputOutputFactory, this.notActiveExceptionSupplier);
        this.registerSerializerHooks(ss);
        if (this.config != null && this.config.getGlobalSerializerConfig() != null) {
            GlobalSerializerConfig globalSerializerConfig = this.config.getGlobalSerializerConfig();
            Serializer serializer = globalSerializerConfig.getImplementation();
            if (serializer == null) {
                try {
                    serializer = (Serializer)ClassLoaderUtil.newInstance(this.classLoader, globalSerializerConfig.getClassName());
                }
                catch (Exception e) {
                    throw new HazelcastSerializationException(e);
                }
            }
            if (serializer instanceof HazelcastInstanceAware) {
                ((HazelcastInstanceAware)((Object)serializer)).setHazelcastInstance(this.hazelcastInstance);
            }
            ((AbstractSerializationService)ss).registerGlobal(serializer, globalSerializerConfig.isOverrideJavaSerialization());
        }
        return ss;
    }

    private void initVersions() {
        if (this.version < 0) {
            byte maxVersion;
            String defaultVal = GroupProperty.SERIALIZATION_VERSION.getDefaultValue();
            byte versionCandidate = Byte.parseByte(System.getProperty(GroupProperty.SERIALIZATION_VERSION.getName(), defaultVal));
            if (versionCandidate > (maxVersion = Byte.parseByte(defaultVal))) {
                throw new IllegalArgumentException("Configured serialization version is higher than the max supported version: " + maxVersion);
            }
            this.version = versionCandidate;
        }
        if (this.portableVersion < 0) {
            this.portableVersion = 0;
        }
    }

    protected InternalSerializationService createSerializationService(InputOutputFactory inputOutputFactory, Supplier<RuntimeException> notActiveExceptionSupplier) {
        switch (this.version) {
            case 1: {
                SerializationServiceV1 serializationServiceV1 = ((SerializationServiceV1.Builder)((SerializationServiceV1.Builder)((SerializationServiceV1.Builder)((SerializationServiceV1.Builder)((SerializationServiceV1.Builder)((SerializationServiceV1.Builder)((SerializationServiceV1.Builder)((SerializationServiceV1.Builder)((SerializationServiceV1.Builder)((SerializationServiceV1.Builder)((SerializationServiceV1.Builder)((SerializationServiceV1.Builder)((SerializationServiceV1.Builder)((SerializationServiceV1.Builder)SerializationServiceV1.builder().withInputOutputFactory(inputOutputFactory)).withVersion(this.version)).withPortableVersion(this.portableVersion)).withClassLoader(this.classLoader)).withDataSerializableFactories(this.dataSerializableFactories)).withPortableFactories(this.portableFactories)).withManagedContext(this.managedContext)).withGlobalPartitionStrategy(this.partitioningStrategy)).withInitialOutputBufferSize(this.initialOutputBufferSize)).withBufferPoolFactory(new BufferPoolFactoryImpl())).withEnableCompression(this.enableCompression)).withEnableSharedObject(this.enableSharedObject)).withNotActiveExceptionSupplier(notActiveExceptionSupplier)).withClassNameFilter(this.classNameFilter)).build();
                serializationServiceV1.registerClassDefinitions(this.classDefinitions, this.checkClassDefErrors);
                return serializationServiceV1;
            }
        }
        throw new IllegalArgumentException("Serialization version is not supported!");
    }

    private void registerSerializerHooks(InternalSerializationService ss) {
        SerializerHookLoader serializerHookLoader = new SerializerHookLoader(this.config, this.classLoader);
        Map<Class, Object> serializers = serializerHookLoader.getSerializers();
        for (Map.Entry<Class, Object> entry : serializers.entrySet()) {
            Class serializationType = entry.getKey();
            Object value = entry.getValue();
            Serializer serializer = value instanceof SerializerHook ? ((SerializerHook)value).createSerializer() : (Serializer)value;
            if (value instanceof HazelcastInstanceAware) {
                ((HazelcastInstanceAware)value).setHazelcastInstance(this.hazelcastInstance);
            }
            if (ClassLoaderUtil.isInternalType(value.getClass())) {
                ((AbstractSerializationService)ss).safeRegister(serializationType, serializer);
                continue;
            }
            ((AbstractSerializationService)ss).register(serializationType, serializer);
        }
    }

    protected InputOutputFactory createInputOutputFactory() {
        this.overrideByteOrder();
        if (this.byteOrder == null) {
            this.byteOrder = DEFAULT_BYTE_ORDER;
        }
        if (this.useNativeByteOrder) {
            this.byteOrder = ByteOrder.nativeOrder();
        }
        return this.byteOrder == ByteOrder.nativeOrder() && this.allowUnsafe && GlobalMemoryAccessorRegistry.MEM_AVAILABLE ? new UnsafeInputOutputFactory() : new ByteArrayInputOutputFactory(this.byteOrder);
    }

    protected void overrideByteOrder() {
        String byteOrderOverride = System.getProperty(BYTE_ORDER_OVERRIDE_PROPERTY);
        if (StringUtil.isNullOrEmpty(byteOrderOverride)) {
            return;
        }
        if (ByteOrder.BIG_ENDIAN.toString().equals(byteOrderOverride)) {
            this.byteOrder = ByteOrder.BIG_ENDIAN;
        } else if (ByteOrder.LITTLE_ENDIAN.toString().equals(byteOrderOverride)) {
            this.byteOrder = ByteOrder.LITTLE_ENDIAN;
        }
    }

    private void addConfigDataSerializableFactories(Map<Integer, DataSerializableFactory> dataSerializableFactories, SerializationConfig config, ClassLoader cl) {
        this.registerDataSerializableFactories(dataSerializableFactories, config);
        this.buildDataSerializableFactories(dataSerializableFactories, config, cl);
        for (DataSerializableFactory f : dataSerializableFactories.values()) {
            if (!(f instanceof HazelcastInstanceAware)) continue;
            ((HazelcastInstanceAware)((Object)f)).setHazelcastInstance(this.hazelcastInstance);
        }
    }

    private void registerDataSerializableFactories(Map<Integer, DataSerializableFactory> dataSerializableFactories, SerializationConfig config) {
        for (Map.Entry<Integer, DataSerializableFactory> entry : config.getDataSerializableFactories().entrySet()) {
            int factoryId = entry.getKey();
            DataSerializableFactory factory = entry.getValue();
            if (factoryId <= 0) {
                throw new IllegalArgumentException("DataSerializableFactory factoryId must be positive! -> " + factory);
            }
            if (dataSerializableFactories.containsKey(factoryId)) {
                throw new IllegalArgumentException("DataSerializableFactory with factoryId '" + factoryId + "' is already registered!");
            }
            dataSerializableFactories.put(factoryId, factory);
        }
    }

    private void buildDataSerializableFactories(Map<Integer, DataSerializableFactory> dataSerializableFactories, SerializationConfig config, ClassLoader cl) {
        for (Map.Entry<Integer, String> entry : config.getDataSerializableFactoryClasses().entrySet()) {
            DataSerializableFactory factory;
            int factoryId = entry.getKey();
            String factoryClassName = entry.getValue();
            if (factoryId <= 0) {
                throw new IllegalArgumentException("DataSerializableFactory factoryId must be positive! -> " + factoryClassName);
            }
            if (dataSerializableFactories.containsKey(factoryId)) {
                throw new IllegalArgumentException("DataSerializableFactory with factoryId '" + factoryId + "' is already registered!");
            }
            try {
                factory = (DataSerializableFactory)ClassLoaderUtil.newInstance(cl, factoryClassName);
            }
            catch (Exception e) {
                throw new HazelcastSerializationException(e);
            }
            dataSerializableFactories.put(factoryId, factory);
        }
    }

    private void addConfigPortableFactories(Map<Integer, PortableFactory> portableFactories, SerializationConfig config, ClassLoader cl) {
        this.registerPortableFactories(portableFactories, config);
        this.buildPortableFactories(portableFactories, config, cl);
        for (PortableFactory f : portableFactories.values()) {
            if (!(f instanceof HazelcastInstanceAware)) continue;
            ((HazelcastInstanceAware)((Object)f)).setHazelcastInstance(this.hazelcastInstance);
        }
    }

    private void registerPortableFactories(Map<Integer, PortableFactory> portableFactories, SerializationConfig config) {
        for (Map.Entry<Integer, PortableFactory> entry : config.getPortableFactories().entrySet()) {
            int factoryId = entry.getKey();
            PortableFactory factory = entry.getValue();
            if (factoryId <= 0) {
                throw new IllegalArgumentException("PortableFactory factoryId must be positive! -> " + factory);
            }
            if (portableFactories.containsKey(factoryId)) {
                throw new IllegalArgumentException("PortableFactory with factoryId '" + factoryId + "' is already registered!");
            }
            portableFactories.put(factoryId, factory);
        }
    }

    private void buildPortableFactories(Map<Integer, PortableFactory> portableFactories, SerializationConfig config, ClassLoader cl) {
        Map<Integer, String> portableFactoryClasses = config.getPortableFactoryClasses();
        for (Map.Entry<Integer, String> entry : portableFactoryClasses.entrySet()) {
            PortableFactory factory;
            int factoryId = entry.getKey();
            String factoryClassName = entry.getValue();
            if (factoryId <= 0) {
                throw new IllegalArgumentException("PortableFactory factoryId must be positive! -> " + factoryClassName);
            }
            if (portableFactories.containsKey(factoryId)) {
                throw new IllegalArgumentException("PortableFactory with factoryId '" + factoryId + "' is already registered!");
            }
            try {
                factory = (PortableFactory)ClassLoaderUtil.newInstance(cl, factoryClassName);
            }
            catch (Exception e) {
                throw new HazelcastSerializationException(e);
            }
            portableFactories.put(factoryId, factory);
        }
    }
}

