/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.metrics.impl;

import com.hazelcast.internal.util.counters.Counter;
import com.hazelcast.util.MapUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

final class ProbeUtils {
    static final int TYPE_PRIMITIVE_LONG = 1;
    static final int TYPE_LONG_NUMBER = 2;
    static final int TYPE_DOUBLE_PRIMITIVE = 3;
    static final int TYPE_DOUBLE_NUMBER = 4;
    static final int TYPE_COLLECTION = 5;
    static final int TYPE_MAP = 6;
    static final int TYPE_COUNTER = 7;
    static final int TYPE_SEMAPHORE = 8;
    private static final Map<Class<?>, Integer> TYPES;

    private ProbeUtils() {
    }

    static boolean isDouble(int type) {
        return type == 3 || type == 4;
    }

    static int getType(Class classType) {
        Integer type = TYPES.get(classType);
        if (type != null) {
            return type;
        }
        ArrayList flattenedClasses = new ArrayList();
        ProbeUtils.flatten(classType, flattenedClasses);
        for (Class clazz : flattenedClasses) {
            type = TYPES.get(clazz);
            if (type == null) continue;
            return type;
        }
        return -1;
    }

    static void flatten(Class clazz, List<Class<?>> result) {
        if (!result.contains(clazz)) {
            result.add(clazz);
        }
        if (clazz.getSuperclass() != null) {
            ProbeUtils.flatten(clazz.getSuperclass(), result);
        }
        for (Class<?> interfaze : clazz.getInterfaces()) {
            if (!result.contains(interfaze)) {
                result.add(interfaze);
            }
            ProbeUtils.flatten(interfaze, result);
        }
    }

    static {
        Map<Class<Object>, Integer> types = MapUtil.createHashMap(18);
        types.put(Byte.TYPE, 1);
        types.put(Short.TYPE, 1);
        types.put(Integer.TYPE, 1);
        types.put(Long.TYPE, 1);
        types.put(Byte.class, 2);
        types.put(Short.class, 2);
        types.put(Integer.class, 2);
        types.put(Long.class, 2);
        types.put(AtomicInteger.class, 2);
        types.put(AtomicLong.class, 2);
        types.put(Double.TYPE, 3);
        types.put(Float.TYPE, 3);
        types.put(Double.class, 4);
        types.put(Float.class, 4);
        types.put(Collection.class, 5);
        types.put(Map.class, 6);
        types.put(Counter.class, 7);
        types.put(Semaphore.class, 8);
        TYPES = Collections.unmodifiableMap(types);
    }
}

