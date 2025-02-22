/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.getters;

import com.hazelcast.query.QueryConstants;
import com.hazelcast.query.QueryException;
import com.hazelcast.query.impl.AbstractIndex;
import com.hazelcast.query.impl.AttributeType;
import com.hazelcast.query.impl.getters.Getter;
import com.hazelcast.query.impl.getters.GetterFactory;
import com.hazelcast.query.impl.getters.NullGetter;
import com.hazelcast.query.impl.getters.NullMultiValueGetter;
import com.hazelcast.query.impl.getters.SuffixModifierUtils;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ExceptionUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

public final class ReflectionHelper {
    private static final int INITIAL_CAPACITY = 3;

    private ReflectionHelper() {
    }

    public static AttributeType getAttributeType(Class klass) {
        if (klass == String.class) {
            return AttributeType.STRING;
        }
        if (klass == Integer.class) {
            return AttributeType.INTEGER;
        }
        if (klass == Short.class) {
            return AttributeType.SHORT;
        }
        if (klass == Long.class) {
            return AttributeType.LONG;
        }
        if (klass == Boolean.class) {
            return AttributeType.BOOLEAN;
        }
        if (klass == Double.class) {
            return AttributeType.DOUBLE;
        }
        if (klass == BigDecimal.class) {
            return AttributeType.BIG_DECIMAL;
        }
        if (klass == BigInteger.class) {
            return AttributeType.BIG_INTEGER;
        }
        if (klass == Float.class) {
            return AttributeType.FLOAT;
        }
        if (klass == Byte.class) {
            return AttributeType.BYTE;
        }
        if (klass == Character.class) {
            return AttributeType.CHAR;
        }
        if (klass == Timestamp.class) {
            return AttributeType.SQL_TIMESTAMP;
        }
        if (klass == Date.class) {
            return AttributeType.SQL_DATE;
        }
        if (klass == java.util.Date.class) {
            return AttributeType.DATE;
        }
        if (klass.isEnum()) {
            return AttributeType.ENUM;
        }
        if (klass == UUID.class) {
            return AttributeType.UUID;
        }
        return null;
    }

    public static Getter createGetter(Object obj, String attribute) {
        Class targetClazz;
        if (obj == null || obj == AbstractIndex.NULL) {
            return NullGetter.NULL_GETTER;
        }
        Class<Object> clazz = targetClazz = obj.getClass();
        try {
            Getter parent = null;
            ArrayList<String> possibleMethodNames = new ArrayList<String>(3);
            for (String fullname : attribute.split("\\.")) {
                String baseName = SuffixModifierUtils.removeModifierSuffix(fullname);
                String modifier = SuffixModifierUtils.getModifierSuffix(fullname, baseName);
                Getter localGetter = null;
                possibleMethodNames.clear();
                possibleMethodNames.add(baseName);
                String camelName = Character.toUpperCase(baseName.charAt(0)) + baseName.substring(1);
                possibleMethodNames.add("get" + camelName);
                possibleMethodNames.add("is" + camelName);
                if (baseName.equals(QueryConstants.THIS_ATTRIBUTE_NAME.value())) {
                    localGetter = GetterFactory.newThisGetter(parent, obj);
                } else {
                    if (parent != null) {
                        clazz = parent.getReturnType();
                    }
                    for (String methodName : possibleMethodNames) {
                        try {
                            Method method = clazz.getMethod(methodName, new Class[0]);
                            method.setAccessible(true);
                            localGetter = GetterFactory.newMethodGetter(obj, parent, method, modifier);
                            if (localGetter == NullGetter.NULL_GETTER || localGetter == NullMultiValueGetter.NULL_MULTIVALUE_GETTER) {
                                return localGetter;
                            }
                            clazz = method.getReturnType();
                            break;
                        }
                        catch (NoSuchMethodException ignored) {
                            EmptyStatement.ignore(ignored);
                        }
                    }
                    if (localGetter == null) {
                        try {
                            Field field = clazz.getField(baseName);
                            localGetter = GetterFactory.newFieldGetter(obj, parent, field, modifier);
                            if (localGetter == NullGetter.NULL_GETTER || localGetter == NullMultiValueGetter.NULL_MULTIVALUE_GETTER) {
                                return localGetter;
                            }
                            clazz = field.getType();
                        }
                        catch (NoSuchFieldException ignored) {
                            EmptyStatement.ignore(ignored);
                        }
                    }
                    if (localGetter == null) {
                        Class c = clazz;
                        while (!c.isInterface() && !Object.class.equals((Object)c)) {
                            try {
                                Field field = c.getDeclaredField(baseName);
                                field.setAccessible(true);
                                localGetter = GetterFactory.newFieldGetter(obj, parent, field, modifier);
                                if (localGetter == NullGetter.NULL_GETTER || localGetter == NullMultiValueGetter.NULL_MULTIVALUE_GETTER) {
                                    return localGetter;
                                }
                                clazz = field.getType();
                                break;
                            }
                            catch (NoSuchFieldException ignored) {
                                c = c.getSuperclass();
                            }
                        }
                    }
                }
                if (localGetter == null) {
                    throw new IllegalArgumentException("There is no suitable accessor for '" + baseName + "' on class '" + clazz.getName() + "'");
                }
                parent = localGetter;
            }
            Getter getter = parent;
            return getter;
        }
        catch (Throwable e) {
            throw new QueryException(e);
        }
    }

    public static Object extractValue(Object object, String attributeName) throws Exception {
        return ReflectionHelper.createGetter(object, attributeName).getValue(object);
    }

    public static <T> T invokeMethod(Object object, String methodName) throws RuntimeException {
        try {
            Method method = object.getClass().getMethod(methodName, new Class[0]);
            method.setAccessible(true);
            return (T)method.invoke(object, new Object[0]);
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }
}

