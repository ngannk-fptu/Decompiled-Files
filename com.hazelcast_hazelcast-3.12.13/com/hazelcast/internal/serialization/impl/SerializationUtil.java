/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.instance.OutOfMemoryErrorDispatcher;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.ByteArrayObjectDataInput;
import com.hazelcast.internal.serialization.impl.ByteArraySerializerAdapter;
import com.hazelcast.internal.serialization.impl.ObjectDataInputStream;
import com.hazelcast.internal.serialization.impl.ObjectDataOutputStream;
import com.hazelcast.internal.serialization.impl.SerializerAdapter;
import com.hazelcast.internal.serialization.impl.StreamSerializerAdapter;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.ByteArraySerializer;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.nio.serialization.Serializer;
import com.hazelcast.nio.serialization.StreamSerializer;
import com.hazelcast.nio.serialization.VersionedPortable;
import com.hazelcast.util.MapUtil;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public final class SerializationUtil {
    static final PartitioningStrategy EMPTY_PARTITIONING_STRATEGY = new EmptyPartitioningStrategy();

    private SerializationUtil() {
    }

    static boolean isNullData(Data data) {
        return data.dataSize() == 0 && data.getType() == 0;
    }

    static RuntimeException handleException(Throwable e) {
        if (e instanceof OutOfMemoryError) {
            OutOfMemoryErrorDispatcher.onOutOfMemory((OutOfMemoryError)e);
            throw (Error)e;
        }
        if (e instanceof Error) {
            throw (Error)e;
        }
        if (e instanceof HazelcastSerializationException) {
            throw (HazelcastSerializationException)e;
        }
        throw new HazelcastSerializationException(e);
    }

    static RuntimeException handleSerializeException(Object rootObject, Throwable e) {
        if (e instanceof OutOfMemoryError) {
            OutOfMemoryErrorDispatcher.onOutOfMemory((OutOfMemoryError)e);
            throw (Error)e;
        }
        if (e instanceof Error) {
            throw (Error)e;
        }
        String clazz = rootObject == null ? "null" : rootObject.getClass().getName();
        throw new HazelcastSerializationException("Failed to serialize '" + clazz + '\'', e);
    }

    static SerializerAdapter createSerializerAdapter(Serializer serializer, InternalSerializationService serializationService) {
        SerializerAdapter s;
        if (serializer instanceof StreamSerializer) {
            s = new StreamSerializerAdapter(serializationService, (StreamSerializer)serializer);
        } else if (serializer instanceof ByteArraySerializer) {
            s = new ByteArraySerializerAdapter((ByteArraySerializer)serializer);
        } else {
            throw new IllegalArgumentException("Serializer " + serializer.getClass().getName() + " must be an instance of either StreamSerializer or ByteArraySerializer");
        }
        return s;
    }

    static void getInterfaces(Class clazz, Set<Class> interfaces) {
        Class<?>[] classes = clazz.getInterfaces();
        if (classes.length > 0) {
            Collections.addAll(interfaces, classes);
            for (Class<?> cl : classes) {
                SerializationUtil.getInterfaces(cl, interfaces);
            }
        }
    }

    static int indexForDefaultType(int typeId) {
        return -typeId;
    }

    public static int getPortableVersion(Portable portable, int defaultVersion) {
        VersionedPortable versionedPortable;
        int version = defaultVersion;
        if (portable instanceof VersionedPortable && (version = (versionedPortable = (VersionedPortable)portable).getClassVersion()) < 0) {
            throw new IllegalArgumentException("Version cannot be negative!");
        }
        return version;
    }

    public static ObjectDataOutputStream createObjectDataOutputStream(OutputStream out, InternalSerializationService ss) {
        return new ObjectDataOutputStream(out, ss);
    }

    public static ObjectDataInputStream createObjectDataInputStream(InputStream in, InternalSerializationService ss) {
        return new ObjectDataInputStream(in, ss);
    }

    public static InputStream convertToInputStream(DataInput in, int offset) {
        if (!(in instanceof ByteArrayObjectDataInput)) {
            throw new HazelcastSerializationException("Cannot convert " + in.getClass().getName() + " to input stream");
        }
        ByteArrayObjectDataInput byteArrayInput = (ByteArrayObjectDataInput)in;
        return new ByteArrayInputStream(byteArrayInput.data, offset, byteArrayInput.size - offset);
    }

    public static <T> void writeNullableList(List<T> list, ObjectDataOutput out) throws IOException {
        if (list == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeInt(list.size());
            for (T item : list) {
                out.writeObject(item);
            }
        }
    }

    public static <T> List<T> readNullableList(ObjectDataInput in) throws IOException {
        boolean notNull = in.readBoolean();
        ArrayList list = null;
        if (notNull) {
            int size = in.readInt();
            list = new ArrayList(size);
            for (int i = 0; i < size; ++i) {
                Object item = in.readObject();
                list.add(item);
            }
        }
        return list;
    }

    public static <K, V> void writeNullableMap(Map<K, V> map, ObjectDataOutput out) throws IOException {
        out.writeBoolean(map != null);
        if (map == null) {
            return;
        }
        SerializationUtil.writeMap(map, out);
    }

    public static <K, V> void writeMap(@Nonnull Map<K, V> map, ObjectDataOutput out) throws IOException {
        out.writeInt(map.size());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            out.writeObject(entry.getKey());
            out.writeObject(entry.getValue());
        }
    }

    public static <K, V> Map<K, V> readNullableMap(ObjectDataInput in) throws IOException {
        boolean isNull;
        boolean bl = isNull = !in.readBoolean();
        if (isNull) {
            return null;
        }
        return SerializationUtil.readMap(in);
    }

    @Nonnull
    public static <K, V> Map<K, V> readMap(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        Map map = MapUtil.createHashMap(size);
        for (int i = 0; i < size; ++i) {
            Object key = in.readObject();
            Object value = in.readObject();
            map.put(key, value);
        }
        return map;
    }

    public static <T> void writeCollection(Collection<T> items, ObjectDataOutput out) throws IOException {
        out.writeInt(items.size());
        for (T item : items) {
            out.writeObject(item);
        }
    }

    public static <T> Collection<T> readCollection(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        if (size == 0) {
            return Collections.emptyList();
        }
        ArrayList collection = new ArrayList(size);
        for (int i = 0; i < size; ++i) {
            Object item = in.readObject();
            collection.add(item);
        }
        return collection;
    }

    @SerializableByConvention
    private static class EmptyPartitioningStrategy
    implements PartitioningStrategy {
        private EmptyPartitioningStrategy() {
        }

        public Object getPartitionKey(Object key) {
            return null;
        }
    }
}

