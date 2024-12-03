/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.util;

import java.lang.ref.SoftReference;

public class StringCache {
    public static final boolean OBJECT_CACHE = StringCache.getBooleanSetting("jackrabbit.stringCache", true);
    public static final int OBJECT_CACHE_SIZE = StringCache.nextPowerOf2(StringCache.getIntSetting("jackrabbit.stringCacheSize", 1024));
    private static SoftReference<String[]> softCache = new SoftReference<Object>(null);

    private StringCache() {
    }

    private static int nextPowerOf2(int x) {
        long i;
        for (i = 1L; i < (long)x && i < 0x3FFFFFFFL; i += i) {
        }
        return (int)i;
    }

    private static boolean getBooleanSetting(String name, boolean defaultValue) {
        String s = StringCache.getProperty(name);
        if (s != null) {
            try {
                return Boolean.valueOf(s);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return defaultValue;
    }

    private static int getIntSetting(String name, int defaultValue) {
        String s = StringCache.getProperty(name);
        if (s != null) {
            try {
                return Integer.decode(s);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return defaultValue;
    }

    private static String getProperty(String name) {
        try {
            return System.getProperty(name);
        }
        catch (Exception e) {
            return null;
        }
    }

    private static String[] getCache() {
        String[] cache;
        if (softCache != null && (cache = softCache.get()) != null) {
            return cache;
        }
        try {
            cache = new String[OBJECT_CACHE_SIZE];
        }
        catch (OutOfMemoryError e) {
            return null;
        }
        softCache = new SoftReference<String[]>(cache);
        return cache;
    }

    public static String cache(String s) {
        if (!OBJECT_CACHE) {
            return s;
        }
        if (s == null) {
            return s;
        }
        if (s.length() == 0) {
            return "";
        }
        int hash = s.hashCode();
        String[] cache = StringCache.getCache();
        if (cache != null) {
            int index = hash & OBJECT_CACHE_SIZE - 1;
            String cached = cache[index];
            if (cached != null && s.equals(cached)) {
                return cached;
            }
            cache[index] = s;
        }
        return s;
    }

    public static String fromCacheOrNew(String s) {
        if (!OBJECT_CACHE) {
            return s;
        }
        if (s == null) {
            return s;
        }
        if (s.length() == 0) {
            return "";
        }
        int hash = s.hashCode();
        String[] cache = StringCache.getCache();
        int index = hash & OBJECT_CACHE_SIZE - 1;
        if (cache == null) {
            return s;
        }
        String cached = cache[index];
        if (cached != null && s.equals(cached)) {
            return cached;
        }
        cache[index] = s = new String(s);
        return s;
    }

    public static void clearCache() {
        softCache = new SoftReference<Object>(null);
    }
}

