/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class IndexHeapMemoryCostUtil {
    private static final int BASE_ARRAY_COST = 16;
    private static final int BASE_STRING_COST = 40;
    private static final int BASE_BIG_INTEGER_COST = 56;
    private static final int BASE_BIG_DECIMAL_COST = 40;
    private static final int BASE_CONCURRENT_HASH_MAP_COST = 80;
    private static final int BASE_CONCURRENT_SKIP_LIST_MAP_COST = 48;
    private static final int DATE_COST = 24;
    private static final int SQL_TIMESTAMP_COST = 32;
    private static final int CONCURRENT_HASH_MAP_ENTRY_COST = 32;
    private static final int CONCURRENT_SKIP_LIST_MAP_ENTRY_COST = 24;
    private static final int QUERY_ENTRY_COST = 32;
    private static final int CACHED_QUERYABLE_ENTRY_COST = 40;
    private static final Map<Class, Integer> KNOWN_FINAL_CLASSES_COSTS = new HashMap<Class, Integer>();
    private static final int ROUGH_BIG_INTEGER_COST = 72;
    private static final int ROUGH_BIG_DECIMAL_COST = 112;
    private static final int ROUGH_UNKNOWN_CLASS_COST = 24;

    private IndexHeapMemoryCostUtil() {
    }

    public static long estimateValueCost(Object value) {
        if (value == null) {
            return 0L;
        }
        Class<?> clazz = value.getClass();
        Integer cost = KNOWN_FINAL_CLASSES_COSTS.get(clazz);
        if (cost != null) {
            return cost.intValue();
        }
        if (value instanceof String) {
            return 40L + (long)((String)value).length() * 2L;
        }
        if (value instanceof Timestamp) {
            return 32L;
        }
        if (value instanceof Date) {
            return 24L;
        }
        if (clazz.isEnum()) {
            return 0L;
        }
        if (value instanceof BigDecimal) {
            return 112L;
        }
        if (value instanceof BigInteger) {
            return 72L;
        }
        return 24L;
    }

    public static long estimateMapCost(long size, boolean ordered, boolean usesCachedQueryableEntries) {
        long mapCost = ordered ? 48L + size * 24L : 80L + size * 32L;
        long queryableEntriesCost = usesCachedQueryableEntries ? size * 40L : size * 32L;
        return mapCost + queryableEntriesCost;
    }

    static {
        KNOWN_FINAL_CLASSES_COSTS.put(Boolean.class, 16);
        KNOWN_FINAL_CLASSES_COSTS.put(Character.class, 16);
        KNOWN_FINAL_CLASSES_COSTS.put(Byte.class, 16);
        KNOWN_FINAL_CLASSES_COSTS.put(Short.class, 16);
        KNOWN_FINAL_CLASSES_COSTS.put(Integer.class, 16);
        KNOWN_FINAL_CLASSES_COSTS.put(Long.class, 24);
        KNOWN_FINAL_CLASSES_COSTS.put(Float.class, 16);
        KNOWN_FINAL_CLASSES_COSTS.put(Double.class, 24);
        KNOWN_FINAL_CLASSES_COSTS.put(UUID.class, 32);
    }
}

