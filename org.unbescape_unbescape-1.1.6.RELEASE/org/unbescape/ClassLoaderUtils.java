/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape;

import java.io.IOException;
import java.io.InputStream;

final class ClassLoaderUtils {
    private static final ClassLoader classClassLoader = ClassLoaderUtils.getClassClassLoader(ClassLoaderUtils.class);
    private static final ClassLoader systemClassLoader = ClassLoaderUtils.getSystemClassLoader();
    private static final boolean systemClassLoaderAccessibleFromClassClassLoader = ClassLoaderUtils.isKnownClassLoaderAccessibleFrom(systemClassLoader, classClassLoader);

    static InputStream loadResourceAsStream(String resourceName) throws IOException {
        InputStream inputStream = ClassLoaderUtils.findResourceAsStream(resourceName);
        if (inputStream != null) {
            return inputStream;
        }
        throw new IOException("Could not locate resource '" + resourceName + "' in the application's class path");
    }

    static InputStream findResourceAsStream(String resourceName) {
        InputStream inputStream;
        ClassLoader contextClassLoader = ClassLoaderUtils.getThreadContextClassLoader();
        if (contextClassLoader != null && (inputStream = contextClassLoader.getResourceAsStream(resourceName)) != null) {
            return inputStream;
        }
        if (!ClassLoaderUtils.isKnownLeafClassLoader(contextClassLoader)) {
            if (classClassLoader != null && classClassLoader != contextClassLoader && (inputStream = classClassLoader.getResourceAsStream(resourceName)) != null) {
                return inputStream;
            }
            if (!systemClassLoaderAccessibleFromClassClassLoader && systemClassLoader != null && systemClassLoader != contextClassLoader && systemClassLoader != classClassLoader && (inputStream = systemClassLoader.getResourceAsStream(resourceName)) != null) {
                return inputStream;
            }
        }
        return null;
    }

    private static ClassLoader getThreadContextClassLoader() {
        try {
            return Thread.currentThread().getContextClassLoader();
        }
        catch (SecurityException se) {
            return null;
        }
    }

    private static ClassLoader getClassClassLoader(Class<?> clazz) {
        try {
            return clazz.getClassLoader();
        }
        catch (SecurityException se) {
            return null;
        }
    }

    private static ClassLoader getSystemClassLoader() {
        try {
            return ClassLoader.getSystemClassLoader();
        }
        catch (SecurityException se) {
            return null;
        }
    }

    private static boolean isKnownClassLoaderAccessibleFrom(ClassLoader accessibleCL, ClassLoader fromCL) {
        if (fromCL == null) {
            return false;
        }
        try {
            ClassLoader parent;
            for (parent = fromCL; parent != null && parent != accessibleCL; parent = parent.getParent()) {
            }
            return parent != null && parent == accessibleCL;
        }
        catch (SecurityException se) {
            return false;
        }
    }

    private static boolean isKnownLeafClassLoader(ClassLoader classLoader) {
        if (classLoader == null) {
            return false;
        }
        if (!ClassLoaderUtils.isKnownClassLoaderAccessibleFrom(classClassLoader, classLoader)) {
            return false;
        }
        return systemClassLoaderAccessibleFromClassClassLoader;
    }

    private ClassLoaderUtils() {
    }
}

