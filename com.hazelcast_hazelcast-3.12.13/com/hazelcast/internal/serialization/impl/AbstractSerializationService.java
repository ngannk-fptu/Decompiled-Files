/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.ManagedContext;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.internal.serialization.InputOutputFactory;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.ConstantSerializers;
import com.hazelcast.internal.serialization.impl.HeapData;
import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.internal.serialization.impl.SerializerAdapter;
import com.hazelcast.internal.serialization.impl.bufferpool.BufferPool;
import com.hazelcast.internal.serialization.impl.bufferpool.BufferPoolFactory;
import com.hazelcast.internal.serialization.impl.bufferpool.BufferPoolThreadLocal;
import com.hazelcast.internal.usercodedeployment.impl.ClassLocator;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.BufferObjectDataOutput;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.Serializer;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.function.Supplier;
import java.io.Externalizable;
import java.io.Serializable;
import java.nio.ByteOrder;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractSerializationService
implements InternalSerializationService {
    protected final ManagedContext managedContext;
    protected final InputOutputFactory inputOutputFactory;
    protected final PartitioningStrategy globalPartitioningStrategy;
    protected final BufferPoolThreadLocal bufferPoolThreadLocal;
    protected SerializerAdapter dataSerializerAdapter;
    protected SerializerAdapter portableSerializerAdapter;
    protected final SerializerAdapter nullSerializerAdapter;
    protected SerializerAdapter javaSerializerAdapter;
    protected SerializerAdapter javaExternalizableAdapter;
    private final IdentityHashMap<Class, SerializerAdapter> constantTypesMap = new IdentityHashMap(28);
    private final SerializerAdapter[] constantTypeIds = new SerializerAdapter[28];
    private final ConcurrentMap<Class, SerializerAdapter> typeMap = new ConcurrentHashMap<Class, SerializerAdapter>();
    private final ConcurrentMap<Integer, SerializerAdapter> idMap = new ConcurrentHashMap<Integer, SerializerAdapter>();
    private final AtomicReference<SerializerAdapter> global = new AtomicReference();
    private boolean overrideJavaSerialization;
    private final ClassLoader classLoader;
    private final int outputBufferSize;
    private volatile boolean active = true;
    private final byte version;
    private final ILogger logger = Logger.getLogger(InternalSerializationService.class);

    AbstractSerializationService(Builder<?> builder) {
        this.inputOutputFactory = ((Builder)builder).inputOutputFactory;
        this.version = ((Builder)builder).version;
        this.classLoader = ((Builder)builder).classLoader;
        this.managedContext = ((Builder)builder).managedContext;
        this.globalPartitioningStrategy = ((Builder)builder).globalPartitionStrategy;
        this.outputBufferSize = ((Builder)builder).initialOutputBufferSize;
        this.bufferPoolThreadLocal = new BufferPoolThreadLocal(this, ((Builder)builder).bufferPoolFactory, ((Builder)builder).notActiveExceptionSupplier);
        this.nullSerializerAdapter = SerializationUtil.createSerializerAdapter(new ConstantSerializers.NullSerializer(), this);
    }

    @Override
    public final <B extends Data> B toData(Object obj) {
        return this.toData(obj, this.globalPartitioningStrategy);
    }

    @Override
    public final <B extends Data> B toData(Object obj, PartitioningStrategy strategy) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Data) {
            return (B)((Data)obj);
        }
        byte[] bytes = this.toBytes(obj, 0, true, strategy);
        return (B)new HeapData(bytes);
    }

    @Override
    public byte[] toBytes(Object obj) {
        return this.toBytes(obj, 0, true, this.globalPartitioningStrategy);
    }

    @Override
    public byte[] toBytes(Object obj, int leftPadding, boolean insertPartitionHash) {
        return this.toBytes(obj, leftPadding, insertPartitionHash, this.globalPartitioningStrategy, this.getByteOrder());
    }

    private byte[] toBytes(Object obj, int leftPadding, boolean writeHash, PartitioningStrategy strategy) {
        return this.toBytes(obj, leftPadding, writeHash, strategy, ByteOrder.BIG_ENDIAN);
    }

    private byte[] toBytes(Object obj, int leftPadding, boolean writeHash, PartitioningStrategy strategy, ByteOrder serializerTypeIdByteOrder) {
        Preconditions.checkNotNull(obj);
        Preconditions.checkNotNull(serializerTypeIdByteOrder);
        BufferPool pool = this.bufferPoolThreadLocal.get();
        BufferObjectDataOutput out = pool.takeOutputBuffer();
        try {
            out.position(leftPadding);
            SerializerAdapter serializer = this.serializerFor(obj);
            if (writeHash) {
                int partitionHash = this.calculatePartitionHash(obj, strategy);
                out.writeInt(partitionHash, ByteOrder.BIG_ENDIAN);
            }
            out.writeInt(serializer.getTypeId(), serializerTypeIdByteOrder);
            serializer.write(out, obj);
            byte[] byArray = out.toByteArray();
            return byArray;
        }
        catch (Throwable e) {
            throw SerializationUtil.handleSerializeException(obj, e);
        }
        finally {
            pool.returnOutputBuffer(out);
        }
    }

    @Override
    public final <T> T toObject(Object object) {
        if (!(object instanceof Data)) {
            return (T)object;
        }
        Data data = (Data)object;
        if (SerializationUtil.isNullData(data)) {
            return null;
        }
        BufferPool pool = this.bufferPoolThreadLocal.get();
        BufferObjectDataInput in = pool.takeInputBuffer(data);
        try {
            ClassLocator.onStartDeserialization();
            int typeId = data.getType();
            SerializerAdapter serializer = this.serializerFor(typeId);
            if (serializer == null) {
                if (this.active) {
                    throw AbstractSerializationService.newHazelcastSerializationException(typeId);
                }
                throw new HazelcastInstanceNotActiveException();
            }
            Object obj = serializer.read(in);
            if (this.managedContext != null) {
                obj = this.managedContext.initialize(obj);
            }
            Object object2 = obj;
            return (T)object2;
        }
        catch (Throwable e) {
            throw SerializationUtil.handleException(e);
        }
        finally {
            ClassLocator.onFinishDeserialization();
            pool.returnInputBuffer(in);
        }
    }

    @Override
    public final <T> T toObject(Object object, Class aClass) {
        if (!(object instanceof Data)) {
            return (T)object;
        }
        Data data = (Data)object;
        if (SerializationUtil.isNullData(data)) {
            return null;
        }
        BufferPool pool = this.bufferPoolThreadLocal.get();
        BufferObjectDataInput in = pool.takeInputBuffer(data);
        try {
            ClassLocator.onStartDeserialization();
            int typeId = data.getType();
            SerializerAdapter serializer = this.serializerFor(typeId);
            if (serializer == null) {
                if (this.active) {
                    throw AbstractSerializationService.newHazelcastSerializationException(typeId);
                }
                throw new HazelcastInstanceNotActiveException();
            }
            Object obj = serializer.read(in, aClass);
            if (this.managedContext != null) {
                obj = this.managedContext.initialize(obj);
            }
            Object object2 = obj;
            return (T)object2;
        }
        catch (Throwable e) {
            throw SerializationUtil.handleException(e);
        }
        finally {
            ClassLocator.onFinishDeserialization();
            pool.returnInputBuffer(in);
        }
    }

    private static HazelcastSerializationException newHazelcastSerializationException(int typeId) {
        return new HazelcastSerializationException("There is no suitable de-serializer for type " + typeId + ". This exception is likely to be caused by differences in the serialization configuration between members or between clients and members.");
    }

    @Override
    public final void writeObject(ObjectDataOutput out, Object obj) {
        if (obj instanceof Data) {
            throw new HazelcastSerializationException("Cannot write a Data instance! Use #writeData(ObjectDataOutput out, Data data) instead.");
        }
        try {
            SerializerAdapter serializer = this.serializerFor(obj);
            out.writeInt(serializer.getTypeId());
            serializer.write(out, obj);
        }
        catch (Throwable e) {
            throw SerializationUtil.handleSerializeException(obj, e);
        }
    }

    @Override
    public final <T> T readObject(ObjectDataInput in) {
        try {
            int typeId = in.readInt();
            SerializerAdapter serializer = this.serializerFor(typeId);
            if (serializer == null) {
                if (this.active) {
                    throw AbstractSerializationService.newHazelcastSerializationException(typeId);
                }
                throw new HazelcastInstanceNotActiveException();
            }
            Object obj = serializer.read(in);
            if (this.managedContext != null) {
                obj = this.managedContext.initialize(obj);
            }
            return (T)obj;
        }
        catch (Throwable e) {
            throw SerializationUtil.handleException(e);
        }
    }

    @Override
    public final <T> T readObject(ObjectDataInput in, Class aClass) {
        try {
            int typeId = in.readInt();
            SerializerAdapter serializer = this.serializerFor(typeId);
            if (serializer == null) {
                if (this.active) {
                    throw AbstractSerializationService.newHazelcastSerializationException(typeId);
                }
                throw new HazelcastInstanceNotActiveException();
            }
            Object obj = serializer.read(in, aClass);
            if (this.managedContext != null) {
                obj = this.managedContext.initialize(obj);
            }
            return (T)obj;
        }
        catch (Throwable e) {
            throw SerializationUtil.handleException(e);
        }
    }

    @Override
    public void disposeData(Data data) {
    }

    @Override
    public final BufferObjectDataInput createObjectDataInput(byte[] data) {
        return this.inputOutputFactory.createInput(data, (InternalSerializationService)this);
    }

    @Override
    public final BufferObjectDataInput createObjectDataInput(Data data) {
        return this.inputOutputFactory.createInput(data, (InternalSerializationService)this);
    }

    @Override
    public final BufferObjectDataOutput createObjectDataOutput(int size) {
        return this.inputOutputFactory.createOutput(size, this);
    }

    @Override
    public BufferObjectDataOutput createObjectDataOutput() {
        return this.inputOutputFactory.createOutput(this.outputBufferSize, this);
    }

    @Override
    public final ClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override
    public final ManagedContext getManagedContext() {
        return this.managedContext;
    }

    @Override
    public ByteOrder getByteOrder() {
        return this.inputOutputFactory.getByteOrder();
    }

    @Override
    public byte getVersion() {
        return this.version;
    }

    @Override
    public void dispose() {
        this.active = false;
        for (SerializerAdapter serializer : this.typeMap.values()) {
            serializer.destroy();
        }
        for (SerializerAdapter serializer : this.constantTypesMap.values()) {
            serializer.destroy();
        }
        this.typeMap.clear();
        this.idMap.clear();
        this.global.set(null);
        this.constantTypesMap.clear();
        this.bufferPoolThreadLocal.clear();
    }

    public final void register(Class type, Serializer serializer) {
        if (type == null) {
            throw new IllegalArgumentException("Class type information is required!");
        }
        if (serializer.getTypeId() <= 0) {
            throw new IllegalArgumentException("Type ID must be positive! Current: " + serializer.getTypeId() + ", Serializer: " + serializer);
        }
        this.safeRegister(type, SerializationUtil.createSerializerAdapter(serializer, this));
    }

    public final void registerGlobal(Serializer serializer) {
        this.registerGlobal(serializer, false);
    }

    public final void registerGlobal(Serializer serializer, boolean overrideJavaSerialization) {
        SerializerAdapter adapter = SerializationUtil.createSerializerAdapter(serializer, this);
        if (!this.global.compareAndSet(null, adapter)) {
            throw new IllegalStateException("Global serializer is already registered!");
        }
        this.overrideJavaSerialization = overrideJavaSerialization;
        SerializerAdapter current = this.idMap.putIfAbsent(serializer.getTypeId(), adapter);
        if (current != null && current.getImpl().getClass() != adapter.getImpl().getClass()) {
            this.global.compareAndSet(adapter, null);
            this.overrideJavaSerialization = false;
            throw new IllegalStateException("Serializer [" + current.getImpl() + "] has been already registered for type-id: " + serializer.getTypeId());
        }
    }

    protected final int calculatePartitionHash(Object obj, PartitioningStrategy strategy) {
        Object pk;
        PartitioningStrategy partitioningStrategy;
        int partitionHash = 0;
        PartitioningStrategy partitioningStrategy2 = partitioningStrategy = strategy == null ? this.globalPartitioningStrategy : strategy;
        if (partitioningStrategy != null && (pk = partitioningStrategy.getPartitionKey(obj)) != null && pk != obj) {
            Object partitionKey = this.toData(pk, SerializationUtil.EMPTY_PARTITIONING_STRATEGY);
            partitionHash = partitionKey == null ? 0 : partitionKey.getPartitionHash();
        }
        return partitionHash;
    }

    protected final boolean safeRegister(Class type, Serializer serializer) {
        return this.safeRegister(type, SerializationUtil.createSerializerAdapter(serializer, this));
    }

    protected final boolean safeRegister(Class type, SerializerAdapter serializer) {
        if (this.constantTypesMap.containsKey(type)) {
            throw new IllegalArgumentException("[" + type + "] serializer cannot be overridden!");
        }
        SerializerAdapter current = this.typeMap.putIfAbsent(type, serializer);
        if (current != null && current.getImpl().getClass() != serializer.getImpl().getClass()) {
            throw new IllegalStateException("Serializer[" + current.getImpl() + "] has been already registered for type: " + type);
        }
        current = this.idMap.putIfAbsent(serializer.getTypeId(), serializer);
        if (current != null && current.getImpl().getClass() != serializer.getImpl().getClass()) {
            throw new IllegalStateException("Serializer [" + current.getImpl() + "] has been already registered for type-id: " + serializer.getTypeId());
        }
        return current == null;
    }

    protected final void registerConstant(Class type, Serializer serializer) {
        this.registerConstant(type, SerializationUtil.createSerializerAdapter(serializer, this));
    }

    protected final void registerConstant(Class type, SerializerAdapter serializer) {
        this.constantTypesMap.put(type, serializer);
        this.constantTypeIds[SerializationUtil.indexForDefaultType((int)serializer.getTypeId())] = serializer;
    }

    private SerializerAdapter registerFromSuperType(Class type, Class superType) {
        SerializerAdapter serializer = (SerializerAdapter)this.typeMap.get(superType);
        if (serializer != null) {
            this.safeRegister(type, serializer);
        }
        return serializer;
    }

    protected final SerializerAdapter serializerFor(int typeId) {
        int index;
        if (typeId <= 0 && (index = SerializationUtil.indexForDefaultType(typeId)) < 28) {
            return this.constantTypeIds[index];
        }
        return (SerializerAdapter)this.idMap.get(typeId);
    }

    protected final SerializerAdapter serializerFor(Object object) {
        if (object == null) {
            return this.nullSerializerAdapter;
        }
        Class<?> type = object.getClass();
        SerializerAdapter serializer = this.lookupDefaultSerializer(type);
        if (serializer == null) {
            serializer = this.lookupCustomSerializer(type);
        }
        if (serializer == null && !this.overrideJavaSerialization) {
            serializer = this.lookupJavaSerializer(type);
        }
        if (serializer == null) {
            serializer = this.lookupGlobalSerializer(type);
        }
        if (serializer == null) {
            if (this.active) {
                throw new HazelcastSerializationException("There is no suitable serializer for " + type);
            }
            throw new HazelcastInstanceNotActiveException();
        }
        return serializer;
    }

    private SerializerAdapter lookupDefaultSerializer(Class type) {
        if (DataSerializable.class.isAssignableFrom(type)) {
            return this.dataSerializerAdapter;
        }
        if (Portable.class.isAssignableFrom(type)) {
            return this.portableSerializerAdapter;
        }
        return this.constantTypesMap.get(type);
    }

    private SerializerAdapter lookupCustomSerializer(Class type) {
        SerializerAdapter serializer;
        block3: {
            Class typeInterface;
            serializer = (SerializerAdapter)this.typeMap.get(type);
            if (serializer != null) {
                return serializer;
            }
            LinkedHashSet<Class> interfaces = new LinkedHashSet<Class>(5);
            SerializationUtil.getInterfaces(type, interfaces);
            for (Class typeSuperclass = type.getSuperclass(); typeSuperclass != null && (serializer = this.registerFromSuperType(type, typeSuperclass)) == null; typeSuperclass = typeSuperclass.getSuperclass()) {
                SerializationUtil.getInterfaces(typeSuperclass, interfaces);
            }
            if (serializer != null) break block3;
            interfaces.remove(Serializable.class);
            interfaces.remove(Externalizable.class);
            Iterator iterator = interfaces.iterator();
            while (iterator.hasNext() && (serializer = this.registerFromSuperType(type, typeInterface = (Class)iterator.next())) == null) {
            }
        }
        return serializer;
    }

    private SerializerAdapter lookupGlobalSerializer(Class type) {
        SerializerAdapter serializer = this.global.get();
        if (serializer != null) {
            this.logger.fine("Registering global serializer for: " + type.getName());
            this.safeRegister(type, serializer);
        }
        return serializer;
    }

    private SerializerAdapter lookupJavaSerializer(Class type) {
        if (Externalizable.class.isAssignableFrom(type)) {
            if (this.safeRegister(type, this.javaExternalizableAdapter) && !Throwable.class.isAssignableFrom(type)) {
                this.logger.info("Performance Hint: Serialization service will use java.io.Externalizable for: " + type.getName() + ". Please consider using a faster serialization option such as DataSerializable.");
            }
            return this.javaExternalizableAdapter;
        }
        if (Serializable.class.isAssignableFrom(type)) {
            if (this.safeRegister(type, this.javaSerializerAdapter) && !Throwable.class.isAssignableFrom(type)) {
                this.logger.info("Performance Hint: Serialization service will use java.io.Serializable for: " + type.getName() + ". Please consider using a faster serialization option such as DataSerializable.");
            }
            return this.javaSerializerAdapter;
        }
        return null;
    }

    public static abstract class Builder<T extends Builder<T>> {
        private InputOutputFactory inputOutputFactory;
        private byte version;
        private ClassLoader classLoader;
        private ManagedContext managedContext;
        private PartitioningStrategy globalPartitionStrategy;
        private int initialOutputBufferSize;
        private BufferPoolFactory bufferPoolFactory;
        private Supplier<RuntimeException> notActiveExceptionSupplier;

        protected Builder() {
        }

        protected abstract T self();

        public final T withInputOutputFactory(InputOutputFactory inputOutputFactory) {
            this.inputOutputFactory = inputOutputFactory;
            return this.self();
        }

        public final T withVersion(byte version) {
            this.version = version;
            return this.self();
        }

        public final T withClassLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this.self();
        }

        public ClassLoader getClassLoader() {
            return this.classLoader;
        }

        public final T withManagedContext(ManagedContext managedContext) {
            this.managedContext = managedContext;
            return this.self();
        }

        public final T withGlobalPartitionStrategy(PartitioningStrategy globalPartitionStrategy) {
            this.globalPartitionStrategy = globalPartitionStrategy;
            return this.self();
        }

        public final T withInitialOutputBufferSize(int initialOutputBufferSize) {
            this.initialOutputBufferSize = initialOutputBufferSize;
            return this.self();
        }

        public final T withBufferPoolFactory(BufferPoolFactory bufferPoolFactory) {
            this.bufferPoolFactory = bufferPoolFactory;
            return this.self();
        }

        public final T withNotActiveExceptionSupplier(Supplier<RuntimeException> notActiveExceptionSupplier) {
            this.notActiveExceptionSupplier = notActiveExceptionSupplier;
            return this.self();
        }
    }
}

