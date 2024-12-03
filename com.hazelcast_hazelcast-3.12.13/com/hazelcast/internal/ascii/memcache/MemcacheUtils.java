/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.internal.ascii.memcache.MapNameAndKeyPair;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public final class MemcacheUtils {
    private MemcacheUtils() {
    }

    public static MapNameAndKeyPair parseMemcacheKey(String key) {
        key = MemcacheUtils.decodeKey(key, "UTF-8");
        String mapName = "hz_memcache_default";
        int index = key.indexOf(58);
        if (index != -1) {
            mapName = "hz_memcache_" + key.substring(0, index);
            key = key.substring(index + 1);
        }
        return new MapNameAndKeyPair(mapName, key);
    }

    static String decodeKey(String key, String encoding) {
        try {
            return URLDecoder.decode(key, encoding);
        }
        catch (UnsupportedEncodingException e) {
            throw new HazelcastException(e);
        }
    }
}

