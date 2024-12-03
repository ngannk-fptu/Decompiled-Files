/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ClassUtil {
    public static List<Class<?>> findSuperTypes(Class<?> cls, Class<?> endBefore) {
        return ClassUtil.findSuperTypes(cls, endBefore, new ArrayList(8));
    }

    public static List<Class<?>> findSuperTypes(Class<?> cls, Class<?> endBefore, List<Class<?>> result) {
        ClassUtil._addSuperTypes(cls, endBefore, result, false);
        return result;
    }

    private static void _addSuperTypes(Class<?> cls, Class<?> endBefore, Collection<Class<?>> result, boolean addClassItself) {
        if (cls == endBefore || cls == null || cls == Object.class) {
            return;
        }
        if (addClassItself) {
            if (result.contains(cls)) {
                return;
            }
            result.add(cls);
        }
        for (Class<?> intCls : cls.getInterfaces()) {
            ClassUtil._addSuperTypes(intCls, endBefore, result, true);
        }
        ClassUtil._addSuperTypes(cls.getSuperclass(), endBefore, result, true);
    }

    public static String canBeABeanType(Class<?> type) {
        if (type.isAnnotation()) {
            return "annotation";
        }
        if (type.isArray()) {
            return "array";
        }
        if (type.isEnum()) {
            return "enum";
        }
        if (type.isPrimitive()) {
            return "primitive";
        }
        return null;
    }

    @Deprecated
    public static String isLocalType(Class<?> type) {
        return ClassUtil.isLocalType(type, false);
    }

    public static String isLocalType(Class<?> type, boolean allowNonStatic) {
        try {
            if (type.getEnclosingMethod() != null) {
                return "local/anonymous";
            }
            if (!allowNonStatic && type.getEnclosingClass() != null && !Modifier.isStatic(type.getModifiers())) {
                return "non-static member class";
            }
        }
        catch (SecurityException securityException) {
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
        return null;
    }

    public static Class<?> getOuterClass(Class<?> type) {
        try {
            if (type.getEnclosingMethod() != null) {
                return null;
            }
            if (!Modifier.isStatic(type.getModifiers())) {
                return type.getEnclosingClass();
            }
        }
        catch (SecurityException securityException) {
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
        return null;
    }

    public static boolean isProxyType(Class<?> type) {
        String name = type.getName();
        return name.startsWith("net.sf.cglib.proxy.") || name.startsWith("org.hibernate.proxy.");
    }

    public static boolean isConcrete(Class<?> type) {
        int mod = type.getModifiers();
        return (mod & 0x600) == 0;
    }

    public static boolean isConcrete(Member member) {
        int mod = member.getModifiers();
        return (mod & 0x600) == 0;
    }

    public static boolean isCollectionMapOrArray(Class<?> type) {
        if (type.isArray()) {
            return true;
        }
        if (Collection.class.isAssignableFrom(type)) {
            return true;
        }
        return Map.class.isAssignableFrom(type);
    }

    public static String getClassDescription(Object classOrInstance) {
        if (classOrInstance == null) {
            return "unknown";
        }
        Class<?> cls = classOrInstance instanceof Class ? (Class<?>)classOrInstance : classOrInstance.getClass();
        return cls.getName();
    }

    public static Class<?> findClass(String className) throws ClassNotFoundException {
        if (className.indexOf(46) < 0) {
            if ("int".equals(className)) {
                return Integer.TYPE;
            }
            if ("long".equals(className)) {
                return Long.TYPE;
            }
            if ("float".equals(className)) {
                return Float.TYPE;
            }
            if ("double".equals(className)) {
                return Double.TYPE;
            }
            if ("boolean".equals(className)) {
                return Boolean.TYPE;
            }
            if ("byte".equals(className)) {
                return Byte.TYPE;
            }
            if ("char".equals(className)) {
                return Character.TYPE;
            }
            if ("short".equals(className)) {
                return Short.TYPE;
            }
            if ("void".equals(className)) {
                return Void.TYPE;
            }
        }
        Throwable prob = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader != null) {
            try {
                return Class.forName(className, true, loader);
            }
            catch (Exception e) {
                prob = ClassUtil.getRootCause(e);
            }
        }
        try {
            return Class.forName(className);
        }
        catch (Exception e) {
            if (prob == null) {
                prob = ClassUtil.getRootCause(e);
            }
            if (prob instanceof RuntimeException) {
                throw (RuntimeException)prob;
            }
            throw new ClassNotFoundException(prob.getMessage(), prob);
        }
    }

    public static boolean hasGetterSignature(Method m) {
        if (Modifier.isStatic(m.getModifiers())) {
            return false;
        }
        Class<?>[] pts = m.getParameterTypes();
        if (pts != null && pts.length != 0) {
            return false;
        }
        return Void.TYPE != m.getReturnType();
    }

    public static Throwable getRootCause(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }

    public static void throwRootCause(Throwable t) throws Exception {
        if ((t = ClassUtil.getRootCause(t)) instanceof Exception) {
            throw (Exception)t;
        }
        throw (Error)t;
    }

    public static void throwAsIAE(Throwable t) {
        ClassUtil.throwAsIAE(t, t.getMessage());
    }

    public static void throwAsIAE(Throwable t, String msg) {
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
        throw new IllegalArgumentException(msg, t);
    }

    public static void unwrapAndThrowAsIAE(Throwable t) {
        ClassUtil.throwAsIAE(ClassUtil.getRootCause(t));
    }

    public static void unwrapAndThrowAsIAE(Throwable t, String msg) {
        ClassUtil.throwAsIAE(ClassUtil.getRootCause(t), msg);
    }

    public static <T> T createInstance(Class<T> cls, boolean canFixAccess) throws IllegalArgumentException {
        Constructor<T> ctor = ClassUtil.findConstructor(cls, canFixAccess);
        if (ctor == null) {
            throw new IllegalArgumentException("Class " + cls.getName() + " has no default (no arg) constructor");
        }
        try {
            return ctor.newInstance(new Object[0]);
        }
        catch (Exception e) {
            ClassUtil.unwrapAndThrowAsIAE(e, "Failed to instantiate class " + cls.getName() + ", problem: " + e.getMessage());
            return null;
        }
    }

    public static <T> Constructor<T> findConstructor(Class<T> cls, boolean canFixAccess) throws IllegalArgumentException {
        try {
            Constructor<T> ctor = cls.getDeclaredConstructor(new Class[0]);
            if (canFixAccess) {
                ClassUtil.checkAndFixAccess(ctor);
            } else if (!Modifier.isPublic(ctor.getModifiers())) {
                throw new IllegalArgumentException("Default constructor for " + cls.getName() + " is not accessible (non-public?): not allowed to try modify access via Reflection: can not instantiate type");
            }
            return ctor;
        }
        catch (NoSuchMethodException ctor) {
        }
        catch (Exception e) {
            ClassUtil.unwrapAndThrowAsIAE(e, "Failed to find default constructor of class " + cls.getName() + ", problem: " + e.getMessage());
        }
        return null;
    }

    public static Object defaultValue(Class<?> cls) {
        if (cls == Integer.TYPE) {
            return 0;
        }
        if (cls == Long.TYPE) {
            return 0L;
        }
        if (cls == Boolean.TYPE) {
            return Boolean.FALSE;
        }
        if (cls == Double.TYPE) {
            return 0.0;
        }
        if (cls == Float.TYPE) {
            return Float.valueOf(0.0f);
        }
        if (cls == Byte.TYPE) {
            return (byte)0;
        }
        if (cls == Short.TYPE) {
            return (short)0;
        }
        if (cls == Character.TYPE) {
            return Character.valueOf('\u0000');
        }
        throw new IllegalArgumentException("Class " + cls.getName() + " is not a primitive type");
    }

    public static Class<?> wrapperType(Class<?> primitiveType) {
        if (primitiveType == Integer.TYPE) {
            return Integer.class;
        }
        if (primitiveType == Long.TYPE) {
            return Long.class;
        }
        if (primitiveType == Boolean.TYPE) {
            return Boolean.class;
        }
        if (primitiveType == Double.TYPE) {
            return Double.class;
        }
        if (primitiveType == Float.TYPE) {
            return Float.class;
        }
        if (primitiveType == Byte.TYPE) {
            return Byte.class;
        }
        if (primitiveType == Short.TYPE) {
            return Short.class;
        }
        if (primitiveType == Character.TYPE) {
            return Character.class;
        }
        throw new IllegalArgumentException("Class " + primitiveType.getName() + " is not a primitive type");
    }

    public static void checkAndFixAccess(Member member) {
        block2: {
            AccessibleObject ao = (AccessibleObject)((Object)member);
            try {
                ao.setAccessible(true);
            }
            catch (SecurityException se) {
                if (ao.isAccessible()) break block2;
                Class<?> declClass = member.getDeclaringClass();
                throw new IllegalArgumentException("Can not access " + member + " (from class " + declClass.getName() + "; failed to set access: " + se.getMessage());
            }
        }
    }

    public static Class<? extends Enum<?>> findEnumType(EnumSet<?> s) {
        if (!s.isEmpty()) {
            return ClassUtil.findEnumType((Enum)s.iterator().next());
        }
        return EnumTypeLocator.instance.enumTypeFor(s);
    }

    public static Class<? extends Enum<?>> findEnumType(EnumMap<?, ?> m) {
        if (!m.isEmpty()) {
            return ClassUtil.findEnumType((Enum)m.keySet().iterator().next());
        }
        return EnumTypeLocator.instance.enumTypeFor(m);
    }

    public static Class<? extends Enum<?>> findEnumType(Enum<?> en) {
        Class<?> ec = en.getClass();
        if (ec.getSuperclass() != Enum.class) {
            ec = ec.getSuperclass();
        }
        return ec;
    }

    public static Class<? extends Enum<?>> findEnumType(Class<?> cls) {
        if (cls.getSuperclass() != Enum.class) {
            cls = cls.getSuperclass();
        }
        return cls;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class EnumTypeLocator {
        static final EnumTypeLocator instance = new EnumTypeLocator();
        private final Field enumSetTypeField = EnumTypeLocator.locateField(EnumSet.class, "elementType", Class.class);
        private final Field enumMapTypeField = EnumTypeLocator.locateField(EnumMap.class, "elementType", Class.class);

        private EnumTypeLocator() {
        }

        public Class<? extends Enum<?>> enumTypeFor(EnumSet<?> set) {
            if (this.enumSetTypeField != null) {
                return (Class)this.get(set, this.enumSetTypeField);
            }
            throw new IllegalStateException("Can not figure out type for EnumSet (odd JDK platform?)");
        }

        public Class<? extends Enum<?>> enumTypeFor(EnumMap<?, ?> set) {
            if (this.enumMapTypeField != null) {
                return (Class)this.get(set, this.enumMapTypeField);
            }
            throw new IllegalStateException("Can not figure out type for EnumMap (odd JDK platform?)");
        }

        private Object get(Object bean, Field field) {
            try {
                return field.get(bean);
            }
            catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        private static Field locateField(Class<?> fromClass, String expectedName, Class<?> type) {
            Field[] fields;
            Field found = null;
            for (Field f : fields = fromClass.getDeclaredFields()) {
                if (!expectedName.equals(f.getName()) || f.getType() != type) continue;
                found = f;
                break;
            }
            if (found == null) {
                for (Field f : fields) {
                    if (f.getType() != type) continue;
                    if (found != null) {
                        return null;
                    }
                    found = f;
                }
            }
            if (found != null) {
                try {
                    found.setAccessible(true);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            return found;
        }
    }
}

