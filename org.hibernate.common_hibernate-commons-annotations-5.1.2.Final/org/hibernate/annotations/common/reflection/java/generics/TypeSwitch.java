/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection.java.generics;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public class TypeSwitch<T> {
    public final T doSwitch(Type type) {
        if (type instanceof Class) {
            return this.caseClass((Class)type);
        }
        if (type instanceof GenericArrayType) {
            return this.caseGenericArrayType((GenericArrayType)type);
        }
        if (type instanceof ParameterizedType) {
            return this.caseParameterizedType((ParameterizedType)type);
        }
        if (type instanceof TypeVariable) {
            return this.caseTypeVariable((TypeVariable)type);
        }
        if (type instanceof WildcardType) {
            return this.caseWildcardType((WildcardType)type);
        }
        return this.defaultCase(type);
    }

    public T caseWildcardType(WildcardType wildcardType) {
        return this.defaultCase(wildcardType);
    }

    public T caseTypeVariable(TypeVariable typeVariable) {
        return this.defaultCase(typeVariable);
    }

    public T caseClass(Class classType) {
        return this.defaultCase(classType);
    }

    public T caseGenericArrayType(GenericArrayType genericArrayType) {
        return this.defaultCase(genericArrayType);
    }

    public T caseParameterizedType(ParameterizedType parameterizedType) {
        return this.defaultCase(parameterizedType);
    }

    public T defaultCase(Type t) {
        return null;
    }
}

