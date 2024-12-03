/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public enum ClassLoaderHelper {


    public static URL getResource(String resource, Class<?> ... classes) {
        return ClassLoaderHelper.getResource(resource, false, classes);
    }

    public static URL getResource(String resource, boolean classesFirst, Class<?> ... classes) {
        URL url;
        if (classesFirst) {
            url = ClassLoaderHelper.getResourceViaClasses(resource, classes);
            if (url == null) {
                url = ClassLoaderHelper.getResourceViaContext(resource);
            }
        } else {
            url = ClassLoaderHelper.getResourceViaContext(resource);
            if (url == null) {
                url = ClassLoaderHelper.getResourceViaClasses(resource, classes);
            }
        }
        return url == null ? ClassLoaderHelper.class.getResource(resource) : url;
    }

    private static URL getResourceViaClasses(String resource, Class<?>[] classes) {
        if (classes != null) {
            for (Class<?> c : classes) {
                URL url = c.getResource(resource);
                if (url == null) continue;
                return url;
            }
        }
        return null;
    }

    private static URL getResourceViaContext(String resource) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return loader == null ? null : loader.getResource(resource);
    }

    private static Class<?> loadClassViaClasses(String fqcn, Class<?>[] classes) {
        if (classes != null) {
            for (Class<?> c : classes) {
                ClassLoader loader = c.getClassLoader();
                if (loader == null) continue;
                try {
                    return loader.loadClass(fqcn);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    // empty catch block
                }
            }
        }
        return null;
    }

    private static Class<?> loadClassViaContext(String fqcn) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
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

    public static InputStream getResourceAsStream(String resource, Class<?> ... classes) {
        return ClassLoaderHelper.getResourceAsStream(resource, false, classes);
    }

    public static InputStream getResourceAsStream(String resource, boolean classesFirst, Class<?> ... classes) {
        URL url = ClassLoaderHelper.getResource(resource, classesFirst, classes);
        try {
            return url != null ? url.openStream() : null;
        }
        catch (IOException e) {
            return null;
        }
    }
}

