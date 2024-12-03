/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang3;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class ClassLoaderUtils {
    private static final URL[] EMPTY_URL_ARRAY = new URL[0];

    public static URL[] getSystemURLs() {
        return ClassLoaderUtils.getURLs(ClassLoader.getSystemClassLoader());
    }

    public static URL[] getThreadURLs() {
        return ClassLoaderUtils.getURLs(Thread.currentThread().getContextClassLoader());
    }

    private static URL[] getURLs(ClassLoader cl) {
        return cl instanceof URLClassLoader ? ((URLClassLoader)cl).getURLs() : EMPTY_URL_ARRAY;
    }

    public static String toString(ClassLoader classLoader) {
        if (classLoader instanceof URLClassLoader) {
            return ClassLoaderUtils.toString((URLClassLoader)classLoader);
        }
        return classLoader.toString();
    }

    public static String toString(URLClassLoader classLoader) {
        return classLoader + Arrays.toString(classLoader.getURLs());
    }
}

