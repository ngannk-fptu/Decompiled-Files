/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection.java.generics;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

class TypeFactory {
    TypeFactory() {
    }

    static ParameterizedType createParameterizedType(final Type rawType, final Type[] substTypeArgs, final Type ownerType) {
        return new ParameterizedType(){

            @Override
            public Type[] getActualTypeArguments() {
                return substTypeArgs;
            }

            @Override
            public Type getRawType() {
                return rawType;
            }

            @Override
            public Type getOwnerType() {
                return ownerType;
            }

            public boolean equals(Object obj) {
                if (!(obj instanceof ParameterizedType)) {
                    return false;
                }
                ParameterizedType other = (ParameterizedType)obj;
                return Arrays.equals(this.getActualTypeArguments(), other.getActualTypeArguments()) && TypeFactory.safeEquals(this.getRawType(), other.getRawType()) && TypeFactory.safeEquals(this.getOwnerType(), other.getOwnerType());
            }

            public int hashCode() {
                return TypeFactory.safeHashCode(this.getActualTypeArguments()) ^ TypeFactory.safeHashCode(this.getRawType()) ^ TypeFactory.safeHashCode(this.getOwnerType());
            }
        };
    }

    static Type createArrayType(Type componentType) {
        if (componentType instanceof Class) {
            return Array.newInstance((Class)componentType, 0).getClass();
        }
        return TypeFactory.createGenericArrayType(componentType);
    }

    private static GenericArrayType createGenericArrayType(final Type componentType) {
        return new GenericArrayType(){

            @Override
            public Type getGenericComponentType() {
                return componentType;
            }

            public boolean equals(Object obj) {
                if (!(obj instanceof GenericArrayType)) {
                    return false;
                }
                GenericArrayType other = (GenericArrayType)obj;
                return TypeFactory.safeEquals(this.getGenericComponentType(), other.getGenericComponentType());
            }

            public int hashCode() {
                return TypeFactory.safeHashCode(this.getGenericComponentType());
            }
        };
    }

    private static boolean safeEquals(Type t1, Type t2) {
        if (t1 == null) {
            return t2 == null;
        }
        return t1.equals(t2);
    }

    private static int safeHashCode(Object o) {
        if (o == null) {
            return 1;
        }
        return o.hashCode();
    }
}

