/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache;

import java.net.URI;

public final class CacheUtil {
    private CacheUtil() {
    }

    public static String getPrefix(URI uri, ClassLoader classLoader) {
        if (uri == null && classLoader == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (uri != null) {
            sb.append(uri.toASCIIString()).append('/');
        }
        if (classLoader != null) {
            sb.append(classLoader.toString()).append('/');
        }
        return sb.toString();
    }

    public static String getPrefixedCacheName(String name, URI uri, ClassLoader classLoader) {
        String cacheNamePrefix = CacheUtil.getPrefix(uri, classLoader);
        if (cacheNamePrefix != null) {
            return cacheNamePrefix + name;
        }
        return name;
    }

    public static String getDistributedObjectName(String cacheName) {
        return CacheUtil.getDistributedObjectName(cacheName, null, null);
    }

    public static String getDistributedObjectName(String cacheName, URI uri, ClassLoader classLoader) {
        return "/hz/" + CacheUtil.getPrefixedCacheName(cacheName, uri, classLoader);
    }
}

