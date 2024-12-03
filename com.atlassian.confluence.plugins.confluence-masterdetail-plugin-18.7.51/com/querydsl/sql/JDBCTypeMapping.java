/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  javax.annotation.Nullable
 */
package com.querydsl.sql;

import com.google.common.collect.ImmutableSet;
import com.mysema.commons.lang.Pair;
import com.querydsl.sql.types.Null;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

final class JDBCTypeMapping {
    private static final Set<Integer> NUMERIC_TYPES;
    private static final Map<Integer, Class<?>> defaultTypes;
    private static final Map<Class<?>, Integer> defaultSqlTypes;
    private final Map<Integer, Class<?>> types = new HashMap();
    private final Map<Class<?>, Integer> sqlTypes = new HashMap();
    private final Map<Pair<Integer, Integer>, Class<?>> numericTypes = new HashMap();

    JDBCTypeMapping() {
    }

    private static void registerDefault(int sqlType, Class<?> javaType) {
        defaultTypes.put(sqlType, javaType);
        defaultSqlTypes.put(javaType, sqlType);
    }

    public void register(int sqlType, Class<?> javaType) {
        this.types.put(sqlType, javaType);
        this.sqlTypes.put(javaType, sqlType);
    }

    public void registerNumeric(int total, int decimal, Class<?> javaType) {
        this.numericTypes.put(Pair.of(total, decimal), javaType);
    }

    private static Class<?> getNumericClass(int total, int decimal) {
        if (decimal <= 0) {
            if (total > 18 || total == 0) {
                return BigInteger.class;
            }
            if (total > 9) {
                return Long.class;
            }
            if (total > 4) {
                return Integer.class;
            }
            if (total > 2) {
                return Short.class;
            }
            return Byte.class;
        }
        return BigDecimal.class;
    }

    @Nullable
    public Class<?> get(int sqlType, int total, int decimal) {
        if (NUMERIC_TYPES.contains(sqlType)) {
            Pair<Integer, Integer> key = Pair.of(total, decimal);
            if (this.numericTypes.containsKey(key)) {
                return this.numericTypes.get(key);
            }
            if (sqlType == 2 || sqlType == 3) {
                return JDBCTypeMapping.getNumericClass(total, decimal);
            }
        }
        if (this.types.containsKey(sqlType)) {
            return this.types.get(sqlType);
        }
        return defaultTypes.get(sqlType);
    }

    @Nullable
    public Integer get(Class<?> clazz) {
        if (this.sqlTypes.containsKey(clazz)) {
            return this.sqlTypes.get(clazz);
        }
        return defaultSqlTypes.get(clazz);
    }

    static {
        defaultTypes = new HashMap();
        defaultSqlTypes = new HashMap();
        JDBCTypeMapping.registerDefault(-101, Object.class);
        JDBCTypeMapping.registerDefault(-102, Timestamp.class);
        JDBCTypeMapping.registerDefault(2012, Object.class);
        JDBCTypeMapping.registerDefault(2013, Time.class);
        JDBCTypeMapping.registerDefault(2014, Timestamp.class);
        JDBCTypeMapping.registerDefault(-7, Boolean.class);
        JDBCTypeMapping.registerDefault(16, Boolean.class);
        JDBCTypeMapping.registerDefault(-5, Long.class);
        JDBCTypeMapping.registerDefault(3, BigDecimal.class);
        JDBCTypeMapping.registerDefault(8, Double.class);
        JDBCTypeMapping.registerDefault(6, Float.class);
        JDBCTypeMapping.registerDefault(4, Integer.class);
        JDBCTypeMapping.registerDefault(2, BigDecimal.class);
        JDBCTypeMapping.registerDefault(7, Float.class);
        JDBCTypeMapping.registerDefault(5, Short.class);
        JDBCTypeMapping.registerDefault(-6, Byte.class);
        JDBCTypeMapping.registerDefault(91, Date.class);
        JDBCTypeMapping.registerDefault(92, Time.class);
        JDBCTypeMapping.registerDefault(93, Timestamp.class);
        JDBCTypeMapping.registerDefault(-15, String.class);
        JDBCTypeMapping.registerDefault(1, String.class);
        JDBCTypeMapping.registerDefault(2011, String.class);
        JDBCTypeMapping.registerDefault(2005, String.class);
        JDBCTypeMapping.registerDefault(-16, String.class);
        JDBCTypeMapping.registerDefault(-1, String.class);
        JDBCTypeMapping.registerDefault(2009, String.class);
        JDBCTypeMapping.registerDefault(-9, String.class);
        JDBCTypeMapping.registerDefault(12, String.class);
        JDBCTypeMapping.registerDefault(-2, byte[].class);
        JDBCTypeMapping.registerDefault(-4, byte[].class);
        JDBCTypeMapping.registerDefault(-3, byte[].class);
        JDBCTypeMapping.registerDefault(2004, Blob.class);
        JDBCTypeMapping.registerDefault(2003, Object[].class);
        JDBCTypeMapping.registerDefault(2001, Object.class);
        JDBCTypeMapping.registerDefault(70, Object.class);
        JDBCTypeMapping.registerDefault(2000, Object.class);
        JDBCTypeMapping.registerDefault(0, Null.class);
        JDBCTypeMapping.registerDefault(1111, Object.class);
        JDBCTypeMapping.registerDefault(2006, Object.class);
        JDBCTypeMapping.registerDefault(-8, Object.class);
        JDBCTypeMapping.registerDefault(2002, Object.class);
        ImmutableSet.Builder builder = ImmutableSet.builder();
        for (Map.Entry<Integer, Class<?>> entry : defaultTypes.entrySet()) {
            if (!Number.class.isAssignableFrom(entry.getValue())) continue;
            builder.add((Object)entry.getKey());
        }
        NUMERIC_TYPES = builder.build();
    }
}

