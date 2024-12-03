/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

public class ClassLoaderUtils {
    public static <T> Class<T> loadClass(String className, Class<?> callingClass) throws ClassNotFoundException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            try {
                return ClassLoaderUtils.coerce(contextClassLoader.loadClass(className));
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        try {
            return ClassLoaderUtils.coerce(Class.forName(className));
        }
        catch (ClassNotFoundException ex) {
            try {
                return ClassLoaderUtils.coerce(ClassLoaderUtils.class.getClassLoader().loadClass(className));
            }
            catch (ClassNotFoundException exc) {
                if (callingClass != null && callingClass.getClassLoader() != null) {
                    return ClassLoaderUtils.coerce(callingClass.getClassLoader().loadClass(className));
                }
                throw exc;
            }
        }
    }

    private static <T> Class<T> coerce(Class<?> klass) {
        Class<?> result = klass;
        return result;
    }

    public static URL getResource(String resourceName, Class<?> callingClass) {
        URL url = null;
        if (Thread.currentThread().getContextClassLoader() != null) {
            url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        }
        if (url == null) {
            url = ClassLoaderUtils.class.getClassLoader().getResource(resourceName);
        }
        if (url == null) {
            url = callingClass.getClassLoader().getResource(resourceName);
        }
        return url;
    }

    public static Enumeration<URL> getResources(String resourceName, Class<?> callingClass) throws IOException {
        Enumeration<URL> urls = null;
        if (Thread.currentThread().getContextClassLoader() != null) {
            urls = Thread.currentThread().getContextClassLoader().getResources(resourceName);
        }
        if (urls == null) {
            urls = ClassLoaderUtils.class.getClassLoader().getResources(resourceName);
        }
        if (urls == null) {
            urls = callingClass.getClassLoader().getResources(resourceName);
        }
        return urls;
    }

    public static InputStream getResourceAsStream(String resourceName, Class<?> callingClass) {
        URL url = ClassLoaderUtils.getResource(resourceName, callingClass);
        try {
            return url != null ? url.openStream() : null;
        }
        catch (IOException e) {
            return null;
        }
    }

    public static void printClassLoader() {
        System.out.println("ClassLoaderUtils.printClassLoader");
        ClassLoaderUtils.printClassLoader(Thread.currentThread().getContextClassLoader());
    }

    public static void printClassLoader(ClassLoader cl) {
        System.out.println("ClassLoaderUtils.printClassLoader(cl = " + cl + ")");
        if (cl != null) {
            ClassLoaderUtils.printClassLoader(cl.getParent());
        }
    }
}

