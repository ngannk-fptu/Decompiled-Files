/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.core.internal.util;

import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class ClassLoaderHelper {
    private ClassLoaderHelper() {
    }

    private static Class<?> loadClassViaClasses(String fqcn, Class<?>[] classes) {
        if (classes == null) {
            return null;
        }
        for (Class<?> clzz : classes) {
            ClassLoader loader;
            if (clzz == null || (loader = clzz.getClassLoader()) == null) continue;
            try {
                return loader.loadClass(fqcn);
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        return null;
    }

    private static Class<?> loadClassViaContext(String fqcn) {
        ClassLoader loader = ClassLoaderHelper.contextClassLoader();
        try {
            return loader == null ? null : loader.loadClass(fqcn);
        }
        catch (ClassNotFoundException classNotFoundException) {
            return null;
        }
    }

    public static Class<?> loadClass(String fqcn, Class<?> ... classes) throws ClassNotFoundException {
        return ClassLoaderHelper.loadClass(fqcn, true, classes);
    }

    public static Class<?> loadClass(String fqcn, boolean classesFirst, Class<?> ... classes) throws ClassNotFoundException {
        Class<?> target = null;
        if (classesFirst) {
            target = ClassLoaderHelper.loadClassViaClasses(fqcn, classes);
            if (target == null) {
                target = ClassLoaderHelper.loadClassViaContext(fqcn);
            }
        } else {
            target = ClassLoaderHelper.loadClassViaContext(fqcn);
            if (target == null) {
                target = ClassLoaderHelper.loadClassViaClasses(fqcn, classes);
            }
        }
        return target == null ? Class.forName(fqcn) : target;
    }

    private static ClassLoader contextClassLoader() {
        ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
        if (threadClassLoader != null) {
            return threadClassLoader;
        }
        return ClassLoader.getSystemClassLoader();
    }

    public static ClassLoader classLoader(Class<?> ... classes) {
        if (classes != null) {
            for (Class<?> clzz : classes) {
                ClassLoader classLoader = clzz.getClassLoader();
                if (classLoader == null) continue;
                return classLoader;
            }
        }
        return ClassLoaderHelper.contextClassLoader();
    }
}

