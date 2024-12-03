/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.VCacheException
 */
package com.atlassian.vcache.internal.core.service;

import com.atlassian.vcache.VCacheException;
import java.util.Map;
import java.util.Set;

public class FactoryUtils {
    public static <K, V> void verifyFactoryResult(Map<K, V> factoryResult, Set<K> expectedKeys) {
        if (expectedKeys.size() != factoryResult.size()) {
            throw new VCacheException("Factory returned " + factoryResult.size() + " entries when " + expectedKeys.size() + " were expected. Expected keys " + expectedKeys + " but got " + factoryResult.keySet() + ".");
        }
        long numberOfUnknownKeys = factoryResult.keySet().stream().filter(k -> !expectedKeys.contains(k)).count();
        if (numberOfUnknownKeys > 0L) {
            throw new VCacheException("Factory returned the keys " + factoryResult.keySet() + " when expected " + expectedKeys + ".");
        }
    }
}

