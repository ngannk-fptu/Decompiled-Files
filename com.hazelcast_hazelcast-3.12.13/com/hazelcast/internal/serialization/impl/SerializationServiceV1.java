/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.internal.serialization.PortableContext;
import com.hazelcast.internal.serialization.impl.AbstractSerializationService;
import com.hazelcast.internal.serialization.impl.ArrayListStreamSerializer;
import com.hazelcast.internal.serialization.impl.ConstantSerializers;
import com.hazelcast.internal.serialization.impl.DataSerializableSerializer;
import com.hazelcast.internal.serialization.impl.JavaDefaultSerializers;
import com.hazelcast.internal.serialization.impl.LinkedListStreamSerializer;
import com.hazelcast.internal.serialization.impl.PortableContextImpl;
import com.hazelcast.internal.serialization.impl.PortableHookLoader;
import com.hazelcast.internal.serialization.impl.PortableSerializer;
import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.ClassNameFilter;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.DataType;
import com.hazelcast.nio.serialization.FieldDefinition;
import com.hazelcast.nio.serialization.FieldType;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.util.MapUtil;
import java.io.Externalizable;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class SerializationServiceV1
extends AbstractSerializationService {
    private static final int FACTORY_AND_CLASS_ID_BYTE_LENGTH = 8;
    private static final int EE_BYTE_LENGTH = 2;
    private final PortableContextImpl portableContext;
    private final PortableSerializer portableSerializer;

    SerializationServiceV1(AbstractBuilder<?> builder) {
        super(builder);
        PortableHookLoader loader = new PortableHookLoader(((AbstractBuilder)builder).portableFactories, builder.getClassLoader());
        this.portableContext = new PortableContextImpl(this, ((AbstractBuilder)builder).portableVersion);
        for (ClassDefinition cd : loader.getDefinitions()) {
            this.portableContext.registerClassDefinition(cd);
        }
        this.dataSerializerAdapter = SerializationUtil.createSerializerAdapter(new DataSerializableSerializer(((AbstractBuilder)builder).dataSerializableFactories, builder.getClassLoader()), this);
        this.portableSerializer = new PortableSerializer(this.portableContext, loader.getFactories());
        this.portableSerializerAdapter = SerializationUtil.createSerializerAdapter(this.portableSerializer, this);
        this.javaSerializerAdapter = SerializationUtil.createSerializerAdapter(new JavaDefaultSerializers.JavaSerializer(((AbstractBuilder)builder).enableSharedObject, ((AbstractBuilder)builder).enableCompression, ((AbstractBuilder)builder).classNameFilter), this);
        this.javaExternalizableAdapter = SerializationUtil.createSerializerAdapter(new JavaDefaultSerializers.ExternalizableSerializer(((AbstractBuilder)builder).enableCompression, ((AbstractBuilder)builder).classNameFilter), this);
        this.registerConstantSerializers();
        this.registerJavaTypeSerializers();
    }

    @Override
    public <B extends Data> B toData(Object obj, DataType type) {
        if (type == DataType.NATIVE) {
            throw new IllegalArgumentException("Native data type is not supported");
        }
        return this.toData(obj);
    }

    @Override
    public <B extends Data> B toData(Object obj, DataType type, PartitioningStrategy strategy) {
        if (type == DataType.NATIVE) {
            throw new IllegalArgumentException("Native data type is not supported");
        }
        return this.toData(obj, strategy);
    }

    @Override
    public <B extends Data> B convertData(Data data, DataType type) {
        if (type == DataType.NATIVE) {
            throw new IllegalArgumentException("Native data type is not supported");
        }
        return (B)data;
    }

    @Override
    public PortableReader createPortableReader(Data data) throws IOException {
        if (!data.isPortable()) {
            throw new IllegalArgumentException("Given data is not Portable! -> " + data.getType());
        }
        BufferObjectDataInput in = this.createObjectDataInput(data);
        return this.portableSerializer.createReader(in);
    }

    @Override
    public PortableContext getPortableContext() {
        return this.portableContext;
    }

    private void registerConstantSerializers() {
        this.registerConstant(null, this.nullSerializerAdapter);
        this.registerConstant(DataSerializable.class, this.dataSerializerAdapter);
        this.registerConstant(Portable.class, this.portableSerializerAdapter);
        this.registerConstant(Byte.class, new ConstantSerializers.ByteSerializer());
        this.registerConstant(Boolean.class, new ConstantSerializers.BooleanSerializer());
        this.registerConstant(Character.class, new ConstantSerializers.CharSerializer());
        this.registerConstant(Short.class, new ConstantSerializers.ShortSerializer());
        this.registerConstant(Integer.class, new ConstantSerializers.IntegerSerializer());
        this.registerConstant(Long.class, new ConstantSerializers.LongSerializer());
        this.registerConstant(Float.class, new ConstantSerializers.FloatSerializer());
        this.registerConstant(Double.class, new ConstantSerializers.DoubleSerializer());
        this.registerConstant(String.class, new ConstantSerializers.StringSerializer());
        this.registerConstant(byte[].class, new ConstantSerializers.TheByteArraySerializer());
        this.registerConstant(boolean[].class, new ConstantSerializers.BooleanArraySerializer());
        this.registerConstant(char[].class, new ConstantSerializers.CharArraySerializer());
        this.registerConstant(short[].class, new ConstantSerializers.ShortArraySerializer());
        this.registerConstant(int[].class, new ConstantSerializers.IntegerArraySerializer());
        this.registerConstant(long[].class, new ConstantSerializers.LongArraySerializer());
        this.registerConstant(float[].class, new ConstantSerializers.FloatArraySerializer());
        this.registerConstant(double[].class, new ConstantSerializers.DoubleArraySerializer());
        this.registerConstant(String[].class, new ConstantSerializers.StringArraySerializer());
    }

    private void registerJavaTypeSerializers() {
        this.registerConstant(Date.class, new JavaDefaultSerializers.DateSerializer());
        this.registerConstant(BigInteger.class, new JavaDefaultSerializers.BigIntegerSerializer());
        this.registerConstant(BigDecimal.class, new JavaDefaultSerializers.BigDecimalSerializer());
        this.registerConstant(Class.class, new JavaDefaultSerializers.ClassSerializer());
        this.registerConstant(Enum.class, new JavaDefaultSerializers.EnumSerializer());
        this.registerConstant(ArrayList.class, new ArrayListStreamSerializer());
        this.registerConstant(LinkedList.class, new LinkedListStreamSerializer());
        this.safeRegister(Serializable.class, this.javaSerializerAdapter);
        this.safeRegister(Externalizable.class, this.javaExternalizableAdapter);
        this.safeRegister(HazelcastJsonValue.class, new JavaDefaultSerializers.HazelcastJsonValueSerializer());
    }

    public void registerClassDefinitions(Collection<ClassDefinition> classDefinitions, boolean checkClassDefErrors) {
        Map<Integer, Map<Integer, ClassDefinition>> factoryMap = MapUtil.createHashMap(classDefinitions.size());
        for (ClassDefinition cd : classDefinitions) {
            int classId;
            int factoryId = cd.getFactoryId();
            HashMap<Integer, ClassDefinition> classDefMap = (HashMap<Integer, ClassDefinition>)factoryMap.get(factoryId);
            if (classDefMap == null) {
                classDefMap = new HashMap<Integer, ClassDefinition>();
                factoryMap.put(factoryId, classDefMap);
            }
            if (classDefMap.containsKey(classId = cd.getClassId())) {
                throw new HazelcastSerializationException("Duplicate registration found for factory-id : " + factoryId + ", class-id " + classId);
            }
            classDefMap.put(classId, cd);
        }
        for (ClassDefinition classDefinition : classDefinitions) {
            this.registerClassDefinition(classDefinition, factoryMap, checkClassDefErrors);
        }
    }

    private void registerClassDefinition(ClassDefinition cd, Map<Integer, Map<Integer, ClassDefinition>> factoryMap, boolean checkClassDefErrors) {
        Set<String> fieldNames = cd.getFieldNames();
        for (String fieldName : fieldNames) {
            ClassDefinition nestedCd;
            FieldDefinition fd = cd.getField(fieldName);
            if (fd.getType() != FieldType.PORTABLE && fd.getType() != FieldType.PORTABLE_ARRAY) continue;
            int factoryId = fd.getFactoryId();
            int classId = fd.getClassId();
            Map<Integer, ClassDefinition> classDefinitionMap = factoryMap.get(factoryId);
            if (classDefinitionMap != null && (nestedCd = classDefinitionMap.get(classId)) != null) {
                this.registerClassDefinition(nestedCd, factoryMap, checkClassDefErrors);
                this.portableContext.registerClassDefinition(nestedCd);
                continue;
            }
            if (!checkClassDefErrors) continue;
            throw new HazelcastSerializationException("Could not find registered ClassDefinition for factory-id : " + factoryId + ", class-id " + classId);
        }
        this.portableContext.registerClassDefinition(cd);
    }

    final PortableSerializer getPortableSerializer() {
        return this.portableSerializer;
    }

    public ObjectDataInput initDataSerializableInputAndSkipTheHeader(Data data) throws IOException {
        BufferObjectDataInput input = this.createObjectDataInput(data);
        byte header = input.readByte();
        if (DataSerializableSerializer.isFlagSet(header, (byte)1)) {
            this.skipBytesSafely(input, 8);
        } else {
            input.readUTF();
        }
        if (DataSerializableSerializer.isFlagSet(header, (byte)2)) {
            this.skipBytesSafely(input, 2);
        }
        return input;
    }

    public static Builder builder() {
        return new Builder();
    }

    private void skipBytesSafely(ObjectDataInput input, int count) throws IOException {
        if (input.skipBytes(count) != count) {
            throw new HazelcastSerializationException("Malformed serialization format");
        }
    }

    public static final class Builder
    extends AbstractBuilder<Builder> {
        protected Builder() {
        }

        @Override
        protected Builder self() {
            return this;
        }

        public SerializationServiceV1 build() {
            return new SerializationServiceV1(this);
        }
    }

    public static abstract class AbstractBuilder<T extends AbstractBuilder<T>>
    extends AbstractSerializationService.Builder<T> {
        private int portableVersion;
        private Map<Integer, ? extends DataSerializableFactory> dataSerializableFactories = Collections.emptyMap();
        private Map<Integer, ? extends PortableFactory> portableFactories = Collections.emptyMap();
        private boolean enableCompression;
        private boolean enableSharedObject;
        private ClassNameFilter classNameFilter;

        protected AbstractBuilder() {
        }

        public final T withPortableVersion(int portableVersion) {
            this.portableVersion = portableVersion;
            return (T)((AbstractBuilder)this.self());
        }

        public final T withDataSerializableFactories(Map<Integer, ? extends DataSerializableFactory> dataSerializableFactories) {
            this.dataSerializableFactories = dataSerializableFactories;
            return (T)((AbstractBuilder)this.self());
        }

        public Map<Integer, ? extends DataSerializableFactory> getDataSerializableFactories() {
            return this.dataSerializableFactories;
        }

        public final T withPortableFactories(Map<Integer, ? extends PortableFactory> portableFactories) {
            this.portableFactories = portableFactories;
            return (T)((AbstractBuilder)this.self());
        }

        public final T withEnableCompression(boolean enableCompression) {
            this.enableCompression = enableCompression;
            return (T)((AbstractBuilder)this.self());
        }

        public final T withEnableSharedObject(boolean enableSharedObject) {
            this.enableSharedObject = enableSharedObject;
            return (T)((AbstractBuilder)this.self());
        }

        public final T withClassNameFilter(ClassNameFilter classNameFilter) {
            this.classNameFilter = classNameFilter;
            return (T)((AbstractBuilder)this.self());
        }
    }
}

