/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection.java.generics;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.Map;
import org.hibernate.annotations.common.reflection.java.generics.TypeSwitch;

public class TypeUtils {
    public static boolean isResolved(Type t) {
        return (Boolean)new TypeSwitch<Boolean>(){

            @Override
            public Boolean caseClass(Class classType) {
                return true;
            }

            @Override
            public Boolean caseGenericArrayType(GenericArrayType genericArrayType) {
                return TypeUtils.isResolved(genericArrayType.getGenericComponentType());
            }

            @Override
            public Boolean caseParameterizedType(ParameterizedType parameterizedType) {
                Type[] typeArgs;
                for (Type arg : typeArgs = parameterizedType.getActualTypeArguments()) {
                    if (TypeUtils.isResolved(arg)) continue;
                    return false;
                }
                return TypeUtils.isResolved(parameterizedType.getRawType());
            }

            @Override
            public Boolean caseTypeVariable(TypeVariable typeVariable) {
                return false;
            }

            @Override
            public Boolean caseWildcardType(WildcardType wildcardType) {
                return TypeUtils.areResolved(wildcardType.getUpperBounds()) != false && TypeUtils.areResolved(wildcardType.getLowerBounds()) != false;
            }
        }.doSwitch(t);
    }

    private static Boolean areResolved(Type[] types) {
        for (Type t : types) {
            if (TypeUtils.isResolved(t)) continue;
            return false;
        }
        return true;
    }

    public static Class<? extends Collection> getCollectionClass(Type type) {
        return (Class)new TypeSwitch<Class<? extends Collection>>(){

            @Override
            public Class<? extends Collection> caseClass(Class clazz) {
                return TypeUtils.isCollectionClass(clazz) ? clazz : null;
            }

            @Override
            public Class<? extends Collection> caseParameterizedType(ParameterizedType parameterizedType) {
                return TypeUtils.getCollectionClass(parameterizedType.getRawType());
            }

            @Override
            public Class<? extends Collection> caseWildcardType(WildcardType wildcardType) {
                Type[] upperBounds = wildcardType.getUpperBounds();
                if (upperBounds.length == 0) {
                    return null;
                }
                return TypeUtils.getCollectionClass(upperBounds[0]);
            }

            @Override
            public Class<? extends Collection> defaultCase(Type t) {
                return null;
            }
        }.doSwitch(type);
    }

    private static boolean isCollectionClass(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz);
    }

    public static boolean isSimple(Type type) {
        return (Boolean)new TypeSwitch<Boolean>(){

            @Override
            public Boolean caseClass(Class clazz) {
                return !clazz.isArray() && !TypeUtils.isCollectionClass(clazz);
            }

            @Override
            public Boolean caseParameterizedType(ParameterizedType parameterizedType) {
                return TypeUtils.isSimple(parameterizedType.getRawType());
            }

            @Override
            public Boolean caseWildcardType(WildcardType wildcardType) {
                return TypeUtils.areSimple(wildcardType.getUpperBounds()) != false && TypeUtils.areSimple(wildcardType.getLowerBounds()) != false;
            }

            @Override
            public Boolean defaultCase(Type t) {
                return false;
            }
        }.doSwitch(type);
    }

    private static Boolean areSimple(Type[] types) {
        for (Type t : types) {
            if (TypeUtils.isSimple(t)) continue;
            return false;
        }
        return true;
    }

    public static boolean isVoid(Type type) {
        return Void.TYPE.equals(type);
    }

    public static boolean isArray(Type t) {
        return (Boolean)new TypeSwitch<Boolean>(){

            @Override
            public Boolean caseClass(Class clazz) {
                return clazz.isArray();
            }

            @Override
            public Boolean caseGenericArrayType(GenericArrayType genericArrayType) {
                return TypeUtils.isSimple(genericArrayType.getGenericComponentType());
            }

            @Override
            public Boolean defaultCase(Type type) {
                return false;
            }
        }.doSwitch(t);
    }

    public static boolean isCollection(Type t) {
        return TypeUtils.getCollectionClass(t) != null;
    }
}

