/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Primitives
 *  javax.annotation.Nullable
 */
package com.querydsl.sql;

import com.google.common.primitives.Primitives;
import com.querydsl.core.util.ReflectionUtils;
import com.querydsl.sql.types.BigDecimalType;
import com.querydsl.sql.types.BigIntegerType;
import com.querydsl.sql.types.BlobType;
import com.querydsl.sql.types.BooleanType;
import com.querydsl.sql.types.ByteType;
import com.querydsl.sql.types.BytesType;
import com.querydsl.sql.types.CalendarType;
import com.querydsl.sql.types.CharacterType;
import com.querydsl.sql.types.ClobType;
import com.querydsl.sql.types.CurrencyType;
import com.querydsl.sql.types.DateTimeType;
import com.querydsl.sql.types.DateType;
import com.querydsl.sql.types.DoubleType;
import com.querydsl.sql.types.FloatType;
import com.querydsl.sql.types.IntegerType;
import com.querydsl.sql.types.LocalDateTimeType;
import com.querydsl.sql.types.LocalDateType;
import com.querydsl.sql.types.LocalTimeType;
import com.querydsl.sql.types.LocaleType;
import com.querydsl.sql.types.LongType;
import com.querydsl.sql.types.ObjectType;
import com.querydsl.sql.types.ShortType;
import com.querydsl.sql.types.StringType;
import com.querydsl.sql.types.TimeType;
import com.querydsl.sql.types.TimestampType;
import com.querydsl.sql.types.Type;
import com.querydsl.sql.types.URLType;
import com.querydsl.sql.types.UtilDateType;
import com.querydsl.sql.types.UtilUUIDType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

class JavaTypeMapping {
    private static final Type<Object> DEFAULT = new ObjectType();
    private static final Map<Class<?>, Type<?>> defaultTypes = new HashMap();
    private final Map<Class<?>, Type<?>> typeByClass = new HashMap();
    private final Map<Class<?>, Type<?>> resolvedTypesByClass = new HashMap();
    private final Map<String, Map<String, Type<?>>> typeByColumn = new HashMap();

    JavaTypeMapping() {
    }

    private static void registerDefault(Type<?> type) {
        defaultTypes.put(type.getReturnedClass(), type);
        Class primitive = Primitives.unwrap(type.getReturnedClass());
        if (primitive != null) {
            defaultTypes.put(primitive, type);
        }
    }

    @Nullable
    public Type<?> getType(String table, String column) {
        Map<String, Type<?>> columns = this.typeByColumn.get(table);
        if (columns != null) {
            return columns.get(column);
        }
        return null;
    }

    public <T> Type<T> getType(Class<T> clazz) {
        Type<?> resolvedType = this.resolvedTypesByClass.get(clazz);
        if (resolvedType == null) {
            resolvedType = this.findType(clazz);
            if (resolvedType != null) {
                this.resolvedTypesByClass.put(clazz, resolvedType);
            } else {
                return DEFAULT;
            }
        }
        return resolvedType;
    }

    @Nullable
    private Type<?> findType(Class<?> clazz) {
        Class<?> cl = clazz;
        do {
            if (this.typeByClass.containsKey(cl)) {
                return this.typeByClass.get(cl);
            }
            if (!defaultTypes.containsKey(cl)) continue;
            return defaultTypes.get(cl);
        } while (!(cl = cl.getSuperclass()).equals(Object.class));
        Set<Class<?>> interfaces = ReflectionUtils.getImplementedInterfaces(clazz);
        for (Class<?> itf : interfaces) {
            if (this.typeByClass.containsKey(itf)) {
                return this.typeByClass.get(itf);
            }
            if (!defaultTypes.containsKey(itf)) continue;
            return defaultTypes.get(itf);
        }
        return null;
    }

    public void register(Type<?> type) {
        this.typeByClass.put(type.getReturnedClass(), type);
        Class primitive = Primitives.unwrap(type.getReturnedClass());
        if (primitive != null) {
            this.typeByClass.put(primitive, type);
        }
        this.resolvedTypesByClass.clear();
    }

    public void setType(String table, String column, Type<?> type) {
        Map<String, Type<?>> columns = this.typeByColumn.get(table);
        if (columns == null) {
            columns = new HashMap();
            this.typeByColumn.put(table, columns);
        }
        columns.put(column, type);
    }

    static {
        JavaTypeMapping.registerDefault(new BigIntegerType());
        JavaTypeMapping.registerDefault(new BigDecimalType());
        JavaTypeMapping.registerDefault(new BlobType());
        JavaTypeMapping.registerDefault(new BooleanType());
        JavaTypeMapping.registerDefault(new BytesType());
        JavaTypeMapping.registerDefault(new ByteType());
        JavaTypeMapping.registerDefault(new CharacterType());
        JavaTypeMapping.registerDefault(new CalendarType());
        JavaTypeMapping.registerDefault(new ClobType());
        JavaTypeMapping.registerDefault(new CurrencyType());
        JavaTypeMapping.registerDefault(new DateType());
        JavaTypeMapping.registerDefault(new DoubleType());
        JavaTypeMapping.registerDefault(new FloatType());
        JavaTypeMapping.registerDefault(new IntegerType());
        JavaTypeMapping.registerDefault(new LocaleType());
        JavaTypeMapping.registerDefault(new LongType());
        JavaTypeMapping.registerDefault(new ObjectType());
        JavaTypeMapping.registerDefault(new ShortType());
        JavaTypeMapping.registerDefault(new StringType());
        JavaTypeMapping.registerDefault(new TimestampType());
        JavaTypeMapping.registerDefault(new TimeType());
        JavaTypeMapping.registerDefault(new URLType());
        JavaTypeMapping.registerDefault(new UtilDateType());
        JavaTypeMapping.registerDefault(new UtilUUIDType(false));
        JavaTypeMapping.registerDefault(new DateTimeType());
        JavaTypeMapping.registerDefault(new LocalDateTimeType());
        JavaTypeMapping.registerDefault(new LocalDateType());
        JavaTypeMapping.registerDefault(new LocalTimeType());
        try {
            Class.forName("java.time.Instant");
            JavaTypeMapping.registerDefault((Type)Class.forName("com.querydsl.sql.types.JSR310InstantType").newInstance());
            JavaTypeMapping.registerDefault((Type)Class.forName("com.querydsl.sql.types.JSR310LocalDateTimeType").newInstance());
            JavaTypeMapping.registerDefault((Type)Class.forName("com.querydsl.sql.types.JSR310LocalDateType").newInstance());
            JavaTypeMapping.registerDefault((Type)Class.forName("com.querydsl.sql.types.JSR310LocalTimeType").newInstance());
            JavaTypeMapping.registerDefault((Type)Class.forName("com.querydsl.sql.types.JSR310OffsetDateTimeType").newInstance());
            JavaTypeMapping.registerDefault((Type)Class.forName("com.querydsl.sql.types.JSR310OffsetTimeType").newInstance());
            JavaTypeMapping.registerDefault((Type)Class.forName("com.querydsl.sql.types.JSR310ZonedDateTimeType").newInstance());
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

