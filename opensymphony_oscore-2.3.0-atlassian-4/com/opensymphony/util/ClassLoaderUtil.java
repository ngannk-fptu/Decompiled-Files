/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ClassLoaderUtil {
    public static URL getResource(String resourceName, Class callingClass) {
        ClassLoader cl;
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        if (url == null) {
            url = ClassLoaderUtil.class.getClassLoader().getResource(resourceName);
        }
        if (url == null && (cl = callingClass.getClassLoader()) != null) {
            url = cl.getResource(resourceName);
        }
        if (url == null && resourceName != null && resourceName.charAt(0) != '/') {
            return ClassLoaderUtil.getResource('/' + resourceName, callingClass);
        }
        return url;
    }

    public static InputStream getResourceAsStream(String resourceName, Class callingClass) {
        URL url = ClassLoaderUtil.getResource(resourceName, callingClass);
        try {
            return url != null ? url.openStream() : null;
        }
        catch (IOException e) {
            return null;
        }
    }

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
                    return ClassLoaderUtil.class.getClassLoader().loadClass(className);
                }
                catch (ClassNotFoundException exc) {
                    return callingClass.getClassLoader().loadClass(className);
                }
            }
        }
    }
}

