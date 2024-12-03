/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection.java.generics;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import org.hibernate.annotations.common.reflection.java.generics.TypeEnvironment;
import org.hibernate.annotations.common.reflection.java.generics.TypeFactory;
import org.hibernate.annotations.common.reflection.java.generics.TypeSwitch;
import org.hibernate.annotations.common.reflection.java.generics.TypeUtils;

final class ApproximatingTypeEnvironment
implements TypeEnvironment {
    ApproximatingTypeEnvironment() {
    }

    @Override
    public Type bind(Type type) {
        Type result = this.fineApproximation(type);
        assert (TypeUtils.isResolved(result));
        return result;
    }

    private Type fineApproximation(Type type) {
        return (Type)new TypeSwitch<Type>(){

            @Override
            public Type caseWildcardType(WildcardType wildcardType) {
                return wildcardType;
            }

            @Override
            public Type caseClass(Class classType) {
                return classType;
            }

            @Override
            public Type caseGenericArrayType(GenericArrayType genericArrayType) {
                if (TypeUtils.isResolved(genericArrayType)) {
                    return genericArrayType;
                }
                Type componentType = genericArrayType.getGenericComponentType();
                Type boundComponentType = ApproximatingTypeEnvironment.this.bind(componentType);
                if (boundComponentType instanceof Class) {
                    return Array.newInstance((Class)boundComponentType, 0).getClass();
                }
                return Object[].class;
            }

            @Override
            public Type caseParameterizedType(ParameterizedType parameterizedType) {
                if (TypeUtils.isResolved(parameterizedType)) {
                    return parameterizedType;
                }
                if (!TypeUtils.isCollection(parameterizedType)) {
                    return Object.class;
                }
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                Type[] approximatedTypeArguments = new Type[typeArguments.length];
                for (int i = 0; i < typeArguments.length; ++i) {
                    approximatedTypeArguments[i] = ApproximatingTypeEnvironment.this.coarseApproximation(typeArguments[i]);
                }
                return TypeFactory.createParameterizedType(ApproximatingTypeEnvironment.this.bind(parameterizedType.getRawType()), approximatedTypeArguments, parameterizedType.getOwnerType());
            }

            @Override
            public Type defaultCase(Type t) {
                return ApproximatingTypeEnvironment.this.coarseApproximation(t);
            }
        }.doSwitch(type);
    }

    private Type coarseApproximation(Type type) {
        Type result = (Type)new TypeSwitch<Type>(){

            @Override
            public Type caseWildcardType(WildcardType wildcardType) {
                return this.approximateTo(wildcardType.getUpperBounds());
            }

            @Override
            public Type caseGenericArrayType(GenericArrayType genericArrayType) {
                if (TypeUtils.isResolved(genericArrayType)) {
                    return genericArrayType;
                }
                return Object[].class;
            }

            @Override
            public Type caseParameterizedType(ParameterizedType parameterizedType) {
                if (TypeUtils.isResolved(parameterizedType)) {
                    return parameterizedType;
                }
                return Object.class;
            }

            @Override
            public Type caseTypeVariable(TypeVariable typeVariable) {
                return this.approximateTo(typeVariable.getBounds());
            }

            private Type approximateTo(Type[] bounds) {
                if (bounds.length != 1) {
                    return Object.class;
                }
                return ApproximatingTypeEnvironment.this.coarseApproximation(bounds[0]);
            }

            @Override
            public Type defaultCase(Type t) {
                return t;
            }
        }.doSwitch(type);
        assert (TypeUtils.isResolved(result));
        return result;
    }

    public String toString() {
        return "approximated_types";
    }
}

