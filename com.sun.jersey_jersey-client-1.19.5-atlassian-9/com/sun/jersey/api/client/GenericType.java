/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.core.reflection.ReflectionHelper
 */
package com.sun.jersey.api.client;

import com.sun.jersey.core.reflection.ReflectionHelper;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericType<T> {
    private final Type t;
    private final Class c;

    protected GenericType() {
        Type superclass = this.getClass().getGenericSuperclass();
        if (!(superclass instanceof ParameterizedType)) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType)superclass;
        this.t = parameterized.getActualTypeArguments()[0];
        this.c = GenericType.getClass(this.t);
    }

    public GenericType(Type genericType) {
        if (genericType == null) {
            throw new IllegalArgumentException("Type must not be null");
        }
        this.t = genericType;
        this.c = GenericType.getClass(this.t);
    }

    private static Class getClass(Type type) {
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            if (parameterizedType.getRawType() instanceof Class) {
                return (Class)parameterizedType.getRawType();
            }
        } else if (type instanceof GenericArrayType) {
            GenericArrayType array = (GenericArrayType)type;
            return ReflectionHelper.getArrayClass((Class)((Class)((ParameterizedType)array.getGenericComponentType()).getRawType()));
        }
        throw new IllegalArgumentException("Type parameter not a class or parameterized type whose raw type is a class");
    }

    public final Type getType() {
        return this.t;
    }

    public final Class<T> getRawClass() {
        return this.c;
    }
}

