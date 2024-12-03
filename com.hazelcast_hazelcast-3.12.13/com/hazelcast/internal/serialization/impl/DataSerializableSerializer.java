/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.VersionedObjectDataInput;
import com.hazelcast.internal.serialization.impl.VersionedObjectDataOutput;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.StreamSerializer;
import com.hazelcast.nio.serialization.TypedDataSerializable;
import com.hazelcast.nio.serialization.TypedStreamDeserializer;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.ServiceLoader;
import com.hazelcast.util.collection.Int2ObjectHashMap;
import com.hazelcast.version.Version;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;

final class DataSerializableSerializer
implements StreamSerializer<DataSerializable>,
TypedStreamDeserializer<DataSerializable> {
    public static final byte IDS_FLAG = 1;
    public static final byte EE_FLAG = 2;
    private static final String FACTORY_ID = "com.hazelcast.DataSerializerHook";
    private final Version version = Version.of(BuildInfoProvider.getBuildInfo().getVersion());
    private final Int2ObjectHashMap<DataSerializableFactory> factories = new Int2ObjectHashMap();

    DataSerializableSerializer(Map<Integer, ? extends DataSerializableFactory> dataSerializableFactories, ClassLoader classLoader) {
        try {
            Iterator<DataSerializerHook> hooks = ServiceLoader.iterator(DataSerializerHook.class, FACTORY_ID, classLoader);
            while (hooks.hasNext()) {
                DataSerializerHook hook = hooks.next();
                DataSerializableFactory factory = hook.createFactory();
                if (factory == null) continue;
                this.register(hook.getFactoryId(), factory);
            }
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
        if (dataSerializableFactories != null) {
            for (Map.Entry<Integer, ? extends DataSerializableFactory> entry : dataSerializableFactories.entrySet()) {
                this.register(entry.getKey(), entry.getValue());
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void register(int factoryId, DataSerializableFactory factory) {
        DataSerializableFactory current = this.factories.get(factoryId);
        if (current != null) {
            if (!current.equals(factory)) throw new IllegalArgumentException("DataSerializableFactory[" + factoryId + "] is already registered! " + current + " -> " + factory);
            Logger.getLogger(this.getClass()).warning("DataSerializableFactory[" + factoryId + "] is already registered! Skipping " + factory);
            return;
        } else {
            this.factories.put(factoryId, factory);
        }
    }

    @Override
    public int getTypeId() {
        return -2;
    }

    @Override
    public DataSerializable read(ObjectDataInput in) throws IOException {
        return this.readInternal(in, null);
    }

    @Override
    public DataSerializable read(ObjectDataInput in, Class aClass) throws IOException {
        return this.readInternal(in, aClass);
    }

    private DataSerializable readInternal(ObjectDataInput in, Class aClass) throws IOException {
        DataSerializableSerializer.setInputVersion(in, this.version);
        DataSerializable ds = null;
        if (null != aClass) {
            try {
                ds = (DataSerializable)aClass.newInstance();
            }
            catch (Exception e) {
                e = this.tryClarifyInstantiationException(aClass, e);
                throw new HazelcastSerializationException("Requested class " + aClass + " could not be instantiated.", e);
            }
        }
        byte header = in.readByte();
        int id = 0;
        int factoryId = 0;
        String className = null;
        try {
            if (DataSerializableSerializer.isFlagSet(header, (byte)1)) {
                factoryId = in.readInt();
                DataSerializableFactory dsf = this.factories.get(factoryId);
                if (dsf == null) {
                    throw new HazelcastSerializationException("No DataSerializerFactory registered for namespace: " + factoryId);
                }
                id = in.readInt();
                if (null == aClass && (ds = dsf.create(id)) == null) {
                    throw new HazelcastSerializationException(dsf + " is not be able to create an instance for ID: " + id + " on factory ID: " + factoryId);
                }
            } else {
                className = in.readUTF();
                if (null == aClass) {
                    ds = (DataSerializable)ClassLoaderUtil.newInstance(in.getClassLoader(), className);
                }
            }
            if (DataSerializableSerializer.isFlagSet(header, (byte)2)) {
                in.readByte();
                in.readByte();
            }
            ds.readData(in);
            return ds;
        }
        catch (Exception e) {
            e = this.tryClarifyNoSuchMethodException(in.getClassLoader(), className, e);
            throw this.rethrowReadException(id, factoryId, className, e);
        }
    }

    public static boolean isFlagSet(byte value, byte flag) {
        return (value & flag) != 0;
    }

    private IOException rethrowReadException(int id, int factoryId, String className, Exception e) throws IOException {
        if (e instanceof IOException) {
            throw (IOException)e;
        }
        if (e instanceof HazelcastSerializationException) {
            throw (HazelcastSerializationException)e;
        }
        throw new HazelcastSerializationException("Problem while reading DataSerializable, namespace: " + factoryId + ", ID: " + id + ", class: '" + className + "', exception: " + e.getMessage(), e);
    }

    private Exception tryClarifyInstantiationException(Class aClass, Exception exception) {
        if (!(exception instanceof InstantiationException)) {
            return exception;
        }
        InstantiationException instantiationException = (InstantiationException)exception;
        String message = DataSerializableSerializer.tryGenerateClarifiedExceptionMessage(aClass);
        if (message == null) {
            return instantiationException;
        }
        InstantiationException clarifiedException = new InstantiationException(message);
        clarifiedException.initCause(instantiationException);
        return clarifiedException;
    }

    private Exception tryClarifyNoSuchMethodException(ClassLoader classLoader, String className, Exception exception) {
        Class<?> aClass;
        if (!(exception instanceof NoSuchMethodException)) {
            return exception;
        }
        NoSuchMethodException noSuchMethodException = (NoSuchMethodException)exception;
        try {
            ClassLoader effectiveClassLoader = classLoader == null ? ClassLoaderUtil.class.getClassLoader() : classLoader;
            aClass = ClassLoaderUtil.loadClass(effectiveClassLoader, className);
        }
        catch (Exception e) {
            return noSuchMethodException;
        }
        String message = DataSerializableSerializer.tryGenerateClarifiedExceptionMessage(aClass);
        if (message == null) {
            message = "Classes conforming to DataSerializable should provide a no-arguments constructor.";
        }
        NoSuchMethodException clarifiedException = new NoSuchMethodException(message);
        clarifiedException.initCause(noSuchMethodException);
        return clarifiedException;
    }

    @Override
    public void write(ObjectDataOutput out, DataSerializable obj) throws IOException {
        DataSerializableSerializer.setOutputVersion(out, this.version);
        boolean identified = obj instanceof IdentifiedDataSerializable;
        out.writeBoolean(identified);
        if (identified) {
            IdentifiedDataSerializable ds = (IdentifiedDataSerializable)obj;
            out.writeInt(ds.getFactoryId());
            out.writeInt(ds.getId());
        } else if (obj instanceof TypedDataSerializable) {
            out.writeUTF(((TypedDataSerializable)obj).getClassType().getName());
        } else {
            out.writeUTF(obj.getClass().getName());
        }
        obj.writeData(out);
    }

    @Override
    public void destroy() {
        this.factories.clear();
    }

    private static void setOutputVersion(ObjectDataOutput out, Version version) {
        ((VersionedObjectDataOutput)out).setVersion(version);
    }

    private static void setInputVersion(ObjectDataInput in, Version version) {
        ((VersionedObjectDataInput)in).setVersion(version);
    }

    private static String tryGenerateClarifiedExceptionMessage(Class aClass) {
        String classType;
        if (aClass.isAnonymousClass()) {
            classType = "Anonymous";
        } else if (aClass.isLocalClass()) {
            classType = "Local";
        } else if (aClass.isMemberClass() && !Modifier.isStatic(aClass.getModifiers())) {
            classType = "Non-static member";
        } else {
            return null;
        }
        return String.format("%s classes can't conform to DataSerializable since they can't provide an explicit no-arguments constructor.", classType);
    }
}

