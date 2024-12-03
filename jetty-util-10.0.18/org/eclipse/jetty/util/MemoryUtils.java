/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util;

public class MemoryUtils {
    private static final int cacheLineBytes;

    private MemoryUtils() {
    }

    public static int getCacheLineBytes() {
        return cacheLineBytes;
    }

    public static int getIntegersPerCacheLine() {
        return MemoryUtils.getCacheLineBytes() >> 2;
    }

    public static int getLongsPerCacheLine() {
        return MemoryUtils.getCacheLineBytes() >> 3;
    }

    static {
        int defaultValue;
        int value = defaultValue = 64;
        try {
            value = Integer.parseInt(System.getProperty("org.eclipse.jetty.util.cacheLineBytes", String.valueOf(defaultValue)));
        }
        catch (Exception exception) {
            // empty catch block
        }
        cacheLineBytes = value;
    }
}

