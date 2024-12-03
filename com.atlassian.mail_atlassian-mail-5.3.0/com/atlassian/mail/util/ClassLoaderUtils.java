/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mail.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ClassLoaderUtils {
    public static Class loadClass(String className, Class callingClass) throws ClassNotFoundException {
        return ClassLoaderUtils.loadClass(className, callingClass.getClassLoader());
    }

    public static Class loadClass(String className, ClassLoader callingClassLoader) throws ClassNotFoundException {
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
                    return callingClassLoader.loadClass(className);
                }
            }
        }
    }

    public static URL getResource(String resourceName, Class callingClass) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        if (url == null) {
            url = ClassLoaderUtils.class.getClassLoader().getResource(resourceName);
        }
        if (url == null) {
            url = callingClass.getClassLoader().getResource(resourceName);
        }
        return url;
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

