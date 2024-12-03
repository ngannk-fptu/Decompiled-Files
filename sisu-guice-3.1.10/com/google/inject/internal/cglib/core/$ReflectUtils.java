/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm.$Attribute;
import com.google.inject.internal.asm.$Type;
import com.google.inject.internal.cglib.core.$ClassInfo;
import com.google.inject.internal.cglib.core.$CodeGenerationException;
import com.google.inject.internal.cglib.core.$Constants;
import com.google.inject.internal.cglib.core.$MethodInfo;
import com.google.inject.internal.cglib.core.$Signature;
import com.google.inject.internal.cglib.core.$TypeUtils;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class $ReflectUtils {
    private static final Map primitives = new HashMap(8);
    private static final Map transforms = new HashMap(8);
    private static final ClassLoader defaultLoader = (class$net$sf$cglib$core$ReflectUtils == null ? (class$net$sf$cglib$core$ReflectUtils = $ReflectUtils.class$("com.google.inject.internal.cglib.core.$ReflectUtils")) : class$net$sf$cglib$core$ReflectUtils).getClassLoader();
    private static Method DEFINE_CLASS;
    private static final ProtectionDomain PROTECTION_DOMAIN;
    private static final String[] CGLIB_PACKAGES;
    static /* synthetic */ Class class$net$sf$cglib$core$ReflectUtils;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class array$B;
    static /* synthetic */ Class class$java$security$ProtectionDomain;
    static /* synthetic */ Class class$java$lang$Object;

    private $ReflectUtils() {
    }

    public static $Type[] getExceptionTypes(Member member) {
        if (member instanceof Method) {
            return $TypeUtils.getTypes(((Method)member).getExceptionTypes());
        }
        if (member instanceof Constructor) {
            return $TypeUtils.getTypes(((Constructor)member).getExceptionTypes());
        }
        throw new IllegalArgumentException("Cannot get exception types of a field");
    }

    public static $Signature getSignature(Member member) {
        if (member instanceof Method) {
            return new $Signature(member.getName(), $Type.getMethodDescriptor((Method)member));
        }
        if (member instanceof Constructor) {
            $Type[] types = $TypeUtils.getTypes(((Constructor)member).getParameterTypes());
            return new $Signature("<init>", $Type.getMethodDescriptor($Type.VOID_TYPE, types));
        }
        throw new IllegalArgumentException("Cannot get signature of a field");
    }

    public static Constructor findConstructor(String desc) {
        return $ReflectUtils.findConstructor(desc, defaultLoader);
    }

    public static Constructor findConstructor(String desc, ClassLoader loader) {
        try {
            int lparen = desc.indexOf(40);
            String className = desc.substring(0, lparen).trim();
            return $ReflectUtils.getClass(className, loader).getConstructor($ReflectUtils.parseTypes(desc, loader));
        }
        catch (ClassNotFoundException e) {
            throw new $CodeGenerationException(e);
        }
        catch (NoSuchMethodException e) {
            throw new $CodeGenerationException(e);
        }
    }

    public static Method findMethod(String desc) {
        return $ReflectUtils.findMethod(desc, defaultLoader);
    }

    public static Method findMethod(String desc, ClassLoader loader) {
        try {
            int lparen = desc.indexOf(40);
            int dot = desc.lastIndexOf(46, lparen);
            String className = desc.substring(0, dot).trim();
            String methodName = desc.substring(dot + 1, lparen).trim();
            return $ReflectUtils.getClass(className, loader).getDeclaredMethod(methodName, $ReflectUtils.parseTypes(desc, loader));
        }
        catch (ClassNotFoundException e) {
            throw new $CodeGenerationException(e);
        }
        catch (NoSuchMethodException e) {
            throw new $CodeGenerationException(e);
        }
    }

    private static Class[] parseTypes(String desc, ClassLoader loader) throws ClassNotFoundException {
        int comma;
        int lparen = desc.indexOf(40);
        int rparen = desc.indexOf(41, lparen);
        ArrayList<String> params = new ArrayList<String>();
        int start = lparen + 1;
        while ((comma = desc.indexOf(44, start)) >= 0) {
            params.add(desc.substring(start, comma).trim());
            start = comma + 1;
        }
        if (start < rparen) {
            params.add(desc.substring(start, rparen).trim());
        }
        Class[] types = new Class[params.size()];
        for (int i = 0; i < types.length; ++i) {
            types[i] = $ReflectUtils.getClass((String)params.get(i), loader);
        }
        return types;
    }

    private static Class getClass(String className, ClassLoader loader) throws ClassNotFoundException {
        return $ReflectUtils.getClass(className, loader, CGLIB_PACKAGES);
    }

    private static Class getClass(String className, ClassLoader loader, String[] packages) throws ClassNotFoundException {
        String save = className;
        int dimensions = 0;
        int index = 0;
        while ((index = className.indexOf("[]", index) + 1) > 0) {
            ++dimensions;
        }
        StringBuffer brackets = new StringBuffer(className.length() - dimensions);
        for (int i = 0; i < dimensions; ++i) {
            brackets.append('[');
        }
        className = className.substring(0, className.length() - 2 * dimensions);
        String prefix = dimensions > 0 ? brackets + "L" : "";
        String suffix = dimensions > 0 ? ";" : "";
        try {
            return Class.forName(prefix + className + suffix, false, loader);
        }
        catch (ClassNotFoundException ignore) {
            for (int i = 0; i < packages.length; ++i) {
                try {
                    return Class.forName(prefix + packages[i] + '.' + className + suffix, false, loader);
                }
                catch (ClassNotFoundException ignore2) {
                    continue;
                }
            }
            if (dimensions == 0) {
                Class c = (Class)primitives.get(className);
                if (c != null) {
                    return c;
                }
            } else {
                String transform = (String)transforms.get(className);
                if (transform != null) {
                    try {
                        return Class.forName(brackets + transform, false, loader);
                    }
                    catch (ClassNotFoundException ignore3) {
                        // empty catch block
                    }
                }
            }
            throw new ClassNotFoundException(save);
        }
    }

    public static Object newInstance(Class type) {
        return $ReflectUtils.newInstance(type, $Constants.EMPTY_CLASS_ARRAY, null);
    }

    public static Object newInstance(Class type, Class[] parameterTypes, Object[] args) {
        return $ReflectUtils.newInstance($ReflectUtils.getConstructor(type, parameterTypes), args);
    }

    public static Object newInstance(Constructor cstruct, Object[] args) {
        boolean flag = cstruct.isAccessible();
        try {
            Object result;
            cstruct.setAccessible(true);
            Object t = result = cstruct.newInstance(args);
            return t;
        }
        catch (InstantiationException e) {
            throw new $CodeGenerationException(e);
        }
        catch (IllegalAccessException e) {
            throw new $CodeGenerationException(e);
        }
        catch (InvocationTargetException e) {
            throw new $CodeGenerationException(e.getTargetException());
        }
        finally {
            cstruct.setAccessible(flag);
        }
    }

    public static Constructor getConstructor(Class type, Class[] parameterTypes) {
        try {
            Constructor constructor = type.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor;
        }
        catch (NoSuchMethodException e) {
            throw new $CodeGenerationException(e);
        }
    }

    public static String[] getNames(Class[] classes) {
        if (classes == null) {
            return null;
        }
        String[] names = new String[classes.length];
        for (int i = 0; i < names.length; ++i) {
            names[i] = classes[i].getName();
        }
        return names;
    }

    public static Class[] getClasses(Object[] objects) {
        Class[] classes = new Class[objects.length];
        for (int i = 0; i < objects.length; ++i) {
            classes[i] = objects[i].getClass();
        }
        return classes;
    }

    public static Method findNewInstance(Class iface) {
        Method m = $ReflectUtils.findInterfaceMethod(iface);
        if (!m.getName().equals("newInstance")) {
            throw new IllegalArgumentException(iface + " missing newInstance method");
        }
        return m;
    }

    public static Method[] getPropertyMethods(PropertyDescriptor[] properties, boolean read, boolean write) {
        HashSet<Method> methods = new HashSet<Method>();
        for (int i = 0; i < properties.length; ++i) {
            PropertyDescriptor pd = properties[i];
            if (read) {
                methods.add(pd.getReadMethod());
            }
            if (!write) continue;
            methods.add(pd.getWriteMethod());
        }
        methods.remove(null);
        return methods.toArray(new Method[methods.size()]);
    }

    public static PropertyDescriptor[] getBeanProperties(Class type) {
        return $ReflectUtils.getPropertiesHelper(type, true, true);
    }

    public static PropertyDescriptor[] getBeanGetters(Class type) {
        return $ReflectUtils.getPropertiesHelper(type, true, false);
    }

    public static PropertyDescriptor[] getBeanSetters(Class type) {
        return $ReflectUtils.getPropertiesHelper(type, false, true);
    }

    private static PropertyDescriptor[] getPropertiesHelper(Class type, boolean read, boolean write) {
        try {
            BeanInfo info = Introspector.getBeanInfo(type, class$java$lang$Object == null ? (class$java$lang$Object = $ReflectUtils.class$("java.lang.Object")) : class$java$lang$Object);
            PropertyDescriptor[] all = info.getPropertyDescriptors();
            if (read && write) {
                return all;
            }
            ArrayList<PropertyDescriptor> properties = new ArrayList<PropertyDescriptor>(all.length);
            for (int i = 0; i < all.length; ++i) {
                PropertyDescriptor pd = all[i];
                if ((!read || pd.getReadMethod() == null) && (!write || pd.getWriteMethod() == null)) continue;
                properties.add(pd);
            }
            return properties.toArray(new PropertyDescriptor[properties.size()]);
        }
        catch (IntrospectionException e) {
            throw new $CodeGenerationException(e);
        }
    }

    public static Method findDeclaredMethod(Class type, String methodName, Class[] parameterTypes) throws NoSuchMethodException {
        for (Class cl = type; cl != null; cl = cl.getSuperclass()) {
            try {
                return cl.getDeclaredMethod(methodName, parameterTypes);
            }
            catch (NoSuchMethodException e) {
                continue;
            }
        }
        throw new NoSuchMethodException(methodName);
    }

    public static List addAllMethods(Class type, List list) {
        list.addAll(Arrays.asList(type.getDeclaredMethods()));
        Class superclass = type.getSuperclass();
        if (superclass != null) {
            $ReflectUtils.addAllMethods(superclass, list);
        }
        Class<?>[] interfaces = type.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            $ReflectUtils.addAllMethods(interfaces[i], list);
        }
        return list;
    }

    public static List addAllInterfaces(Class type, List list) {
        Class superclass = type.getSuperclass();
        if (superclass != null) {
            list.addAll(Arrays.asList(type.getInterfaces()));
            $ReflectUtils.addAllInterfaces(superclass, list);
        }
        return list;
    }

    public static Method findInterfaceMethod(Class iface) {
        if (!iface.isInterface()) {
            throw new IllegalArgumentException(iface + " is not an interface");
        }
        Method[] methods = iface.getDeclaredMethods();
        if (methods.length != 1) {
            throw new IllegalArgumentException("expecting exactly 1 method in " + iface);
        }
        return methods[0];
    }

    public static Class defineClass(String className, byte[] b, ClassLoader loader) throws Exception {
        Object[] args = new Object[]{className, b, new Integer(0), new Integer(b.length), PROTECTION_DOMAIN};
        Class c = (Class)DEFINE_CLASS.invoke((Object)loader, args);
        Class.forName(className, true, loader);
        return c;
    }

    public static int findPackageProtected(Class[] classes) {
        for (int i = 0; i < classes.length; ++i) {
            if (Modifier.isPublic(classes[i].getModifiers())) continue;
            return i;
        }
        return 0;
    }

    public static $MethodInfo getMethodInfo(final Member member, final int modifiers) {
        final $Signature sig = $ReflectUtils.getSignature(member);
        return new $MethodInfo(){
            private $ClassInfo ci;

            public $ClassInfo getClassInfo() {
                if (this.ci == null) {
                    this.ci = $ReflectUtils.getClassInfo(member.getDeclaringClass());
                }
                return this.ci;
            }

            public int getModifiers() {
                return modifiers;
            }

            public $Signature getSignature() {
                return sig;
            }

            public $Type[] getExceptionTypes() {
                return $ReflectUtils.getExceptionTypes(member);
            }

            public $Attribute getAttribute() {
                return null;
            }
        };
    }

    public static $MethodInfo getMethodInfo(Member member) {
        return $ReflectUtils.getMethodInfo(member, member.getModifiers());
    }

    public static $ClassInfo getClassInfo(final Class clazz) {
        final $Type type = $Type.getType(clazz);
        final $Type sc = clazz.getSuperclass() == null ? null : $Type.getType(clazz.getSuperclass());
        return new $ClassInfo(){

            public $Type getType() {
                return type;
            }

            public $Type getSuperType() {
                return sc;
            }

            public $Type[] getInterfaces() {
                return $TypeUtils.getTypes(clazz.getInterfaces());
            }

            public int getModifiers() {
                return clazz.getModifiers();
            }
        };
    }

    public static Method[] findMethods(String[] namesAndDescriptors, Method[] methods) {
        HashMap<String, Method> map = new HashMap<String, Method>();
        for (int i = 0; i < methods.length; ++i) {
            Method method = methods[i];
            map.put(method.getName() + $Type.getMethodDescriptor(method), method);
        }
        Method[] result = new Method[namesAndDescriptors.length / 2];
        for (int i = 0; i < result.length; ++i) {
            result[i] = (Method)map.get(namesAndDescriptors[i * 2] + namesAndDescriptors[i * 2 + 1]);
            if (result[i] != null) continue;
        }
        return result;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        PROTECTION_DOMAIN = (ProtectionDomain)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return (class$net$sf$cglib$core$ReflectUtils == null ? (class$net$sf$cglib$core$ReflectUtils = $ReflectUtils.class$("com.google.inject.internal.cglib.core.$ReflectUtils")) : class$net$sf$cglib$core$ReflectUtils).getProtectionDomain();
            }
        });
        AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                try {
                    Class<?> loader = Class.forName("java.lang.ClassLoader");
                    DEFINE_CLASS = loader.getDeclaredMethod("defineClass", class$java$lang$String == null ? (class$java$lang$String = $ReflectUtils.class$("java.lang.String")) : class$java$lang$String, array$B == null ? (array$B = $ReflectUtils.class$("[B")) : array$B, Integer.TYPE, Integer.TYPE, class$java$security$ProtectionDomain == null ? (class$java$security$ProtectionDomain = $ReflectUtils.class$("java.security.ProtectionDomain")) : class$java$security$ProtectionDomain);
                    DEFINE_CLASS.setAccessible(true);
                }
                catch (ClassNotFoundException e) {
                    throw new $CodeGenerationException(e);
                }
                catch (NoSuchMethodException e) {
                    throw new $CodeGenerationException(e);
                }
                return null;
            }
        });
        CGLIB_PACKAGES = new String[]{"java.lang"};
        primitives.put("byte", Byte.TYPE);
        primitives.put("char", Character.TYPE);
        primitives.put("double", Double.TYPE);
        primitives.put("float", Float.TYPE);
        primitives.put("int", Integer.TYPE);
        primitives.put("long", Long.TYPE);
        primitives.put("short", Short.TYPE);
        primitives.put("boolean", Boolean.TYPE);
        transforms.put("byte", "B");
        transforms.put("char", "C");
        transforms.put("double", "D");
        transforms.put("float", "F");
        transforms.put("int", "I");
        transforms.put("long", "J");
        transforms.put("short", "S");
        transforms.put("boolean", "Z");
    }
}

