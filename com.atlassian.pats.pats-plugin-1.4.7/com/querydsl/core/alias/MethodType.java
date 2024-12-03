/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.querydsl.core.alias;

import com.querydsl.core.alias.ManagedObject;
import com.querydsl.core.types.EntityPath;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public enum MethodType {
    GET_MAPPED_PATH("__mappedPath", EntityPath.class, ManagedObject.class, new Class[0]),
    GETTER("(get|is).+", Object.class, Object.class, new Class[0]),
    HASH_CODE("hashCode", Integer.TYPE, Object.class, new Class[0]),
    LIST_ACCESS("get", Object.class, List.class, Integer.TYPE),
    MAP_ACCESS("get", Object.class, Map.class, Object.class),
    SIZE("size", Integer.TYPE, Object.class, new Class[0]),
    TO_STRING("toString", String.class, Object.class, new Class[0]),
    SCALA_GETTER(".+", Object.class, Object.class, new Class[0]),
    SCALA_LIST_ACCESS("apply", Object.class, Object.class, Integer.TYPE),
    SCALA_MAP_ACCESS("apply", Object.class, Object.class, Object.class);

    private final Pattern pattern;
    private final Class<?> returnType;
    private final Class<?> ownerType;
    private final Class<?>[] paramTypes;

    private MethodType(String namePattern, Class<?> returnType, Class<?> ownerType, Class<?> ... paramTypes) {
        this.pattern = Pattern.compile(namePattern);
        this.returnType = returnType;
        this.ownerType = ownerType;
        this.paramTypes = paramTypes;
    }

    @Nullable
    public static MethodType get(Method method) {
        for (MethodType methodType : MethodType.values()) {
            if (!methodType.pattern.matcher(method.getName()).matches() || methodType.returnType != Object.class && !methodType.returnType.isAssignableFrom(method.getReturnType()) || methodType.ownerType != Object.class && !methodType.ownerType.isAssignableFrom(method.getDeclaringClass()) || !Arrays.equals(methodType.paramTypes, method.getParameterTypes())) continue;
            return methodType;
        }
        return null;
    }
}

