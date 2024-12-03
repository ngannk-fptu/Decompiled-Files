/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.hazelcast.serialization.OsgiSafe
 */
package com.atlassian.cache.hazelcast;

import com.atlassian.hazelcast.serialization.OsgiSafe;

public final class OsgiSafeUtils {
    private OsgiSafeUtils() {
    }

    public static <T> T unwrap(OsgiSafe<T> value) {
        return (T)(value == null ? null : value.getValue());
    }

    public static <T> OsgiSafe<T> wrap(T value) {
        return new OsgiSafe(value);
    }
}

