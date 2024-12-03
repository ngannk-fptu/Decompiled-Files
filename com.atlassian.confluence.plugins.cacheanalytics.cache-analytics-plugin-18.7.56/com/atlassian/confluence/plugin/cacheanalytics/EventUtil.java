/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.cacheanalytics;

import java.util.stream.IntStream;

public final class EventUtil {
    public static int simpleHash(String cacheName) {
        return IntStream.range(0, cacheName.length()).map(cacheName::charAt).reduce(0, Math::addExact);
    }
}

