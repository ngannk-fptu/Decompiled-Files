/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.json;

import java.lang.reflect.Constructor;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractQueue;
import java.util.AbstractSequentialList;
import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import nonapi.io.github.classgraph.json.ClassFields;
import nonapi.io.github.classgraph.json.JSONUtils;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;

class ClassFieldCache {
    private final Map<Class<?>, ClassFields> classToClassFields = new HashMap();
    private final boolean resolveTypes;
    private final boolean onlySerializePublicFields;
    private final Map<Class<?>, Constructor<?>> defaultConstructorForConcreteType = new HashMap();
    private final Map<Class<?>, Constructor<?>> constructorForConcreteTypeWithSizeHint = new HashMap();
    private static final Constructor<?> NO_CONSTRUCTOR;
    ReflectionUtils reflectionUtils;

    ClassFieldCache(boolean forDeserialization, boolean onlySerializePublicFields, ReflectionUtils reflectionUtils) {
        this.resolveTypes = forDeserialization;
        this.onlySerializePublicFields = !forDeserialization && onlySerializePublicFields;
        this.reflectionUtils = reflectionUtils;
    }

    ClassFields get(Class<?> cls) {
        ClassFields classFields = this.classToClassFields.get(cls);
        if (classFields == null) {
            classFields = new ClassFields(cls, this.resolveTypes, this.onlySerializePublicFields, this, this.reflectionUtils);
            this.classToClassFields.put(cls, classFields);
        }
        return classFields;
    }

    private static Class<?> getConcreteType(Class<?> rawType, boolean returnNullIfNotMapOrCollection) {
        if (rawType == Map.class || rawType == AbstractMap.class || rawType == HashMap.class) {
            return HashMap.class;
        }
        if (rawType == ConcurrentMap.class || rawType == ConcurrentHashMap.class) {
            return ConcurrentHashMap.class;
        }
        if (rawType == SortedMap.class || rawType == NavigableMap.class || rawType == TreeMap.class) {
            return TreeMap.class;
        }
        if (rawType == ConcurrentNavigableMap.class || rawType == ConcurrentSkipListMap.class) {
            return ConcurrentSkipListMap.class;
        }
        if (rawType == List.class || rawType == AbstractList.class || rawType == ArrayList.class || rawType == Collection.class) {
            return ArrayList.class;
        }
        if (rawType == AbstractSequentialList.class || rawType == LinkedList.class) {
            return LinkedList.class;
        }
        if (rawType == Set.class || rawType == AbstractSet.class || rawType == HashSet.class) {
            return HashSet.class;
        }
        if (rawType == SortedSet.class || rawType == TreeSet.class) {
            return TreeSet.class;
        }
        if (rawType == Queue.class || rawType == AbstractQueue.class || rawType == Deque.class || rawType == ArrayDeque.class) {
            return ArrayDeque.class;
        }
        if (rawType == BlockingQueue.class || rawType == LinkedBlockingQueue.class) {
            return LinkedBlockingQueue.class;
        }
        if (rawType == BlockingDeque.class || rawType == LinkedBlockingDeque.class) {
            return LinkedBlockingDeque.class;
        }
        if (rawType == TransferQueue.class || rawType == LinkedTransferQueue.class) {
            return LinkedTransferQueue.class;
        }
        return returnNullIfNotMapOrCollection ? null : rawType;
    }

    Constructor<?> getDefaultConstructorForConcreteTypeOf(Class<?> cls) {
        Class<?> concreteType;
        if (cls == null) {
            throw new IllegalArgumentException("Class reference cannot be null");
        }
        Constructor<?> constructor = this.defaultConstructorForConcreteType.get(cls);
        if (constructor != null) {
            return constructor;
        }
        for (Class<?> c = concreteType = ClassFieldCache.getConcreteType(cls, false); c != null && (c != Object.class || cls == Object.class); c = c.getSuperclass()) {
            try {
                Constructor<?> defaultConstructor = c.getDeclaredConstructor(new Class[0]);
                JSONUtils.makeAccessible(defaultConstructor, this.reflectionUtils);
                this.defaultConstructorForConcreteType.put(cls, defaultConstructor);
                return defaultConstructor;
            }
            catch (ReflectiveOperationException | SecurityException exception) {
                continue;
            }
        }
        throw new IllegalArgumentException("Class " + cls.getName() + " does not have an accessible default (no-arg) constructor");
    }

    Constructor<?> getConstructorWithSizeHintForConcreteTypeOf(Class<?> cls) {
        Constructor<?> constructor = this.constructorForConcreteTypeWithSizeHint.get(cls);
        if (constructor == NO_CONSTRUCTOR) {
            return null;
        }
        if (constructor != null) {
            return constructor;
        }
        Class<?> concreteType = ClassFieldCache.getConcreteType(cls, true);
        if (concreteType != null) {
            for (Class<?> c = concreteType; c != null && (c != Object.class || cls == Object.class); c = c.getSuperclass()) {
                try {
                    Constructor<?> constructorWithSizeHint = c.getDeclaredConstructor(Integer.TYPE);
                    JSONUtils.makeAccessible(constructorWithSizeHint, this.reflectionUtils);
                    this.constructorForConcreteTypeWithSizeHint.put(cls, constructorWithSizeHint);
                    return constructorWithSizeHint;
                }
                catch (ReflectiveOperationException | SecurityException exception) {
                    continue;
                }
            }
        }
        this.constructorForConcreteTypeWithSizeHint.put(cls, NO_CONSTRUCTOR);
        return null;
    }

    static {
        try {
            NO_CONSTRUCTOR = NoConstructor.class.getDeclaredConstructor(new Class[0]);
        }
        catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException("Could not find or access constructor for " + NoConstructor.class.getName(), e);
        }
    }

    private static class NoConstructor {
    }
}

