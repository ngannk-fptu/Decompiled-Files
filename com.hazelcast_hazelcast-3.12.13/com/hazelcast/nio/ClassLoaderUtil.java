/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.internal.usercodedeployment.impl.ClassSource;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.util.ConcurrentReferenceHashMap;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@PrivateApi
public final class ClassLoaderUtil {
    public static final String HAZELCAST_BASE_PACKAGE = "com.hazelcast.";
    public static final String HAZELCAST_ARRAY = "[Lcom.hazelcast.";
    private static final boolean CLASS_CACHE_DISABLED = Boolean.getBoolean("hazelcast.compat.classloading.cache.disabled");
    private static final Map<String, Class> PRIMITIVE_CLASSES;
    private static final int MAX_PRIM_CLASS_NAME_LENGTH = 7;
    private static final ClassLoaderWeakCache<Constructor> CONSTRUCTOR_CACHE;
    private static final ClassLoaderWeakCache<Class> CLASS_CACHE;
    private static final Constructor<?> IRRESOLVABLE_CONSTRUCTOR;

    private ClassLoaderUtil() {
    }

    public static <T> T getOrCreate(T instance, ClassLoader classLoader, String className) {
        if (instance != null) {
            return instance;
        }
        if (className != null) {
            try {
                return ClassLoaderUtil.newInstance(classLoader, className);
            }
            catch (Exception e) {
                throw ExceptionUtil.rethrow(e);
            }
        }
        return null;
    }

    public static <T> T newInstance(ClassLoader classLoaderHint, String className) throws Exception {
        Preconditions.isNotNull(className, "className");
        Class<?> primitiveClass = ClassLoaderUtil.tryPrimitiveClass(className);
        if (primitiveClass != null) {
            return (T)primitiveClass.newInstance();
        }
        ClassLoader cl1 = classLoaderHint;
        if (cl1 == null) {
            cl1 = ClassLoaderUtil.class.getClassLoader();
        }
        if (cl1 == null) {
            cl1 = Thread.currentThread().getContextClassLoader();
        }
        ClassLoader cl2 = null;
        if ((className.startsWith(HAZELCAST_BASE_PACKAGE) || className.startsWith(HAZELCAST_ARRAY)) && cl1 != ClassLoaderUtil.class.getClassLoader()) {
            cl2 = ClassLoaderUtil.class.getClassLoader();
        }
        if (cl2 == null) {
            cl2 = Thread.currentThread().getContextClassLoader();
        }
        if (cl1 == cl2) {
            cl2 = null;
        }
        if (cl1 == null && cl2 != null) {
            cl1 = cl2;
            cl2 = null;
        }
        if (cl1 != null) {
            Constructor constructor = CONSTRUCTOR_CACHE.get(cl1, className);
            if (constructor == IRRESOLVABLE_CONSTRUCTOR && cl2 != null) {
                constructor = CONSTRUCTOR_CACHE.get(cl2, className);
            }
            if (constructor != null && constructor != IRRESOLVABLE_CONSTRUCTOR) {
                return constructor.newInstance(new Object[0]);
            }
        }
        try {
            return ClassLoaderUtil.newInstance0(cl1, className);
        }
        catch (ClassNotFoundException e1) {
            if (cl2 != null) {
                ((ClassLoaderWeakCache)ClassLoaderUtil.CONSTRUCTOR_CACHE).put(cl1, className, ClassLoaderUtil.IRRESOLVABLE_CONSTRUCTOR);
                try {
                    return ClassLoaderUtil.newInstance0(cl2, className);
                }
                catch (ClassNotFoundException e2) {
                    EmptyStatement.ignore(e2);
                }
            }
            throw e1;
        }
    }

    private static <T> T newInstance0(ClassLoader classLoader, String className) throws Exception {
        Class<?> klass = classLoader == null ? Class.forName(className) : ClassLoaderUtil.tryLoadClass(className, classLoader);
        Constructor<?> constructor = klass.getDeclaredConstructor(new Class[0]);
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
        if (!ClassLoaderUtil.shouldBypassCache(klass) && classLoader != null) {
            ((ClassLoaderWeakCache)ClassLoaderUtil.CONSTRUCTOR_CACHE).put(classLoader, className, constructor);
        }
        return (T)constructor.newInstance(new Object[0]);
    }

    public static Class<?> loadClass(ClassLoader classLoaderHint, String className) throws ClassNotFoundException {
        Preconditions.isNotNull(className, "className");
        Class<?> primitiveClass = ClassLoaderUtil.tryPrimitiveClass(className);
        if (primitiveClass != null) {
            return primitiveClass;
        }
        ClassLoader theClassLoader = classLoaderHint;
        if (theClassLoader == null) {
            theClassLoader = Thread.currentThread().getContextClassLoader();
        }
        if (theClassLoader != null) {
            try {
                return ClassLoaderUtil.tryLoadClass(className, theClassLoader);
            }
            catch (ClassNotFoundException ignore) {
                theClassLoader = null;
            }
        }
        if (className.startsWith(HAZELCAST_BASE_PACKAGE) || className.startsWith(HAZELCAST_ARRAY)) {
            theClassLoader = ClassLoaderUtil.class.getClassLoader();
        }
        if (theClassLoader == null) {
            theClassLoader = Thread.currentThread().getContextClassLoader();
        }
        if (theClassLoader != null) {
            return ClassLoaderUtil.tryLoadClass(className, theClassLoader);
        }
        return Class.forName(className);
    }

    private static Class<?> tryPrimitiveClass(String className) {
        Class primitiveClass;
        if (className.length() <= 7 && Character.isLowerCase(className.charAt(0)) && (primitiveClass = PRIMITIVE_CLASSES.get(className)) != null) {
            return primitiveClass;
        }
        return null;
    }

    public static boolean isClassAvailable(ClassLoader classLoader, String className) {
        try {
            Class<?> clazz = ClassLoaderUtil.loadClass(classLoader, className);
            return clazz != null;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static Class<?> tryLoadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
        Class<?> clazz;
        if (!CLASS_CACHE_DISABLED && (clazz = CLASS_CACHE.get(classLoader, className)) != null) {
            return clazz;
        }
        clazz = className.startsWith("[") ? Class.forName(className, false, classLoader) : classLoader.loadClass(className);
        if (!CLASS_CACHE_DISABLED && !ClassLoaderUtil.shouldBypassCache(clazz)) {
            ((ClassLoaderWeakCache)ClassLoaderUtil.CLASS_CACHE).put(classLoader, className, clazz);
        }
        return clazz;
    }

    public static boolean isInternalType(Class type) {
        String name = type.getName();
        ClassLoader classLoader = ClassLoaderUtil.class.getClassLoader();
        return type.getClassLoader() == classLoader && name.startsWith(HAZELCAST_BASE_PACKAGE);
    }

    public static Class<?> tryLoadClass(String className) throws ClassNotFoundException {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            return contextClassLoader.loadClass(className);
        }
    }

    public static boolean isClassDefined(String className) {
        try {
            ClassLoaderUtil.tryLoadClass(className);
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean implementsInterfaceWithSameName(Class<?> clazz, Class<?> iface) {
        Class<?>[] interfaces;
        for (Class<?> implementedInterface : interfaces = ClassLoaderUtil.getAllInterfaces(clazz)) {
            if (!implementedInterface.getName().equals(iface.getName())) continue;
            return true;
        }
        return false;
    }

    public static Class<?>[] getAllInterfaces(Class<?> clazz) {
        HashSet interfaces = new HashSet();
        ClassLoaderUtil.addOwnInterfaces(clazz, interfaces);
        ClassLoaderUtil.addInterfacesOfSuperclasses(clazz, interfaces);
        return interfaces.toArray(new Class[0]);
    }

    private static void addOwnInterfaces(Class<?> clazz, Collection<Class<?>> allInterfaces) {
        Class<?>[] interfaces = clazz.getInterfaces();
        Collections.addAll(allInterfaces, interfaces);
        for (Class<?> cl : interfaces) {
            ClassLoaderUtil.addOwnInterfaces(cl, allInterfaces);
        }
    }

    private static void addInterfacesOfSuperclasses(Class<?> clazz, Collection<Class<?>> interfaces) {
        for (Class<?> superClass = clazz.getSuperclass(); superClass != null; superClass = superClass.getSuperclass()) {
            ClassLoaderUtil.addOwnInterfaces(superClass, interfaces);
        }
    }

    private static boolean shouldBypassCache(Class clazz) {
        return clazz.getClassLoader() instanceof ClassSource;
    }

    static {
        CONSTRUCTOR_CACHE = new ClassLoaderWeakCache();
        CLASS_CACHE = new ClassLoaderWeakCache();
        try {
            IRRESOLVABLE_CONSTRUCTOR = IrresolvableConstructor.class.getConstructor(new Class[0]);
        }
        catch (NoSuchMethodException e) {
            throw new Error("Couldn't initialize irresolvable constructor.", e);
        }
        HashMap<String, Class<Object>> primitives = new HashMap<String, Class<Object>>(10, 1.0f);
        primitives.put("boolean", Boolean.TYPE);
        primitives.put("byte", Byte.TYPE);
        primitives.put("int", Integer.TYPE);
        primitives.put("long", Long.TYPE);
        primitives.put("short", Short.TYPE);
        primitives.put("float", Float.TYPE);
        primitives.put("double", Double.TYPE);
        primitives.put("char", Character.TYPE);
        primitives.put("void", Void.TYPE);
        PRIMITIVE_CLASSES = Collections.unmodifiableMap(primitives);
    }

    private static final class IrresolvableConstructor {
        public IrresolvableConstructor() {
            throw new UnsupportedOperationException("Irresolvable constructor should never be instantiated.");
        }
    }

    private static final class ClassLoaderWeakCache<V> {
        private final ConcurrentMap<ClassLoader, ConcurrentMap<String, WeakReference<V>>> cache = new ConcurrentReferenceHashMap<ClassLoader, ConcurrentMap<String, WeakReference<V>>>(16);

        private ClassLoaderWeakCache() {
        }

        private void put(ClassLoader classLoader, String className, V value) {
            ConcurrentMap old;
            ClassLoader cl = classLoader == null ? ClassLoaderUtil.class.getClassLoader() : classLoader;
            ConcurrentMap<String, WeakReference<V>> innerCache = (ConcurrentHashMap<String, WeakReference<V>>)this.cache.get(cl);
            if (innerCache == null && (old = (ConcurrentMap)this.cache.putIfAbsent(cl, innerCache = new ConcurrentHashMap<String, WeakReference<V>>(100))) != null) {
                innerCache = old;
            }
            innerCache.put(className, new WeakReference<V>(value));
        }

        public V get(ClassLoader classloader, String className) {
            V value;
            Preconditions.isNotNull(className, "className");
            ConcurrentMap innerCache = (ConcurrentMap)this.cache.get(classloader);
            if (innerCache == null) {
                return null;
            }
            WeakReference reference = (WeakReference)innerCache.get(className);
            V v = value = reference == null ? null : (V)reference.get();
            if (reference != null && value == null) {
                innerCache.remove(className);
            }
            return value;
        }
    }
}

