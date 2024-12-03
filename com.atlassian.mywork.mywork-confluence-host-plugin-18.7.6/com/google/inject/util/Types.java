/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.util;

import com.google.inject.Provider;
import com.google.inject.internal.MoreTypes;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Types {
    private Types() {
    }

    public static ParameterizedType newParameterizedType(Type rawType, Type ... typeArguments) {
        return Types.newParameterizedTypeWithOwner(null, rawType, typeArguments);
    }

    public static ParameterizedType newParameterizedTypeWithOwner(Type ownerType, Type rawType, Type ... typeArguments) {
        return new MoreTypes.ParameterizedTypeImpl(ownerType, rawType, typeArguments);
    }

    public static GenericArrayType arrayOf(Type componentType) {
        return new MoreTypes.GenericArrayTypeImpl(componentType);
    }

    public static WildcardType subtypeOf(Type bound) {
        return new MoreTypes.WildcardTypeImpl(new Type[]{bound}, MoreTypes.EMPTY_TYPE_ARRAY);
    }

    public static WildcardType supertypeOf(Type bound) {
        return new MoreTypes.WildcardTypeImpl(new Type[]{Object.class}, new Type[]{bound});
    }

    public static ParameterizedType listOf(Type elementType) {
        return Types.newParameterizedType(List.class, new Type[]{elementType});
    }

    public static ParameterizedType setOf(Type elementType) {
        return Types.newParameterizedType(Set.class, new Type[]{elementType});
    }

    public static ParameterizedType mapOf(Type keyType, Type valueType) {
        return Types.newParameterizedType(Map.class, new Type[]{keyType, valueType});
    }

    public static ParameterizedType providerOf(Type providedType) {
        return Types.newParameterizedType(Provider.class, new Type[]{providedType});
    }
}

