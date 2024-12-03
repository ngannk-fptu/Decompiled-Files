/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.lang;

import com.mchange.v1.lang.AmbiguousClassNameException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ClassUtils {
    static final String[] EMPTY_SA = new String[0];
    static Map primitivesToClasses;

    public static Set publicSupertypesForMethods(Class clazz, Method[] methodArray) {
        Set set = ClassUtils.allAssignableFrom(clazz);
        HashSet<Class> hashSet = new HashSet<Class>();
        for (Class clazz2 : set) {
            if (!ClassUtils.isPublic(clazz2) || !ClassUtils.hasAllMethodsAsSupertype(clazz2, methodArray)) continue;
            hashSet.add(clazz2);
        }
        return Collections.unmodifiableSet(hashSet);
    }

    public static boolean isPublic(Class clazz) {
        return (clazz.getModifiers() & 1) != 0;
    }

    public static boolean hasAllMethodsAsSupertype(Class clazz, Method[] methodArray) {
        return ClassUtils.hasAllMethods(clazz, methodArray, true);
    }

    public static boolean hasAllMethodsAsSubtype(Class clazz, Method[] methodArray) {
        return ClassUtils.hasAllMethods(clazz, methodArray, false);
    }

    private static boolean hasAllMethods(Class clazz, Method[] methodArray, boolean bl) {
        int n = methodArray.length;
        for (int i = 0; i < n; ++i) {
            if (ClassUtils.containsMethod(clazz, methodArray[i], bl)) continue;
            return false;
        }
        return true;
    }

    public static boolean containsMethodAsSupertype(Class clazz, Method method) {
        return ClassUtils.containsMethod(clazz, method, true);
    }

    public static boolean containsMethodAsSubtype(Class clazz, Method method) {
        return ClassUtils.containsMethod(clazz, method, false);
    }

    private static boolean containsMethod(Class clazz, Method method, boolean bl) {
        try {
            Method method2 = clazz.getMethod(method.getName(), method.getParameterTypes());
            Class<?> clazz2 = method.getReturnType();
            Class<?> clazz3 = method2.getReturnType();
            return clazz2.equals(clazz3) || bl && clazz3.isAssignableFrom(clazz2) || !bl && clazz2.isAssignableFrom(clazz3);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            return false;
        }
    }

    public static Set allAssignableFrom(Class clazz) {
        HashSet hashSet = new HashSet();
        for (Class clazz2 = clazz; clazz2 != null; clazz2 = clazz2.getSuperclass()) {
            hashSet.add(clazz2);
        }
        ClassUtils.addSuperInterfacesToSet(clazz, hashSet);
        return hashSet;
    }

    public static String simpleClassName(Class clazz) {
        int n = 0;
        while (clazz.isArray()) {
            ++n;
            clazz = clazz.getComponentType();
        }
        String string = ClassUtils.simpleClassName(clazz.getName());
        if (n > 0) {
            StringBuffer stringBuffer = new StringBuffer(16);
            stringBuffer.append(string);
            for (int i = 0; i < n; ++i) {
                stringBuffer.append("[]");
            }
            return stringBuffer.toString();
        }
        return string;
    }

    private static String simpleClassName(String string) {
        int n = string.lastIndexOf(46);
        if (n < 0) {
            return string;
        }
        String string2 = string.substring(n + 1);
        if (string2.indexOf(36) >= 0) {
            StringBuffer stringBuffer = new StringBuffer(string2);
            int n2 = stringBuffer.length();
            for (int i = 0; i < n2; ++i) {
                if (stringBuffer.charAt(i) != '$') continue;
                stringBuffer.setCharAt(i, '.');
            }
            return stringBuffer.toString();
        }
        return string2;
    }

    public static boolean isPrimitive(String string) {
        return primitivesToClasses.get(string) != null;
    }

    public static Class classForPrimitive(String string) {
        return (Class)primitivesToClasses.get(string);
    }

    public static Class forName(String string) throws ClassNotFoundException {
        Class<?> clazz = ClassUtils.classForPrimitive(string);
        if (clazz == null) {
            clazz = Class.forName(string);
        }
        return clazz;
    }

    public static Class forName(String string, String[] stringArray, String[] stringArray2) throws AmbiguousClassNameException, ClassNotFoundException {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            return ClassUtils.classForSimpleName(string, stringArray, stringArray2);
        }
    }

    public static Class classForSimpleName(String string, String[] stringArray, String[] stringArray2) throws AmbiguousClassNameException, ClassNotFoundException {
        HashSet<String> hashSet = new HashSet<String>();
        Class<?> clazz = ClassUtils.classForPrimitive(string);
        if (clazz == null) {
            String string2;
            if (stringArray == null) {
                stringArray = EMPTY_SA;
            }
            if (stringArray2 == null) {
                stringArray2 = EMPTY_SA;
            }
            int n = stringArray2.length;
            for (int i = 0; i < n; ++i) {
                string2 = ClassUtils.fqcnLastElement(stringArray2[i]);
                if (!hashSet.add(string2)) {
                    throw new IllegalArgumentException("Duplicate imported classes: " + string2);
                }
                if (!string.equals(string2)) continue;
                clazz = Class.forName(stringArray2[i]);
            }
            if (clazz == null) {
                try {
                    clazz = Class.forName("java.lang." + string);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    // empty catch block
                }
                n = stringArray.length;
                for (int i = 0; i < n; ++i) {
                    try {
                        string2 = stringArray[i] + '.' + string;
                        Class<?> clazz2 = Class.forName(string2);
                        if (clazz != null) {
                            throw new AmbiguousClassNameException(string, clazz, clazz2);
                        }
                        clazz = clazz2;
                        continue;
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        // empty catch block
                    }
                }
            }
        }
        if (clazz == null) {
            throw new ClassNotFoundException("Could not find a class whose unqualified name is \"" + string + "\" with the imports supplied. Import packages are " + Arrays.asList(stringArray) + "; class imports are " + Arrays.asList(stringArray2));
        }
        return clazz;
    }

    public static String resolvableTypeName(Class clazz, String[] stringArray, String[] stringArray2) throws ClassNotFoundException {
        String string = ClassUtils.simpleClassName(clazz);
        try {
            ClassUtils.classForSimpleName(string, stringArray, stringArray2);
        }
        catch (AmbiguousClassNameException ambiguousClassNameException) {
            return clazz.getName();
        }
        return string;
    }

    public static String fqcnLastElement(String string) {
        int n = string.lastIndexOf(46);
        if (n < 0) {
            return string;
        }
        return string.substring(n + 1);
    }

    private static void addSuperInterfacesToSet(Class clazz, Set set) {
        Class<?>[] classArray = clazz.getInterfaces();
        int n = classArray.length;
        for (int i = 0; i < n; ++i) {
            set.add(classArray[i]);
            ClassUtils.addSuperInterfacesToSet(classArray[i], set);
        }
    }

    private ClassUtils() {
    }

    static {
        HashMap<String, Class<Object>> hashMap = new HashMap<String, Class<Object>>();
        hashMap.put("boolean", Boolean.TYPE);
        hashMap.put("int", Integer.TYPE);
        hashMap.put("char", Character.TYPE);
        hashMap.put("short", Short.TYPE);
        hashMap.put("int", Integer.TYPE);
        hashMap.put("long", Long.TYPE);
        hashMap.put("float", Float.TYPE);
        hashMap.put("double", Double.TYPE);
        hashMap.put("void", Void.TYPE);
        primitivesToClasses = Collections.unmodifiableMap(hashMap);
    }
}

