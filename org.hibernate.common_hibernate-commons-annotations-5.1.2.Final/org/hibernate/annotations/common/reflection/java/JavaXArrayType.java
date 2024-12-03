/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection.java;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Collection;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
import org.hibernate.annotations.common.reflection.java.JavaXType;
import org.hibernate.annotations.common.reflection.java.generics.TypeEnvironment;
import org.hibernate.annotations.common.reflection.java.generics.TypeSwitch;

final class JavaXArrayType
extends JavaXType {
    public JavaXArrayType(Type type, TypeEnvironment context, JavaReflectionManager factory) {
        super(type, context, factory);
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public XClass getElementClass() {
        return this.toXClass(this.getElementType());
    }

    private Type getElementType() {
        return (Type)new TypeSwitch<Type>(){

            @Override
            public Type caseClass(Class classType) {
                return classType.getComponentType();
            }

            @Override
            public Type caseGenericArrayType(GenericArrayType genericArrayType) {
                return genericArrayType.getGenericComponentType();
            }

            @Override
            public Type defaultCase(Type t) {
                throw new IllegalArgumentException(t + " is not an array type");
            }
        }.doSwitch(this.approximate());
    }

    @Override
    public XClass getClassOrElementClass() {
        return this.getElementClass();
    }

    @Override
    public Class<? extends Collection> getCollectionClass() {
        return null;
    }

    @Override
    public XClass getMapKey() {
        return null;
    }

    @Override
    public XClass getType() {
        Class<? extends Object> boundType = this.getElementType();
        if (boundType instanceof Class) {
            boundType = this.arrayTypeOf(boundType);
        }
        return this.toXClass(boundType);
    }

    private Class<? extends Object> arrayTypeOf(Class componentType) {
        return Array.newInstance(componentType, 0).getClass();
    }
}

