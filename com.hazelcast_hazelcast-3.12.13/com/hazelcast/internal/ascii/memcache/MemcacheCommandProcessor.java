/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.AbstractTextCommandProcessor;
import com.hazelcast.internal.ascii.TextCommandService;

public abstract class MemcacheCommandProcessor<T>
extends AbstractTextCommandProcessor<T> {
    public static final String MAP_NAME_PREFIX = "hz_memcache_";
    public static final String DEFAULT_MAP_NAME = "hz_memcache_default";

    protected MemcacheCommandProcessor(TextCommandService textCommandService) {
        super(textCommandService);
    }

    public static byte[] longToByteArray(long v) {
        long paramV = v;
        int len = (int)(paramV / 256L) + 1;
        byte[] bytes = new byte[len];
        for (int i = len - 1; i >= 0; --i) {
            long t = paramV % 256L;
            bytes[i] = t < 128L ? (byte)t : (byte)(t - 256L);
            paramV = (paramV - t) / 256L;
        }
        return bytes;
    }

    public static int byteArrayToLong(byte[] v) {
        if (v.length > 8) {
            return -1;
        }
        int r = 0;
        for (int n : v) {
            int t = n;
            t = t >= 0 ? t : t + 256;
            r = r * 256 + t;
        }
        return r;
    }

    public static byte[] concatenate(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}

