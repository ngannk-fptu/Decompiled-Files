/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public class ClassLoaderUtils {
    public static Class loadClass(String className, Class callingClass) throws ClassNotFoundException {
        return ClassLoaderUtils.loadClass(className, callingClass.getClassLoader());
    }

    public static Class loadClass(String className, ClassLoader callingClassLoader) throws ClassNotFoundException {
        try {
            if (Thread.currentThread().getContextClassLoader() == null) {
                throw new ClassNotFoundException();
            }
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
                    return callingClassLoader.loadClass(className);
                }
            }
        }
    }

    public static URL getResource(String resourceName, Class callingClass) {
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

    public static ResourceBundle getBundle(String resourceName, Locale locale, Class callingClass) {
        ResourceBundle bundle = null;
        if (Thread.currentThread().getContextClassLoader() != null) {
            bundle = ResourceBundle.getBundle(resourceName, locale, Thread.currentThread().getContextClassLoader());
        }
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(resourceName, locale, ClassLoaderUtils.class.getClassLoader());
        }
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(resourceName, locale, callingClass.getClassLoader());
        }
        return bundle;
    }

    public static Enumeration getResources(String resourceName, Class callingClass) throws IOException {
        Enumeration<URL> urls = null;
        if (Thread.currentThread().getContextClassLoader() != null) {
            urls = Thread.currentThread().getContextClassLoader().getResources(resourceName);
        }
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
}

