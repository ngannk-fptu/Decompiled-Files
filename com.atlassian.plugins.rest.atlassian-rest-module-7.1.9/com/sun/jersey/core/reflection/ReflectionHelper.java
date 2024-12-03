/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.reflection;

import com.sun.jersey.core.osgi.OsgiRegistry;
import com.sun.jersey.impl.ImplMessages;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReflectionHelper {
    private static final Logger LOGGER = Logger.getLogger(ReflectionHelper.class.getName());
    private static final PrivilegedAction NoOpPrivilegedACTION = new PrivilegedAction(){

        public Object run() {
            return null;
        }
    };

    public static Class getDeclaringClass(AccessibleObject ao) {
        if (ao instanceof Method) {
            return ((Method)ao).getDeclaringClass();
        }
        if (ao instanceof Field) {
            return ((Field)ao).getDeclaringClass();
        }
        if (ao instanceof Constructor) {
            return ((Constructor)ao).getDeclaringClass();
        }
        throw new RuntimeException();
    }

    public static String objectToString(Object o) {
        if (o == null) {
            return "null";
        }
        StringBuffer sb = new StringBuffer();
        sb.append(o.getClass().getName()).append('@').append(Integer.toHexString(o.hashCode()));
        return sb.toString();
    }

    public static String methodInstanceToString(Object o, Method m) {
        StringBuffer sb = new StringBuffer();
        sb.append(o.getClass().getName()).append('@').append(Integer.toHexString(o.hashCode())).append('.').append(m.getName()).append('(');
        Class<?>[] params = m.getParameterTypes();
        for (int i = 0; i < params.length; ++i) {
            sb.append(ReflectionHelper.getTypeName(params[i]));
            if (i >= params.length - 1) continue;
            sb.append(",");
        }
        sb.append(')');
        return sb.toString();
    }

    private static String getTypeName(Class type) {
        if (type.isArray()) {
            try {
                Class<?> cl = type;
                int dimensions = 0;
                while (cl.isArray()) {
                    ++dimensions;
                    cl = cl.getComponentType();
                }
                StringBuffer sb = new StringBuffer();
                sb.append(cl.getName());
                for (int i = 0; i < dimensions; ++i) {
                    sb.append("[]");
                }
                return sb.toString();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return type.getName();
    }

    public static PrivilegedAction<Class<?>> classForNamePA(String name) {
        return ReflectionHelper.classForNamePA(name, ReflectionHelper.getContextClassLoader());
    }

    public static PrivilegedAction<Class<?>> classForNamePA(final String name, final ClassLoader cl) {
        return new PrivilegedAction<Class<?>>(){

            @Override
            public Class<?> run() {
                block6: {
                    if (cl != null) {
                        try {
                            return Class.forName(name, false, cl);
                        }
                        catch (ClassNotFoundException ex) {
                            if (!LOGGER.isLoggable(Level.FINE)) break block6;
                            LOGGER.log(Level.FINE, "Unable to load class " + name + " using the supplied class loader " + cl.getClass().getName() + ".", ex);
                        }
                    }
                }
                try {
                    return Class.forName(name);
                }
                catch (ClassNotFoundException ex) {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.log(Level.FINE, "Unable to load class " + name + " using the current class loader.", ex);
                    }
                    return null;
                }
            }
        };
    }

    public static <T> PrivilegedExceptionAction<Class<T>> classForNameWithExceptionPEA(String name) throws ClassNotFoundException {
        return ReflectionHelper.classForNameWithExceptionPEA(name, ReflectionHelper.getContextClassLoader());
    }

    public static <T> PrivilegedExceptionAction<Class<T>> classForNameWithExceptionPEA(final String name, final ClassLoader cl) throws ClassNotFoundException {
        return new PrivilegedExceptionAction<Class<T>>(){

            @Override
            public Class<T> run() throws ClassNotFoundException {
                if (cl != null) {
                    try {
                        return Class.forName(name, false, cl);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        // empty catch block
                    }
                }
                return Class.forName(name);
            }
        };
    }

    public static PrivilegedAction<ClassLoader> getContextClassLoaderPA() {
        return new PrivilegedAction<ClassLoader>(){

            @Override
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        };
    }

    private static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged(ReflectionHelper.getContextClassLoaderPA());
    }

    public static PrivilegedAction setAccessibleMethodPA(final Method m) {
        if (Modifier.isPublic(m.getModifiers())) {
            return NoOpPrivilegedACTION;
        }
        return new PrivilegedAction<Object>(){

            @Override
            public Object run() {
                if (!m.isAccessible()) {
                    m.setAccessible(true);
                }
                return m;
            }
        };
    }

    public static Class getGenericClass(Type parameterizedType) throws IllegalArgumentException {
        Type t = ReflectionHelper.getTypeArgumentOfParameterizedType(parameterizedType);
        if (t == null) {
            return null;
        }
        Class c = ReflectionHelper.getClassOfType(t);
        if (c == null) {
            throw new IllegalArgumentException(ImplMessages.GENERIC_TYPE_NOT_SUPPORTED(t, parameterizedType));
        }
        return c;
    }

    public static TypeClassPair getTypeArgumentAndClass(Type parameterizedType) throws IllegalArgumentException {
        Type t = ReflectionHelper.getTypeArgumentOfParameterizedType(parameterizedType);
        if (t == null) {
            return null;
        }
        Class c = ReflectionHelper.getClassOfType(t);
        if (c == null) {
            throw new IllegalArgumentException(ImplMessages.GENERIC_TYPE_NOT_SUPPORTED(t, parameterizedType));
        }
        return new TypeClassPair(t, c);
    }

    private static Type getTypeArgumentOfParameterizedType(Type parameterizedType) {
        if (!(parameterizedType instanceof ParameterizedType)) {
            return null;
        }
        ParameterizedType type = (ParameterizedType)parameterizedType;
        Type[] genericTypes = type.getActualTypeArguments();
        if (genericTypes.length != 1) {
            return null;
        }
        return genericTypes[0];
    }

    private static Class getClassOfType(Type type) {
        ParameterizedType subType;
        Type t;
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType)type;
            Type t2 = arrayType.getGenericComponentType();
            if (t2 instanceof Class) {
                return ReflectionHelper.getArrayClass((Class)t2);
            }
        } else if (type instanceof ParameterizedType && (t = (subType = (ParameterizedType)type).getRawType()) instanceof Class) {
            return (Class)t;
        }
        return null;
    }

    public static Class getArrayClass(Class c) {
        try {
            Object o = Array.newInstance(c, 0);
            return o.getClass();
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static PrivilegedAction<Method> getValueOfStringMethodPA(final Class<?> c) {
        return new PrivilegedAction<Method>(){

            @Override
            public Method run() {
                try {
                    Method m = c.getDeclaredMethod("valueOf", String.class);
                    if (!Modifier.isStatic(m.getModifiers()) && m.getReturnType() == c) {
                        return null;
                    }
                    return m;
                }
                catch (NoSuchMethodException nsme) {
                    return null;
                }
            }
        };
    }

    public static PrivilegedAction<Method> getFromStringStringMethodPA(final Class<?> c) {
        return new PrivilegedAction<Method>(){

            @Override
            public Method run() {
                try {
                    Method m = c.getDeclaredMethod("fromString", String.class);
                    if (!Modifier.isStatic(m.getModifiers()) && m.getReturnType() == c) {
                        return null;
                    }
                    return m;
                }
                catch (NoSuchMethodException nsme) {
                    return null;
                }
            }
        };
    }

    public static PrivilegedAction<Constructor> getStringConstructorPA(final Class<?> c) {
        return new PrivilegedAction<Constructor>(){

            @Override
            public Constructor run() {
                try {
                    return c.getConstructor(String.class);
                }
                catch (SecurityException e) {
                    throw e;
                }
                catch (Exception e) {
                    return null;
                }
            }
        };
    }

    public static Class[] getParameterizedClassArguments(DeclaringClassInterfacePair p) {
        if (p.genericInterface instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)p.genericInterface;
            Type[] as = pt.getActualTypeArguments();
            Class[] cas = new Class[as.length];
            for (int i = 0; i < as.length; ++i) {
                Type a = as[i];
                if (a instanceof Class) {
                    cas[i] = (Class)a;
                    continue;
                }
                if (a instanceof ParameterizedType) {
                    pt = (ParameterizedType)a;
                    cas[i] = (Class)pt.getRawType();
                    continue;
                }
                if (!(a instanceof TypeVariable)) continue;
                ClassTypePair ctp = ReflectionHelper.resolveTypeVariable(p.concreteClass, p.declaringClass, (TypeVariable)a);
                cas[i] = ctp != null ? ctp.c : Object.class;
            }
            return cas;
        }
        return null;
    }

    public static Type[] getParameterizedTypeArguments(DeclaringClassInterfacePair p) {
        if (p.genericInterface instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)p.genericInterface;
            Type[] as = pt.getActualTypeArguments();
            Type[] ras = new Type[as.length];
            for (int i = 0; i < as.length; ++i) {
                Type a = as[i];
                if (a instanceof Class) {
                    ras[i] = a;
                    continue;
                }
                if (a instanceof ParameterizedType) {
                    pt = (ParameterizedType)a;
                    ras[i] = a;
                    continue;
                }
                if (!(a instanceof TypeVariable)) continue;
                ClassTypePair ctp = ReflectionHelper.resolveTypeVariable(p.concreteClass, p.declaringClass, (TypeVariable)a);
                ras[i] = ctp.t;
            }
            return ras;
        }
        return null;
    }

    public static DeclaringClassInterfacePair getClass(Class concrete, Class iface) {
        return ReflectionHelper.getClass(concrete, iface, concrete);
    }

    private static DeclaringClassInterfacePair getClass(Class concrete, Class iface, Class c) {
        Type[] gis = c.getGenericInterfaces();
        DeclaringClassInterfacePair p = ReflectionHelper.getType(concrete, iface, c, gis);
        if (p != null) {
            return p;
        }
        if ((c = c.getSuperclass()) == null || c == Object.class) {
            return null;
        }
        return ReflectionHelper.getClass(concrete, iface, c);
    }

    private static DeclaringClassInterfacePair getType(Class concrete, Class iface, Class c, Type[] ts) {
        for (Type t : ts) {
            DeclaringClassInterfacePair p = ReflectionHelper.getType(concrete, iface, c, t);
            if (p == null) continue;
            return p;
        }
        return null;
    }

    private static DeclaringClassInterfacePair getType(Class concrete, Class iface, Class c, Type t) {
        if (t instanceof Class) {
            if (t == iface) {
                return new DeclaringClassInterfacePair(concrete, c, t);
            }
            return ReflectionHelper.getClass(concrete, iface, (Class)t);
        }
        if (t instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)t;
            if (pt.getRawType() == iface) {
                return new DeclaringClassInterfacePair(concrete, c, t);
            }
            return ReflectionHelper.getClass(concrete, iface, (Class)pt.getRawType());
        }
        return null;
    }

    public static ClassTypePair resolveTypeVariable(Class c, Class dc, TypeVariable tv) {
        return ReflectionHelper.resolveTypeVariable(c, dc, tv, new HashMap<TypeVariable, Type>());
    }

    private static ClassTypePair resolveTypeVariable(Class c, Class dc, TypeVariable tv, Map<TypeVariable, Type> map) {
        Type[] gis;
        for (Type gi : gis = c.getGenericInterfaces()) {
            ParameterizedType pt;
            ClassTypePair ctp;
            if (!(gi instanceof ParameterizedType) || (ctp = ReflectionHelper.resolveTypeVariable(pt = (ParameterizedType)gi, (Class)pt.getRawType(), dc, tv, map)) == null) continue;
            return ctp;
        }
        Type gsc = c.getGenericSuperclass();
        if (gsc instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)gsc;
            return ReflectionHelper.resolveTypeVariable(pt, c.getSuperclass(), dc, tv, map);
        }
        if (gsc instanceof Class) {
            return ReflectionHelper.resolveTypeVariable(c.getSuperclass(), dc, tv, map);
        }
        return null;
    }

    private static ClassTypePair resolveTypeVariable(ParameterizedType pt, Class c, Class dc, TypeVariable tv, Map<TypeVariable, Type> map) {
        Type[] typeArguments = pt.getActualTypeArguments();
        TypeVariable<Class<T>>[] typeParameters = c.getTypeParameters();
        HashMap<TypeVariable, Type> submap = new HashMap<TypeVariable, Type>();
        for (int i = 0; i < typeArguments.length; ++i) {
            if (typeArguments[i] instanceof TypeVariable) {
                Type t2 = map.get(typeArguments[i]);
                submap.put(typeParameters[i], t2);
                continue;
            }
            submap.put(typeParameters[i], typeArguments[i]);
        }
        if (c == dc) {
            Type t = (Type)submap.get(tv);
            if (t instanceof Class) {
                return new ClassTypePair((Class)t);
            }
            if (t instanceof GenericArrayType) {
                if ((t = ((GenericArrayType)t).getGenericComponentType()) instanceof Class) {
                    c = (Class)t;
                    try {
                        return new ClassTypePair(ReflectionHelper.getArrayClass(c));
                    }
                    catch (Exception t2) {
                        return null;
                    }
                }
                if (t instanceof ParameterizedType) {
                    Type rt = ((ParameterizedType)t).getRawType();
                    if (!(rt instanceof Class)) {
                        return null;
                    }
                    c = (Class)rt;
                    try {
                        return new ClassTypePair(ReflectionHelper.getArrayClass(c), t);
                    }
                    catch (Exception e) {
                        return null;
                    }
                }
                return null;
            }
            if (t instanceof ParameterizedType) {
                pt = (ParameterizedType)t;
                if (pt.getRawType() instanceof Class) {
                    return new ClassTypePair((Class)pt.getRawType(), pt);
                }
                return null;
            }
            return null;
        }
        return ReflectionHelper.resolveTypeVariable(c, dc, tv, submap);
    }

    public static PrivilegedAction<Method> findMethodOnClassPA(final Class<?> c, final Method m) {
        return new PrivilegedAction<Method>(){

            @Override
            public Method run() {
                try {
                    return c.getMethod(m.getName(), m.getParameterTypes());
                }
                catch (NoSuchMethodException nsme) {
                    for (Method _m : c.getMethods()) {
                        if (!_m.getName().equals(m.getName()) || _m.getParameterTypes().length != m.getParameterTypes().length || !ReflectionHelper.compareParameterTypes(m.getGenericParameterTypes(), _m.getGenericParameterTypes())) continue;
                        return _m;
                    }
                    return null;
                }
            }
        };
    }

    public static OsgiRegistry getOsgiRegistryInstance() {
        try {
            Class<?> bundleReferenceClass = Class.forName("org.osgi.framework.BundleReference");
            if (bundleReferenceClass != null) {
                return OsgiRegistry.getInstance();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return null;
    }

    private static boolean compareParameterTypes(Type[] ts, Type[] _ts) {
        for (int i = 0; i < ts.length; ++i) {
            if (ts[i].equals(_ts[i]) || _ts[i] instanceof TypeVariable) continue;
            return false;
        }
        return true;
    }

    public static class ClassTypePair {
        public final Class c;
        public final Type t;

        public ClassTypePair(Class c) {
            this(c, c);
        }

        public ClassTypePair(Class c, Type t) {
            this.c = c;
            this.t = t;
        }
    }

    public static class DeclaringClassInterfacePair {
        public final Class concreteClass;
        public final Class declaringClass;
        public final Type genericInterface;

        private DeclaringClassInterfacePair(Class concreteClass, Class declaringClass, Type genericInteface) {
            this.concreteClass = concreteClass;
            this.declaringClass = declaringClass;
            this.genericInterface = genericInteface;
        }
    }

    public static final class TypeClassPair {
        public final Type t;
        public final Class c;

        public TypeClassPair(Type t, Class c) {
            this.t = t;
            this.c = c;
        }
    }
}

