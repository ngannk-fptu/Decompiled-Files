/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection.java.generics;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import org.hibernate.annotations.common.reflection.java.generics.TypeEnvironment;
import org.hibernate.annotations.common.reflection.java.generics.TypeFactory;
import org.hibernate.annotations.common.reflection.java.generics.TypeSwitch;

final class SimpleTypeEnvironment
extends HashMap<Type, Type>
implements TypeEnvironment {
    private static final long serialVersionUID = 1L;
    private final TypeSwitch<Type> substitute = new TypeSwitch<Type>(){

        @Override
        public Type caseClass(Class classType) {
            return classType;
        }

        @Override
        public Type caseGenericArrayType(GenericArrayType genericArrayType) {
            Type boundComponentType;
            Type originalComponentType = genericArrayType.getGenericComponentType();
            if (originalComponentType == (boundComponentType = SimpleTypeEnvironment.this.bind(originalComponentType))) {
                return genericArrayType;
            }
            return TypeFactory.createArrayType(boundComponentType);
        }

        @Override
        public Type caseParameterizedType(ParameterizedType parameterizedType) {
            Object[] boundArguments;
            Object[] originalArguments = parameterizedType.getActualTypeArguments();
            if (this.areSame(originalArguments, boundArguments = SimpleTypeEnvironment.this.substitute((Type[])originalArguments))) {
                return parameterizedType;
            }
            return TypeFactory.createParameterizedType(parameterizedType.getRawType(), (Type[])boundArguments, parameterizedType.getOwnerType());
        }

        private boolean areSame(Object[] array1, Object[] array2) {
            if (array1.length != array2.length) {
                return false;
            }
            for (int i = 0; i < array1.length; ++i) {
                if (array1[i] == array2[i]) continue;
                return false;
            }
            return true;
        }

        @Override
        public Type caseTypeVariable(TypeVariable typeVariable) {
            if (!SimpleTypeEnvironment.this.containsKey(typeVariable)) {
                return typeVariable;
            }
            return (Type)SimpleTypeEnvironment.this.get(typeVariable);
        }

        @Override
        public Type caseWildcardType(WildcardType wildcardType) {
            return wildcardType;
        }
    };

    public SimpleTypeEnvironment(Type[] formalTypeArgs, Type[] actualTypeArgs) {
        for (int i = 0; i < formalTypeArgs.length; ++i) {
            this.put(formalTypeArgs[i], actualTypeArgs[i]);
        }
    }

    @Override
    public Type bind(Type type) {
        return this.substitute.doSwitch(type);
    }

    private Type[] substitute(Type[] types) {
        Type[] substTypes = new Type[types.length];
        for (int i = 0; i < substTypes.length; ++i) {
            substTypes[i] = this.bind(types[i]);
        }
        return substTypes;
    }
}

