/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.xml.mapping;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.privilegedactions.LoadClass;

class ClassLoadingHelper {
    private static final String PACKAGE_SEPARATOR = ".";
    private static final String ARRAY_CLASS_NAME_PREFIX = "[L";
    private static final String ARRAY_CLASS_NAME_SUFFIX = ";";
    private static final Map<String, Class<?>> PRIMITIVE_NAME_TO_PRIMITIVE;
    private final ClassLoader externalClassLoader;
    private final ClassLoader threadContextClassLoader;

    ClassLoadingHelper(ClassLoader externalClassLoader, ClassLoader threadContextClassLoader) {
        this.externalClassLoader = externalClassLoader;
        this.threadContextClassLoader = threadContextClassLoader;
    }

    Class<?> loadClass(String className, String defaultPackage) {
        if (PRIMITIVE_NAME_TO_PRIMITIVE.containsKey(className)) {
            return PRIMITIVE_NAME_TO_PRIMITIVE.get(className);
        }
        StringBuilder fullyQualifiedClass = new StringBuilder();
        String tmpClassName = className;
        if (ClassLoadingHelper.isArrayClassName(className)) {
            fullyQualifiedClass.append(ARRAY_CLASS_NAME_PREFIX);
            tmpClassName = ClassLoadingHelper.getArrayElementClassName(className);
        }
        if (ClassLoadingHelper.isQualifiedClass(tmpClassName)) {
            fullyQualifiedClass.append(tmpClassName);
        } else {
            fullyQualifiedClass.append(defaultPackage);
            fullyQualifiedClass.append(PACKAGE_SEPARATOR);
            fullyQualifiedClass.append(tmpClassName);
        }
        if (ClassLoadingHelper.isArrayClassName(className)) {
            fullyQualifiedClass.append(ARRAY_CLASS_NAME_SUFFIX);
        }
        return this.loadClass(fullyQualifiedClass.toString());
    }

    private Class<?> loadClass(String className) {
        return (Class)ClassLoadingHelper.run(LoadClass.action(className, this.externalClassLoader, this.threadContextClassLoader));
    }

    private static boolean isArrayClassName(String className) {
        return className.startsWith(ARRAY_CLASS_NAME_PREFIX) && className.endsWith(ARRAY_CLASS_NAME_SUFFIX);
    }

    private static String getArrayElementClassName(String className) {
        return className.substring(2, className.length() - 1);
    }

    private static boolean isQualifiedClass(String clazz) {
        return clazz.contains(PACKAGE_SEPARATOR);
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }

    static {
        HashMap<String, Class<Object>> tmpMap = CollectionHelper.newHashMap(9);
        tmpMap.put(Boolean.TYPE.getName(), Boolean.TYPE);
        tmpMap.put(Character.TYPE.getName(), Character.TYPE);
        tmpMap.put(Double.TYPE.getName(), Double.TYPE);
        tmpMap.put(Float.TYPE.getName(), Float.TYPE);
        tmpMap.put(Long.TYPE.getName(), Long.TYPE);
        tmpMap.put(Integer.TYPE.getName(), Integer.TYPE);
        tmpMap.put(Short.TYPE.getName(), Short.TYPE);
        tmpMap.put(Byte.TYPE.getName(), Byte.TYPE);
        tmpMap.put(Void.TYPE.getName(), Void.TYPE);
        PRIMITIVE_NAME_TO_PRIMITIVE = Collections.unmodifiableMap(tmpMap);
    }
}

