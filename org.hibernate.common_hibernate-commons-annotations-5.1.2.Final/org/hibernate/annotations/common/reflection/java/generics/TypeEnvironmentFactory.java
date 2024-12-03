/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection.java.generics;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.hibernate.annotations.common.reflection.java.generics.ApproximatingTypeEnvironment;
import org.hibernate.annotations.common.reflection.java.generics.CompoundTypeEnvironment;
import org.hibernate.annotations.common.reflection.java.generics.IdentityTypeEnvironment;
import org.hibernate.annotations.common.reflection.java.generics.SimpleTypeEnvironment;
import org.hibernate.annotations.common.reflection.java.generics.TypeEnvironment;
import org.hibernate.annotations.common.reflection.java.generics.TypeSwitch;

public final class TypeEnvironmentFactory {
    private TypeEnvironmentFactory() {
    }

    public static TypeEnvironment getEnvironment(Class context) {
        if (context == null) {
            return IdentityTypeEnvironment.INSTANCE;
        }
        return TypeEnvironmentFactory.createEnvironment(context);
    }

    public static TypeEnvironment getEnvironment(Type context) {
        if (context == null) {
            return IdentityTypeEnvironment.INSTANCE;
        }
        return TypeEnvironmentFactory.createEnvironment(context);
    }

    public static TypeEnvironment getEnvironment(Type t, TypeEnvironment context) {
        return CompoundTypeEnvironment.create(TypeEnvironmentFactory.getEnvironment(t), context);
    }

    public static TypeEnvironment toApproximatingEnvironment(TypeEnvironment context) {
        return CompoundTypeEnvironment.create(new ApproximatingTypeEnvironment(), context);
    }

    private static TypeEnvironment createEnvironment(Type context) {
        return (TypeEnvironment)new TypeSwitch<TypeEnvironment>(){

            @Override
            public TypeEnvironment caseClass(Class classType) {
                return CompoundTypeEnvironment.create(TypeEnvironmentFactory.createSuperTypeEnvironment(classType), TypeEnvironmentFactory.getEnvironment(classType.getSuperclass()));
            }

            @Override
            public TypeEnvironment caseParameterizedType(ParameterizedType parameterizedType) {
                return TypeEnvironmentFactory.createEnvironment(parameterizedType);
            }

            @Override
            public TypeEnvironment defaultCase(Type t) {
                throw new IllegalArgumentException("Invalid type for generating environment: " + t);
            }
        }.doSwitch(context);
    }

    private static TypeEnvironment createSuperTypeEnvironment(Class clazz) {
        Class superclass = clazz.getSuperclass();
        if (superclass == null) {
            return IdentityTypeEnvironment.INSTANCE;
        }
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof Class) {
            return IdentityTypeEnvironment.INSTANCE;
        }
        if (genericSuperclass instanceof ParameterizedType) {
            Type[] formalArgs = superclass.getTypeParameters();
            Type[] actualArgs = ((ParameterizedType)genericSuperclass).getActualTypeArguments();
            return new SimpleTypeEnvironment(formalArgs, actualArgs);
        }
        throw new AssertionError((Object)"Should be unreachable");
    }

    private static TypeEnvironment createEnvironment(ParameterizedType t) {
        Type[] tactuals = t.getActualTypeArguments();
        Type rawType = t.getRawType();
        if (rawType instanceof Class) {
            Type[] tparms = ((Class)rawType).getTypeParameters();
            return new SimpleTypeEnvironment(tparms, tactuals);
        }
        return IdentityTypeEnvironment.INSTANCE;
    }
}

