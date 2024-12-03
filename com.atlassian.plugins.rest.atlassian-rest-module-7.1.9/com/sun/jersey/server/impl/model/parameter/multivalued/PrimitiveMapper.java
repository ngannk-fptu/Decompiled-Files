/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.parameter.multivalued;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

final class PrimitiveMapper {
    static final Map<Class, Class> primitiveToClassMap = PrimitiveMapper.getPrimitiveToClassMap();
    static final Map<Class, Object> primitiveToDefaultValueMap = PrimitiveMapper.getPrimitiveToDefaultValueMap();

    PrimitiveMapper() {
    }

    private static Map<Class, Class> getPrimitiveToClassMap() {
        WeakHashMap<Class<Comparable<Boolean>>, Class<Double>> m = new WeakHashMap<Class<Comparable<Boolean>>, Class<Double>>();
        m.put(Boolean.TYPE, Boolean.class);
        m.put(Byte.TYPE, Byte.class);
        m.put(Short.TYPE, Short.class);
        m.put(Integer.TYPE, Integer.class);
        m.put(Long.TYPE, Long.class);
        m.put(Float.TYPE, Float.class);
        m.put(Double.TYPE, Double.class);
        return Collections.unmodifiableMap(m);
    }

    private static Map<Class, Object> getPrimitiveToDefaultValueMap() {
        WeakHashMap<Class<Double>, Comparable<Boolean>> m = new WeakHashMap<Class<Double>, Comparable<Boolean>>();
        m.put(Boolean.class, Boolean.valueOf(false));
        m.put(Byte.class, Byte.valueOf((byte)0));
        m.put(Short.class, Short.valueOf((short)0));
        m.put(Integer.class, Integer.valueOf(0));
        m.put(Long.class, Long.valueOf(0L));
        m.put(Float.class, Float.valueOf(0.0f));
        m.put(Double.class, Double.valueOf(0.0));
        return Collections.unmodifiableMap(m);
    }
}

