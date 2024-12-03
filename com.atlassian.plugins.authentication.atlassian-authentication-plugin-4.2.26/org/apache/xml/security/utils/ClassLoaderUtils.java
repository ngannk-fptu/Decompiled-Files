/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClassLoaderUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ClassLoaderUtils.class);

    private ClassLoaderUtils() {
    }

    public static URL getResource(String resourceName, Class<?> callingClass) {
        ClassLoader cl;
        ClassLoader cluClassloader;
        if (resourceName == null) {
            throw new NullPointerException();
        }
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        if (url == null && resourceName.charAt(0) == '/') {
            url = Thread.currentThread().getContextClassLoader().getResource(resourceName.substring(1));
        }
        if ((cluClassloader = ClassLoaderUtils.class.getClassLoader()) == null) {
            cluClassloader = ClassLoader.getSystemClassLoader();
        }
        if (url == null) {
            url = cluClassloader.getResource(resourceName);
        }
        if (url == null && resourceName.charAt(0) == '/') {
            url = cluClassloader.getResource(resourceName.substring(1));
        }
        if (url == null && (cl = callingClass.getClassLoader()) != null) {
            url = cl.getResource(resourceName);
        }
        if (url == null) {
            url = callingClass.getResource(resourceName);
        }
        if (url == null && resourceName.charAt(0) != '/') {
            return ClassLoaderUtils.getResource('/' + resourceName, callingClass);
        }
        return url;
    }

    public static List<URL> getResources(String resourceName, Class<?> callingClass) {
        URL url;
        ClassLoader cl;
        ClassLoader cluClassloader;
        if (resourceName == null) {
            throw new NullPointerException();
        }
        ArrayList<URL> ret = new ArrayList<URL>();
        Enumeration<URL> urls = new Enumeration<URL>(){

            @Override
            public boolean hasMoreElements() {
                return false;
            }

            @Override
            public URL nextElement() {
                return null;
            }
        };
        try {
            urls = Thread.currentThread().getContextClassLoader().getResources(resourceName);
        }
        catch (IOException e) {
            LOG.debug(e.getMessage(), (Throwable)e);
        }
        if (!urls.hasMoreElements() && resourceName.charAt(0) == '/') {
            try {
                urls = Thread.currentThread().getContextClassLoader().getResources(resourceName.substring(1));
            }
            catch (IOException e) {
                LOG.debug(e.getMessage(), (Throwable)e);
            }
        }
        if ((cluClassloader = ClassLoaderUtils.class.getClassLoader()) == null) {
            cluClassloader = ClassLoader.getSystemClassLoader();
        }
        if (!urls.hasMoreElements()) {
            try {
                urls = cluClassloader.getResources(resourceName);
            }
            catch (IOException e) {
                LOG.debug(e.getMessage(), (Throwable)e);
            }
        }
        if (!urls.hasMoreElements() && resourceName.charAt(0) == '/') {
            try {
                urls = cluClassloader.getResources(resourceName.substring(1));
            }
            catch (IOException e) {
                LOG.debug(e.getMessage(), (Throwable)e);
            }
        }
        if (!urls.hasMoreElements() && (cl = callingClass.getClassLoader()) != null) {
            try {
                urls = cl.getResources(resourceName);
            }
            catch (IOException e) {
                LOG.debug(e.getMessage(), (Throwable)e);
            }
        }
        if (!urls.hasMoreElements() && (url = callingClass.getResource(resourceName)) != null) {
            ret.add(url);
        }
        while (urls.hasMoreElements()) {
            ret.add(urls.nextElement());
        }
        if (ret.isEmpty() && resourceName.charAt(0) != '/') {
            return ClassLoaderUtils.getResources('/' + resourceName, callingClass);
        }
        return ret;
    }

    public static InputStream getResourceAsStream(String resourceName, Class<?> callingClass) {
        URL url = ClassLoaderUtils.getResource(resourceName, callingClass);
        try {
            return url != null ? url.openStream() : null;
        }
        catch (IOException e) {
            LOG.debug(e.getMessage(), (Throwable)e);
            return null;
        }
    }

    public static Class<?> loadClass(String className, Class<?> callingClass) throws ClassNotFoundException {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl != null) {
                return cl.loadClass(className);
            }
        }
        catch (ClassNotFoundException e) {
            LOG.debug(e.getMessage(), (Throwable)e);
        }
        return ClassLoaderUtils.loadClass2(className, callingClass);
    }

    private static Class<?> loadClass2(String className, Class<?> callingClass) throws ClassNotFoundException {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException ex) {
            block5: {
                try {
                    if (ClassLoaderUtils.class.getClassLoader() != null) {
                        return ClassLoaderUtils.class.getClassLoader().loadClass(className);
                    }
                }
                catch (ClassNotFoundException exc) {
                    if (callingClass == null || callingClass.getClassLoader() == null) break block5;
                    return callingClass.getClassLoader().loadClass(className);
                }
            }
            LOG.debug(ex.getMessage(), (Throwable)ex);
            throw ex;
        }
    }
}

