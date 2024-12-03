/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.restapi.graphql;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Map;
import java.util.Objects;

public class ReflectionUtil {
    public static Class getClazz(Type type) {
        Objects.requireNonNull(type);
        if (type instanceof ParameterizedType) {
            return (Class)((ParameterizedType)type).getRawType();
        }
        if (type instanceof WildcardType) {
            Type[] upperBounds = ((WildcardType)type).getUpperBounds();
            return upperBounds.length > 0 ? ReflectionUtil.getClazz(upperBounds[0]) : Object.class;
        }
        if (!(type instanceof Class)) {
            return Map.class;
        }
        return (Class)type;
    }
}

