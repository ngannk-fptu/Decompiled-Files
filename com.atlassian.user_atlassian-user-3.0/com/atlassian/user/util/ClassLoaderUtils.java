/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

public class ClassLoaderUtils {
    public static Class loadClass(String className, Class callingClass) throws ClassNotFoundException {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException e) {
            try {
                return Class.forName(className);
            }
            catch (ClassNotFoundException ex) {
                try {
                    return ClassLoaderUtils.class.getClassLoader().loadClass(className);
                }
                catch (ClassNotFoundException exc) {
                    return callingClass.getClassLoader().loadClass(className);
                }
            }
        }
    }

    public static URL getResource(String resourceName, Class callingClass) {
        URL url = null;
        url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        if (url == null) {
            url = ClassLoaderUtils.class.getClassLoader().getResource(resourceName);
        }
        if (url == null) {
            url = callingClass.getClassLoader().getResource(resourceName);
        }
        return url;
    }

    public static Enumeration getResources(String resourceName, Class callingClass) throws IOException {
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(resourceName);
        if (urls == null && (urls = ClassLoaderUtils.class.getClassLoader().getResources(resourceName)) == null) {
            urls = callingClass.getClassLoader().getResources(resourceName);
        }
        return urls;
    }

    public static InputStream getResourceAsStream(String resourceName, Class callingClass) {
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

