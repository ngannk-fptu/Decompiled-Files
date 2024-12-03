/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.util;

public class ClassUtils {
    private ClassUtils() {
    }

    public static Class<?> forName(String className) throws ClassNotFoundException {
        return ClassUtils.forName(ClassUtils.class, className);
    }

    public static Class<?> forName(Class<?> contextClass, String className) throws ClassNotFoundException {
        Class<?> c = null;
        if (contextClass.getClassLoader() != null) {
            c = ClassUtils.forName(className, contextClass.getClassLoader());
        }
        if (c == null && Thread.currentThread().getContextClassLoader() != null) {
            c = ClassUtils.forName(className, Thread.currentThread().getContextClassLoader());
        }
        if (c == null) {
            throw new ClassNotFoundException("Failed to load class" + className);
        }
        return c;
    }

    public static Class<?> forName(ClassLoader classLoader, String className) throws ClassNotFoundException {
        Class<?> c = null;
        if (classLoader != null) {
            c = ClassUtils.forName(className, classLoader);
        }
        if (c == null && Thread.currentThread().getContextClassLoader() != null) {
            c = ClassUtils.forName(className, Thread.currentThread().getContextClassLoader());
        }
        if (c == null) {
            throw new ClassNotFoundException("Failed to load class" + className);
        }
        return c;
    }

    private static Class<?> forName(String className, ClassLoader classLoader) {
        Class<?> c = null;
        if (classLoader != null && className != null) {
            try {
                c = Class.forName(className, true, classLoader);
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        return c;
    }
}

